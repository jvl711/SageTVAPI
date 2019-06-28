
package jvl.sage.api.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.File;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import sun.misc.IOUtils;


public class MiniHttpHandler implements HttpHandler
{

    @Override
    public void handle(HttpExchange msg) throws IOException 
    {
        String context = msg.getHttpContext().getPath();
        try
        {
            if(context.equals("/api/v1/test"))
            {
                System.out.println("JVL HTTP Test Called");
                test(msg);
            }
            else if(context.equals("/api/v1/tv/all"))
            {
                System.out.println("JVL HTTP tv/all Called");
                GetAllTVEpisodes(msg);
            }
            else if(context.equals("/api/v1/poster"))
            {
                System.out.println("JVL HTTP Poster Called");
                GetPoster(msg);
            }
            else
            {
                //return error response
            }
        }
        catch(Exception ex)
        {
            //Unhandled exception processing the request
        }
    }
 
    public void test(HttpExchange msg) throws UnsupportedEncodingException, IOException
    {
        OutputStream out = msg.getResponseBody();
        
        
        Map<String, String> query = MiniHttpHandler.ParseQuery(msg);
        
        if(query.containsKey("test"))
        {
            System.out.println("Test: " + query.get("test"));
        }
        
        byte data [] = "This is a test".getBytes("UTF-8");
        msg.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        msg.sendResponseHeaders(200, data.length);
        out.write(data);
        out.close();
    }
    
    public void GetPoster(HttpExchange msg) throws IOException
    {
        System.out.println("JVL HTTP Parse Query: " + msg.getRequestURI().getQuery());
        Map<String, String> query = MiniHttpHandler.ParseQuery(msg);
        
        try
        {
            if(query.containsKey("mediafileid"))
            {
                int mediaFileId = Integer.parseInt(query.get("mediafileid"));
                MediaFile mediaFile = MediaFile.GetMediaFileForID(mediaFileId);
                
                String path = mediaFile.GetShow().GetPoster();
                                
                if(path.length() > 0)
                {
                    File file = new File(path);
                    byte [] data = IOUtils.readFully(new java.io.FileInputStream(file), -1, true);
                    
                    OutputStream out = msg.getResponseBody();
                    msg.getResponseHeaders().add("Content-Type", "image/jpg");
                    msg.sendResponseHeaders(200, data.length);
                    out.write(data);
                    out.close();
                }
            }
            else
            {                
                msg.sendResponseHeaders(400, 0);
                msg.getResponseBody().close();
            }
        }
        catch(Exception ex)
        {
            msg.sendResponseHeaders(500, 0);
            msg.getResponseBody().close();
            System.out.println("Error getting poster: " + ex.getMessage());
        }
    }
    
    public void GetAllTVEpisodes(HttpExchange msg) throws UnsupportedEncodingException, IOException, SageCallApiException
    {
        OutputStream out = msg.getResponseBody();
        byte data[];
        String test = "";
        
        MediaFiles tv = MediaFile.GetTVFiles();
        
        for(int i = 0; i < tv.size(); i++)
        {
            test += tv.get(i).GetMediaFileID() + "," + tv.get(i).GetShow().GetTitle() + "," + tv.get(i).GetShow().GetEpisodeName() + "\n";
        }
        
        data = test.getBytes("UTF-8");
        msg.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        msg.sendResponseHeaders(200, data.length);
        out.write(data);
        out.close();
    }
                
    public static Map<String, String> ParseQuery(HttpExchange msg)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        String [] pairs = msg.getRequestURI().getQuery().split("&");
        
        for(int i = 0; i < pairs.length; i++)
        {
            String [] temp = pairs[i].split("=");
            
            if(temp.length == 2)
            {
                map.put(temp[0].toLowerCase(), temp[1]);
            }
            else
            {
                //Malformed.... Ignore
            }
        }
        
        return map;
    }
    
}

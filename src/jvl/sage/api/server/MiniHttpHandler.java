
package jvl.sage.api.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.OutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;


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
                test(msg);
            }
            else if(context.equals("/api/v1/tv/all"))
            {
                GetAllTVEpisodes(msg);
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
        byte data [] = "This is a test".getBytes("UTF-8");
        msg.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
        msg.sendResponseHeaders(200, data.length);
        out.write(data);
        out.close();
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
            
    
}

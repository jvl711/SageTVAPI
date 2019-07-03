
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
import jvl.sage.api.Airing;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.Show;
import org.json.JSONArray;
import org.json.JSONObject;
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
            msg.sendResponseHeaders(500, 0);
            msg.getResponseBody().close();
            System.out.println("JVL error processing request for context: " + msg.getHttpContext().getPath());
            System.out.println("Error message: " + ex.getMessage());
            
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
                System.out.println("JVL - mediafileid present.  Attempt to get poster");
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
                System.out.println("JVL - mediafileid missing.  Send 400 error");
                msg.sendResponseHeaders(400, -1);
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
        
        JSONObject jsonShows = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        
        for(int i = 0; i < tv.size(); i++)
        {
            JSONObject jsonShow = new JSONObject();
            MediaFile mediaFile = tv.get(i);
            Show show = mediaFile.GetShow();
            Airing airing = mediaFile.GetAiring();
            
            jsonShow.put("MediaFileID", mediaFile.GetMediaFileID());
            jsonShow.put("Show", show.GetTitle());
            jsonShow.put("EpisodeName", show.GetEpisodeName());
            jsonShow.put("Season", show.GetSeasonNumber());
            jsonShow.put("SeasonString", show.GetSeasonNumberString());
            jsonShow.put("EpisodeNumber", show.GetEpisodeNumber());
            jsonShow.put("EpisodeNumberString", show.GetEpisodeNumberString());
            jsonShow.put("Categories", show.GetCategories());
            jsonShow.put("TheMovieDBID", show.GetTheMovieDBID());
            jsonShow.put("Watched", airing.IsWatched());
            
            jsonArray.put(jsonShow);
        }
        
        jsonShows.put("Shows", jsonArray);
        
        data = jsonShows.toString(4).getBytes("UTF-8");
        msg.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
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

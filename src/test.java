
import jvl.sage.api.server.MiniServer;
import org.json.JSONArray;
import org.json.JSONObject;

public class test 
{
    public static void main(String args[])
    {
        /*
        try
        {
            MiniServer server = new MiniServer(8080);
            
            server.start();
            
            System.in.read();
            
            server.stop();
        }
        catch(Exception ex)
        {
            
        }
        */
        
        JSONObject jmain = new JSONObject();
        
        JSONArray jarray = new JSONArray();
        
        for(int i = 0; i < 100; i++)
        {
            JSONObject jobject = new JSONObject();
            
            jobject.put("MediaFileID", i);
            jobject.put("Title", "Title" + i);
            jobject.put("EpisodeName", "EpisodeName" + i);
            jobject.put("Season", i);
            jobject.put("Episode", i);
            
            jarray.put(jobject);
        }
        
        jmain.put("shows", jarray);
        
        System.out.println(jmain.toString(4));
    }
}



import jvl.sage.api.server.MiniServer;

public class test 
{
    public static void main(String args[])
    {
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
        
    }
}

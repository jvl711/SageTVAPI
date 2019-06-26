
package jvl.sage.api.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MiniServer 
{
    private int bindingport;
    private HttpServer httpserver;
    
    public MiniServer(int bindingport) throws IOException
    {
        this.bindingport = bindingport;
        httpserver = HttpServer.create(new InetSocketAddress(this.bindingport), 0);
        MiniHttpHandler handler = new MiniHttpHandler();

        httpserver.createContext("/api/v1/test", handler);
        httpserver.createContext("/api/v1/tv/all", handler);
    }
    
    public void start()
    {
        httpserver.start();
    }
    
    public void stop()
    {
        httpserver.stop(0);
    }
    
}

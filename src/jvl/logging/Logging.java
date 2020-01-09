
package jvl.logging;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Logging 
{
    private static final Logger root = Logger.getLogger("");
    
    static
    {
        StdOutHandler handler = new StdOutHandler();
        
        //Remove all handlers from the root logger ("In this case root is JVL")
        for(int i = root.getHandlers().length - 1; i >= 0; i--)
        {
            root.removeHandler(Logger.getLogger("").getHandlers()[i]);
        }
        
        //Add stdout logger
        root.addHandler(handler);
        //Defailt logging to info level
        root.setLevel(Level.WARNING);
        
        Logger.getLogger("jvl.sage.api").setLevel(Level.WARNING);
        Logger.getLogger("jvl.tmdb").setLevel(Level.WARNING);
        Logger.getLogger("jvl.metadata").setLevel(Level.WARNING);
        Logger.getLogger("sun.awt").setLevel(Level.WARNING);
        Logger.getLogger("java.awt").setLevel(Level.WARNING);
        Logger.getLogger("javax.awt").setLevel(Level.WARNING);
        Logger.getLogger("java.swing").setLevel(Level.WARNING);
        Logger.getLogger("javax.swing").setLevel(Level.WARNING);
        
    }
    
    public static Logger getLogger(String name)
    {
        return Logger.getLogger(name);
    }
    
    public static void setRootLevel(Level level)
    {
        root.setLevel(level);
    }
}

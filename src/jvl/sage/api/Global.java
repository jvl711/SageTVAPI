
package jvl.sage.api;

import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;

public class Global extends SageAPI
{
    /**
     * This is to help scale insets and lines in the UI which are not percent based
     * It will be based on resolution with
     * 
     * < 1280 = 1 (SD and below)
     * < 1920 = 2 HD ~720P
     * < 3840 = 3 HD ~1080P
     * >= 3840 = 4 4K and above
     * 
     */
    public static int GetUIScaleFactor(UIContext context) throws SageCallApiException
    {
       int width = Global.GetFullUIWidth(context);
       
       if(width < 1280)
       {
           return 1;
       }
       else if (width < 1920)
       {
           return 2;
       }
       else if(width < 3840)
       {
           return 3;
       }
       else
       {
           return 4;
       }
           
    }
    
    public static int GetFullUIHeight(UIContext context) throws SageCallApiException
    {
        int result = 0;
        
        result = Configuration.callApiInt(context, "GetFullUIHeight");

        return result;
    }
    
    public static int GetFullUIWidth(UIContext context) throws SageCallApiException
    {
        int result = 0;
        
        result = Configuration.callApiInt(context, "GetFullUIWidth");

        return result;
    }
}

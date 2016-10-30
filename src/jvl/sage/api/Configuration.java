
package jvl.sage.api;

import jvl.sage.SageAPI;


public class Configuration extends SageAPI
{
    public static String getServerProperty(String property, String defaultValue)
    {
        String result = "";
        
        try
        {
            result = Configuration.callApiString("GetServerProperty", property, defaultValue);
        }
        catch(Exception ex)
        {
            System.out.println("Error getting Server Property");
        }
        
        return result;
    }
    
    public static void setServerProperty(String property, String value)
    {

        try
        {
            Configuration.callApi("SetServerProperty", property, value);
        }
        catch(Exception ex)
        {
            System.out.println("Error setting Server Property");
        }
     
    }
    
}

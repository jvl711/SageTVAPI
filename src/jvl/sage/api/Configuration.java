
package jvl.sage.api;

import jvl.sage.SageAPI;


public class Configuration extends SageAPI
{
    public static String GetProperty(UIContext context, String property, String defaultValue)
    {
        String result = "";
        
        try
        {
            Configuration.callApiString(context, "GetProperty", property, defaultValue);
        }
        catch(Exception ex)
        {
            System.out.println("Error getting Client Property: " + property);
        }
        
        return result;
    }
    
    public static void SetProperty(UIContext context, String property, String value)
    {   
        try
        {
            Configuration.callApiString(context, "SetProperty", property, value);
        }
        catch(Exception ex)
        {
            System.out.println("Error setting Client Property: " + property);
        }
    }
    
    public static String GetServerProperty(String property, String defaultValue)
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
    
    public static void SetServerProperty(String property, String value)
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

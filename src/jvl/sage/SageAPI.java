
package jvl.sage;

import java.lang.reflect.InvocationTargetException;
import jvl.sage.api.UIContext;
import sage.SageTV;

public class SageAPI 
{
    
    protected static void callApi(String method) throws SageCallApiException
    {
        callAPIBase(method, null);
    }
    
    protected static void callApi(UIContext context, String method) throws SageCallApiException
    {
        callAPIBase(context, method, null);
    }
    
    protected static void callApi(String method, Object arg1) throws SageCallApiException
    {
        callAPIBase(method, new Object [] {arg1});
    }
    
    protected static void callApi(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static void callApi(String method, Object arg1, Object arg2) throws SageCallApiException
    {
        callAPIBase(method, new Object [] {arg1, arg2});
    }
    
    protected static void callApi(String method, Object arg1, Object arg2, Object arg3) throws SageCallApiException
    {
        callAPIBase(method, new Object [] {arg1, arg2, arg3});
    }
    
    protected static void callApi(UIContext context, String method, Object arg1, Object arg2) throws SageCallApiException
    {
        callAPIBase(context, method, new Object [] {arg1, arg2});
    }
    
    protected static Object callApiObject(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static Object callApiObject(String method, Object arg1) throws SageCallApiException
    {
        return callAPIBase(method, new Object [] {arg1});
    }
    
    protected static Object callApiObject(UIContext context, String method) throws SageCallApiException
    {
        return callAPIBase(context, method, null);
    }
    
    
    protected static Object callApiObject(String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return callAPIBase(method, new Object [] {arg1, arg2});
    }
    
    protected static Object callApiObject(String method, Object arg1, Object arg2, Object arg3) throws SageCallApiException
    {
        return callAPIBase(method, new Object [] {arg1, arg2, arg3});
    }
    
    protected static Object callApiObject(UIContext context, String method, Object arg1, Object arg2, Object arg3) throws SageCallApiException
    {
        return callAPIBase(context, method, new Object [] {arg1, arg2, arg3});
    }
    
    protected static Object callApiObject(UIContext context, String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return callAPIBase(context, method, new Object [] {arg1, arg2});
    }
    
    protected static Object [] callApiArray(String method, Object arg1) throws SageCallApiException
    {
        return (Object [])callAPIBase(method, new Object [] {arg1});
    }
    
    protected static Object [] callApiArray(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return (Object [])callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static Object [] callApiArray(String method) throws SageCallApiException
    {
        return (Object [])callAPIBase(method, null);
    }
    
    protected static Object [] callApiArray(UIContext context, String method) throws SageCallApiException
    {
        return (Object [])callAPIBase(context, method, null);
    }
    
    protected static String callApiString(String method, Object arg1) throws SageCallApiException
    {
        return (String)callAPIBase(method, new Object [] {arg1});
    }
    
    protected static String callApiString(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return (String)callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static String callApiString(String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return (String)callAPIBase(method, new Object [] {arg1, arg2});
    }
    
    protected static String callApiString(UIContext context, String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return (String)callAPIBase(context, method, new Object [] {arg1, arg2});
    }
    
    protected static String callApiString(String method) throws SageCallApiException
    {
        return (String)callAPIBase(method, null);
    }
    
    protected static String callApiString(UIContext context, String method) throws SageCallApiException
    {
        return (String)callAPIBase(context, method, null);
    }
    
    protected static int callApiInt(String method) throws SageCallApiException
    {
        return (Integer)callAPIBase(method, null);
    }
    
    protected static int callApiInt(UIContext context, String method) throws SageCallApiException
    {
        return (Integer)callAPIBase(context, method, null);
    }
    
    protected static int callApiInt(String method, Object arg1) throws SageCallApiException
    {
        return (Integer)callAPIBase(method, new Object [] {arg1});
    }
    
    protected static int callApiInt(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return (Integer)callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static long callApiLong(String method) throws SageCallApiException
    {
        return (Long)callAPIBase(method, null);
    }
    
    protected static long callApiLong(UIContext context, String method) throws SageCallApiException
    {
        return (Long)callAPIBase(context, method, null);
    }
    
    protected static long callApiLong(String method, Object arg1) throws SageCallApiException
    {
        return (Long)callAPIBase(method, new Object [] {arg1});
    }
    
    protected static long callApiLong(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return (Long)callAPIBase(context, method, new Object [] {arg1});
    }
    
    protected static long callApiLong(String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return (Long)callAPIBase(method, new Object [] {arg1, arg2});
    }
    
    protected static long callApiLong(UIContext context, String method, Object arg1, Object arg2) throws SageCallApiException
    {
        return (Long)callAPIBase(context, method, new Object [] {arg1, arg2});
    }
    
    protected static boolean callAPIBoolean(String method) throws SageCallApiException
    {
        return (Boolean)callAPIBase(method, null);
    }
    
    protected static boolean callAPIBoolean(UIContext context, String method) throws SageCallApiException
    {
        return (Boolean)callAPIBase(context, method, null);
    }
    
    protected static boolean callAPIBoolean(String method, Object arg1) throws SageCallApiException
    {
        return (Boolean)callAPIBase(method, new Object [] {arg1});
    }
    
    protected static boolean callAPIBoolean(UIContext context, String method, Object arg1) throws SageCallApiException
    {
        return (Boolean)callAPIBase(context, method, new Object [] {arg1});
    }
    
    private static Object callAPIBase(String method, Object [] args) throws SageCallApiException
    {
        
        Object ret = null;
        
        try 
        {
            String message = "SageAPI calling method: " + method + System.getProperty("line.separator");
        
            if(args != null)
            {
                for(int i = 0; i < args.length; i++)
                {
                    if(args[i] != null)
                    {
                        message += "\targs[" + i + "]: " + args[i].toString();
                    }
                    else
                    {
                        message += "\targs[" + i + "]: null";
                    }
                }
            }
            else
            {
                message += "\targs[]: null";
            }
            
            Debug.Writeln(message, Debug.INFO);
            
            ret = (Object)SageTV.api(method, args);
                    
            if(ret == null)
            {
                Debug.Writeln("SageAPI returned: null", Debug.INFO);
            }
            else
            {
                Debug.Writeln("SageAPI returned: " + ret.toString(), Debug.INFO);
            }
        } 
        catch (InvocationTargetException ex) 
        {
            SageCallApiException exception = new SageCallApiException(method, args, ex);
            
            Debug.Writeln(exception.getMessage(), Debug.ERROR);
            Debug.WriteStackTrace(exception, Debug.ERROR);
            
            throw exception;
        }
        
        return ret;
    }
    
    private static Object callAPIBase(UIContext context, String method, Object [] args) throws SageCallApiException
    {
        
        Object ret = null;
        
        try 
        {
            String message = "SageAPI context [" + context + "] calling method [" + method + "]" + System.getProperty("line.separator");
        
            if(args != null)
            {
                for(int i = 0; i < args.length; i++)
                {
                    if(args[i] != null)
                    {
                        message += "\targs[" + i + "]: " + args[i].toString();
                    }
                    else
                    {
                        message += "\targs[" + i + "]: null";
                    }
                }
            }
            else
            {
                message += "\targs[]: null";
            }
            
            Debug.Writeln(message, Debug.INFO);
            
            ret = (Object)SageTV.apiUI(context.GetName(), method, args);
            
                    
            if(ret == null)
            {
                Debug.Writeln("SageAPI returned: null", Debug.INFO);
            }
            else
            {
                Debug.Writeln("SageAPI returned: " + ret.toString(), Debug.INFO);
            }
        } 
        catch (InvocationTargetException ex) 
        {
            SageCallApiException exception = new SageCallApiException(method, args, ex);
            
            Debug.Writeln(exception.getMessage(), Debug.ERROR);
            Debug.WriteStackTrace(exception, Debug.ERROR);
            
            throw exception;
        }
        
        return ret;
    }
    
}

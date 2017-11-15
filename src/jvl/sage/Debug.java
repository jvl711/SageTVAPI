
package jvl.sage;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;


public class Debug 
{
    private static File debugfile;
    private static boolean isDebug;
    private static int debugLevel;
    private static long maxDebugFileSize;
    
    /* Minimum debug fileSize */
    private static final long minDebugFileSizeAllowed = 10240;
    
    /* Debug Levels */
    public static final int INFO = 0;
    public static final int WARNING = 100;
    public static final int ERROR = 200;
    
    private static String fileName = "JVL_SageAPI_Debug";
    private static String fileExt = "txt";
    
    /* Debug File */
    private static FileWriter output;
    
    private static ArrayList<String> classFilters;
    
    static
    {
        
        Debug.classFilters = new ArrayList<String>();
        Debug.debugfile = new File(fileName + "." + fileExt);
        Debug.debugLevel = Debug.INFO;
        Debug.maxDebugFileSize = 10240 * 1024; /* Defaulting to 10MB */
        Debug.isDebug = false;
        
        try
        {
            Debug.output = new FileWriter(Debug.debugfile, true);
        }
        catch(Exception ex) 
        { 
            System.out.println("JVL - Error creating FileWriter: " + ex.getMessage());
        }
        
        System.out.println("********** jvl.sage.Debug Static Constructor ***************");
        System.out.println("jvl.sage.Debug");
        System.out.println("Debug File Location: " + Debug.debugfile.getPath());
        
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() 
        {
            public void run() 
            {
                System.out.println("Deubg shutdown hook called!");
                
                try
                {
                    output.close();
                }
                catch(Exception ex) { }
                
            }
        }, "JVL-Debug-Shutdown-Thread"));
        
        
    }
    
    public static void SetDebug(boolean state)
    {
        Debug.isDebug = state;
    }
    
    public static boolean GetDebug()
    {
        return Debug.isDebug;
    }
    
    /**
     * Sets at what level items are written to the debug log
     * 
     * @param level Debug level
     */
    public static void SetDebugLevel(int level)
    {
        Debug.debugLevel = level;
    }
    
    public static void AddClassFilter(String classFilter)
    {
        if(!Debug.classFilters.contains(classFilter))
        {
            Debug.classFilters.add(classFilter);
        }
    }
    
    public static void RemoveClassFilter(String classFilter)
    {
        Debug.classFilters.remove(classFilter);
    }
    
    /**
     * Set max debug file size.  Can not be less than the minimum defined size
     * of 1024.  If set to negative number than size is unlimited
     * 
     * @param size 
     */
    public static void SetMaxFileSize(long size)
    {
        
        if(size < 0 )
        {
            Debug.maxDebugFileSize = -1;
        }
        else if(size < Debug.minDebugFileSizeAllowed )
        {
            Debug.maxDebugFileSize = Debug.minDebugFileSizeAllowed;
        }
        else
        {
            Debug.maxDebugFileSize = size;
        }
    }
    
    public static void RotateLogFile()
    {
        File temp = new File(Debug.fileName + "_1." + Debug.fileExt);
            
        if(temp.exists())
        {
            temp.delete();
        }

        Debug.debugfile.renameTo(temp);
    }
    
    private static void CheckForFileRotation()
    {
        if(Debug.maxDebugFileSize > -1 && Debug.debugfile.length() >= Debug.maxDebugFileSize)
        {
            File temp = new File(Debug.fileName + "_1." + Debug.fileExt);
            
            if(temp.exists())
            {
                temp.delete();
            }
           
            Debug.debugfile.renameTo(temp);
        }
    }
    
    private static String GetDebugLevelString(int level)
    {
        String ret = "INFO";
        
        if(level >= Debug.WARNING)
        {
            ret = "WARNING";
        }
        else if(level >= Debug.ERROR)
        {
            ret = "ERROR";
        }
        
        return ret;
    }
    
    
    public static void Writeln(String line, int level)
    {
        String callingClass = "";
        callingClass = sun.reflect.Reflection.getCallerClass().getName();
        
        Debug.Writeln(callingClass, line, level);
    }
    
    public static void Writeln(String callingClass, String line, int level)
    {
        
        if(Debug.isDebug &&  level >= Debug.debugLevel)
        {
            String formatedLine = Debug.GetDebugLevelString(level) + "[" + callingClass + "]" + " - " + line;
            
            try
            {
                //Check for class filter
                if(Debug.classFilters.size() > 0 && !callingClass.equalsIgnoreCase(""))
                {
                    if(!Debug.classFilters.contains(callingClass))
                    {
                        return;
                    }
                }    
                
                Debug.CheckForFileRotation();
                
                output.write(formatedLine + System.getProperty("line.separator"));
                output.flush();
            }
            catch(Exception ex)
            {
                System.out.println("Error writing to JVL Debug File.");   
                ex.printStackTrace();
            }
        }
    }
    
    public static void WriteStackTrace(Exception exception, int level)
    {
        String formattedLine = "";
        String callingClass = "";
        
        callingClass = sun.reflect.Reflection.getCallerClass().getName();
        
        if(Debug.isDebug && level >= Debug.debugLevel)
        { 
            if(Debug.classFilters.size() > 0 && !callingClass.equalsIgnoreCase(""))
            {
                if(!Debug.classFilters.contains(callingClass))
                {
                    return;
                }
            }    
            
            StackTraceElement [] stack = exception.getStackTrace();

            for(int i = 0; i < stack.length; i++)
            {
                formattedLine += stack[i].toString();
            }

            Debug.Writeln(callingClass, formattedLine, level);
            
        }
    }
}

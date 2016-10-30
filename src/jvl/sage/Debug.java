
package jvl.sage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

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
    
    static
    {
        Debug.debugfile = new File(fileName + "." + fileExt);
        Debug.debugLevel = Debug.INFO;
        Debug.maxDebugFileSize = 10240 * 1024; /* Defaulting to 10MB */
        Debug.isDebug = false;
        
        System.out.println("**********STATIC CONSTRUCTOR CALLED***************");
        System.out.println("jvl.sage.Debug");
        System.out.println("Debug File Location: " + Debug.debugfile.getPath());
        
    }
    
    public static void SetDebug(boolean state)
    {
        Debug.isDebug = state;
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
        
        if(Debug.isDebug &&  level >= Debug.debugLevel)
        {
            String formatedLine = Debug.GetDebugLevelString(level) + " - " + line;
            
            try
            {
                Debug.CheckForFileRotation();
                
                FileWriter output = new FileWriter(debugfile, true);
                output.write(formatedLine + System.lineSeparator());
                output.close();
            }
            catch(Exception ex)
            {
                System.out.println("Error writing to JVL Debug File.");
            }
        }
    }
    
    public static void WriteStackTrace(Exception exception, int level)
    {
        String formattedLine = "";
        
        if(Debug.isDebug && level >= Debug.debugLevel)
        { 
            StackTraceElement [] stack = exception.getStackTrace();
            
            for(int i = 0; i < stack.length; i++)
            {
                formattedLine += stack[i].toString();
            }
            
            Debug.Writeln(formattedLine, level);
        }
    }
}

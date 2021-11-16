
package jvl.logging;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class StdOutHandler extends Handler
{
    public StdOutHandler()
    {

    }
    
    @Override
    public synchronized void publish(LogRecord record) 
    {
        try
        {
            if(record.getParameters() != null && record.getParameters().length > 0)
            { 
                System.out.println(record.getLoggerName() + " [" + record.getLevel().getName() + "] : " +  MessageFormat.format(record.getMessage(), record.getParameters()));
            }
            else
            {
                System.out.println(record.getLoggerName() + " [" + record.getLevel().getName() + "] : " +  record.getMessage());
            }
            
            if(record.getThrown() != null)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                String stackTrace = sw.toString();
                System.out.println(stackTrace);
            }
        }
        catch(Exception ex)
        {
            System.out.println(record.getLoggerName() + " [" + record.getLevel().getName() + "] : " +  record.getMessage());
        }
    }

    @Override
    public void flush()
    {
        
    }

    @Override
    public void close() throws SecurityException
    {
        
    }
    
}

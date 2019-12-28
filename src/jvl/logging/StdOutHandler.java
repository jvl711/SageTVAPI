
package jvl.logging;


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
        
        
        System.out.println(record.getLoggerName() + " [" + record.getLevel().getName() + "] : " +  MessageFormat.format(record.getMessage(), record.getParameters()));
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

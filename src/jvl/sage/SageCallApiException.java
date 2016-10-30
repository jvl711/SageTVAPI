
package jvl.sage;

public class SageCallApiException extends Exception
{
    private String method;
    private Object [] args;
    private Exception baseException;    

    public SageCallApiException(String method, Object [] args, Exception baseException)
    {
        this.method = method;
        this.args = args;
        this.baseException = baseException;
    }
    
    @Override
    public String getMessage()
    {
        String message = "";
        
        message += "Error calling method: " + method;
        
        if(args != null)
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i] != null)
                {
                    message += " args[" + i + "]: " + args[i].toString();
                }
                else
                {
                    message += " args[" + i + "]: null";
                }
            }
        }
        else
        {
            message += " args[]: null";
        }
        
        return message;
    }
    
    public Exception GetBaseException()
    {
        return this.baseException;
    }
    
}

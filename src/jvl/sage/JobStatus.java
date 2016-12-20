
package jvl.sage;


public class JobStatus 
{
    /* STATUS */
    public static final int ERROR = -1;
    public static final int INITIALIZED = 0;
    public static final int RUNNING = 1;
    public static final int COMPLETE = 2;
    
    
    private String statusMessage;
    private int status;
     
    public JobStatus()
    {
        this.status = JobStatus.INITIALIZED;
        this.statusMessage = "Initialized";
    }
    
    public void SetRunning()
    {
        this.status = JobStatus.RUNNING;
        this.statusMessage = "Running";
    }
    
    public void SetComplete()
    {
        this.status = JobStatus.COMPLETE;
        this.statusMessage = "Complete";
    }
    
    public void SetError(String message)
    {
        this.status = JobStatus.ERROR;
        this.statusMessage = message;
    }
    
    public void SetStatusMessage(String message)
    {
        this.statusMessage = message;
    }
    
    public void SetStatus(int status)
    {
        if(status >= -1 && status <= 2)
        {
            this.status = status;
        }
        else
        {
            throw new RuntimeException("Unknown status");
        }
    }
    
    public String GetStatusMessage()
    {
        return this.statusMessage;
    }
    
    public boolean IsComplete()
    {
        if(this.status == JobStatus.COMPLETE || this.status == JobStatus.ERROR)
        {
            return true;
        }
        
        return false;
    }
    
    public boolean IsRunning()
    {
        return (this.status == JobStatus.RUNNING);
    }
            
}

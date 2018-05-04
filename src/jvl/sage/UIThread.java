
package jvl.sage;

import jvl.sage.api.Widget;


public class UIThread extends Thread
{
    private Widget execute;
    public Object executeResult;
    private Widget beforeExecute;
    private Widget afterExecute;
    
    public UIThread()
    {
        
    }
    
    public void SetOnExecuteWidget(Widget widget)
    {
        this.execute = widget;
    }
    
    public void SetBeforeExecuteWidget(Widget widget)
    {
        this.beforeExecute = widget;
    }
    
    public void SetAfterExecuteWidget(Widget widget)
    {
        this.afterExecute = widget;
    }
    
    public Object GetLastResult()
    {
        return this.executeResult;
    }
    
    public void StartThread()
    {
        
    }
    
    public void StopThread()
    {
        
    }
    
    @Override
    public void run() 
    {
        
        try
        {
            this.executeResult = this.execute.ExecuteWidgetChain();
        }
        catch(Exception ex)
        {
            /*
            What do we do...  Do we exit thread?  Do we allow a certain number
            of errors?
            */
        }
        
    }
}

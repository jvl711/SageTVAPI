
package jvl.sage;

import jvl.sage.api.Widget;

public class UIThread extends Thread
{
    private Widget execute;
    public Object executeResult;
    private Widget beforeExecute;
    private Widget afterExecute;
    private final long timer;
    private boolean running;
    private final long contextCheckTime = 60000; /* Check if context is still valid after this time */

    public UIThread(Widget executeWidget, long timer)
    {
        this.execute = executeWidget;
        this.timer = timer;
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
        this.running = true;
        this.start();
    }
    
    public void StopThread()
    {
        this.running = false;
    }
    
    @Override
    public void run()
    {
        long sinceLastContextCheck = 0;
        
        while(running)
        {
            
            
            try
            {
                if(this.beforeExecute != null)
                {
                    this.beforeExecute.ExecuteWidgetChain();
                }

            }
            catch(Exception ex) { }

            try
            {
                this.executeResult = this.execute.ExecuteWidgetChain();
            }
            catch(Exception ex)
            {
                System.out.println("JVL - UIThread exception.  Stopping thread");
                this.running = false;
                break;
            }

            try
            {
                if(this.afterExecute != null)
                {
                    this.afterExecute.ExecuteWidgetChain();
                }

            }
            catch(Exception ex) { }
        
            try
            {
                Thread.sleep(timer);
            }
            catch(Exception ex) { }    
        
            if(sinceLastContextCheck >= contextCheckTime)
            {
                try
                {
                    if(!execute.GetUIConext().isActive())
                    {
                        System.out.println("JVL - UIThread.  UIConext not active.  Exiting thread.");
                        running = false;
                    }
                    else
                    {
                        System.out.println("JVL - UIThread.  UIConext is still active.");
                        sinceLastContextCheck = 0;
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("JVL - UIThread error.  Exception checking UIContext status");
                    running = false;
                }
            }
            else
            {
                sinceLastContextCheck += timer;
            }
            
        }

    }
}

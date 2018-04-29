package jvl.sage.api;

import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;


public class Widget extends SageObject
{
    private UIContext uicontext;
    private Object widget;
    
    public Widget(String contextName, Object widget)
    {
        this(new UIContext(contextName), widget);
    }
    
    public Widget(UIContext context, Object widget)
    {
        this.uicontext = context;
        this.widget = widget;
    }
    
    /**
     * Returns the name of the Widget
     * @return the name of the Widget
     * @throws SageCallApiException 
     */
    public String GetName() throws SageCallApiException
    {
        
        return Widget.callApiString(this.uicontext, "GetWidgetName", this.widget);
        
    }
    
    /**
     * Executes a Widget and the chain of child Widgets underneath it
     * @return the value returned by the last executed Widget in the chain
    */
    public Object ExecuteWidgetChain() throws SageCallApiException
    {
        
        Object ret = null;
        
        try
        {   
            System.out.println("JVL - Calling ExecuteWidgetChainInCurrentMenuContext on: " + this.GetName() + " - " + this.GetSymbol());
            ret = Widget.callApiObject(uicontext, "ExecuteWidgetChainInCurrentMenuContext", this.UnwrapObject());
            System.out.println("JVL - Completed calling ExecuteWidgetChainInCurrentMenuContext");
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Error executing ExecuteWdigetChain " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return ret;
    }
    
    /**
     * Returns the symbol of this Widget
     * @return the symbol of the Widget
     * @throws SageCallApiException 
     */
    public String GetSymbol() throws SageCallApiException
    {
        return Widget.callApiString(this.uicontext, "GetWidgetSymbol", this.widget);
    }
    
    /**
     * Returns the type of this Widget
     * @return the type of the Widget
     * @throws SageCallApiException 
     */
    public String GetType() throws SageCallApiException
    {
        return Widget.callApiString(this.uicontext, "GetWidgetType", this.widget);
    }
  
    /**
     * Launches menu if it is a menu widget.  If it is not a menu widget no action
     * is taken
     * @throws SageCallApiException 
     */
    public void LaunchMenu() throws SageCallApiException
    {
        if(this.GetType().equalsIgnoreCase("menu"))
        {
            Widget.callApi(uicontext, "LaunchMenuWidget", widget);
        }
    }
    
/* --------------------------- STATIC METHODS ---------------------------------------*/    
    
    public static Widget GetCurrentMenuWidget(UIContext uicontext) throws SageCallApiException
    {
        return new Widget(uicontext, Widget.callApiObject(uicontext, "GetCurrentMenuWidget"));
    }
            
    
    /**
     * Searches currently active menu for the widget with the specified name
     * 
     * @param uicontext UI Context to do the search in
     * @param name Name of the widget to search for
     * 
     * @return If the widget is found in the active menu then it returns it, or null
     */
    public static Widget FindActiveWidget(UIContext uicontext, String name) throws SageCallApiException
    {
        Widget menu = Widget.GetCurrentMenuWidget(uicontext);

        Object ret = Widget.callApiObject(uicontext, "GetWidgetChild", menu.UnwrapObject(), null, name);
        
        if(ret == null)
        {
            return null;
        }
        else
        {
            return new Widget(uicontext, ret);
        }
    }
    
    @Override
    public Object UnwrapObject() 
    {
        return this.widget;
    }
    
    @Override
    public String toString() 
    {
        String ret;
        try 
        {
            ret = "Widget Name: " + this.GetName() + " Type: " + this.GetType() + " Symbol: " + this.GetSymbol();
        } 
        catch (SageCallApiException ex) 
        {
            ret = "Error: " + ex.getMessage();
        }
        
        return ret;
        
    }
    
}

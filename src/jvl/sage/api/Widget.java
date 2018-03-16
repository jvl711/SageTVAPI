package jvl.sage.api;

import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;

/**
 *
 * @author jvl711
 */
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

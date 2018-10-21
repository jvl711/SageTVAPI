
package jvl.sage.api;

import jvl.sage.SageCallApiException;


public class UIContext 
{
    private final String name;
    
    public UIContext(String name)
    {
        this.name = name;
    }
    
    public String GetName()
    {
        return this.name;
    }
    
    /***
     * Looks at all available UIContextNames to see if this one is valid/available
     * @return true is the UIContext is available
     * @throws SageCallApiException 
    */
    public boolean isActive() throws SageCallApiException
    {
        String [] uiContextNames = Global.GetUIContextNames();
        
        for(int i = 0; i < uiContextNames.length; i++)
        {
            if(uiContextNames[i].equalsIgnoreCase(this.GetName()))
            {
                return true;
            }
        }
        
        return false;
    }
            
}




package jvl.sage.api;

import jvl.sage.SageObject;


public class Channel extends SageObject
{
    private Object channel;
    
    public Channel(Object channel)
    {
        this.channel = channel;
    }
    
    public String getChannelNumber()
    {
        String channelNumber = "";
        
        try
        {
            channelNumber = callApiString("GetChannelNumber", channel);
        }
        catch(Exception ex)
        {
            System.out.println("Error getting channel number");
        }
        
        return channelNumber;
    }
    
    
    @Override
    public Object UnwrapObject() 
    {
        return this.channel;
    }
    
    
}

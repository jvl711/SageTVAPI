
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import jvl.sage.SageCallApiException;

public class Channels extends SageArrayObject<Channel>
{
    ArrayList<Channel> channels;
    
    public Channels()
    {
        try
        {
            this.LoadChannels();
        }
        catch(Exception ex)
        {
            System.out.println("Error loading channel object");
        }
    }

    private void LoadChannels() throws SageCallApiException
    {
        channels = new ArrayList();
        
        Object [] rawChannels = callApiArray("GetAllChannels");
        
        for(int i = 0; i < rawChannels.length; i++)
        {
            channels.add(new Channel(rawChannels[i]));
        }
    }
    
    /*
    public void AddChannel(Channel channel)
    {
        channels.add(channel);
    }
    
    public void RemoveChannel(Channel channel)
    {
        channels.remove(channel);
    }
    
    public void RemoveChannel(int index)
    {
        channels.remove(index);
    }
    
    public Channel getChannel(int index)
    {
        return channels.get(index);
    }
    
    public int getSize()
    {
        return channels.size();
    }
    */  
    
    @Override
    public Object[] UnwrapObject() 
    {
        Object [] unwrapped = new Object[channels.size()];
        
        for(int i = 0; i < channels.size(); i++)
        {
            unwrapped[i] = channels.get(i).UnwrapObject();
        }
        
        return unwrapped;
    }

    @Override
    public Channel Remove(int index) 
    {
        return this.channels.remove(index);
    }

    @Override
    public Channel Get(int index) 
    {
        return this.channels.get(index);
    }

    @Override
    public void Add(Channel d) 
    {
        this.channels.add(d);
    }

    @Override
    public int Size() 
    {
        return this.channels.size();
    }

    @Override
    public void Set(int index, Channel d) 
    {
        this.channels.set(index, d);
    }
    
}

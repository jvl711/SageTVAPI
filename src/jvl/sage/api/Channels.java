
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import jvl.sage.SageCallApiException;

public class Channels extends SageArrayObject<Channel>
{
   
    
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
        //channels = new ArrayList();
        
        Object [] rawChannels = callApiArray("GetAllChannels");
        
        for(int i = 0; i < rawChannels.length; i++)
        {
            this.add(new Channel(rawChannels[i]));
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
        Object [] unwrapped = new Object[this.size()];
        
        for(int i = 0; i < this.size(); i++)
        {
            unwrapped[i] = this.get(i).UnwrapObject();
        }
        
        return unwrapped;
    }

    /**
     * @deprecated 
     */
    //@Override
    //public Channel Remove(int index) 
    //{
    //    System.out.println("JVL - Deprecated called (Channels.Remove)");
    //    return this.remove(index);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public Channel Get(int index) 
    //{
    //    System.out.println("JVL - Deprecated called (Channels.Get)");
    //    return this.get(index);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public void Add(Channel d) 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Add)");
    //    this.add(d);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public int Size() 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Size)");
    //    return this.size();
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public void Set(int index, Channel d) 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Set)");
    //    this.set(index, d);
    //}
    
}

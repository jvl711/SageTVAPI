
package jvl.sage;

import java.util.ArrayList;
import java.util.Arrays;
import jvl.sage.api.Channels;
import jvl.sage.api.Configuration;

public class FavoriteChannels 
{
    private ArrayList<String> channels;
    private String listName;
    private int listIndex;
    private static final String propertyPrefix = "jvl.favorites.list.";
    
    public FavoriteChannels(int listIndex)
    {
        channels = new ArrayList<String>();
        this.listIndex = listIndex;
        
        this.listName = Configuration.GetServerProperty(propertyPrefix + listIndex + ".listname", "List " + listIndex);
        String temp = Configuration.GetServerProperty(propertyPrefix + listIndex, "");
        
        Debug.Writeln("List Property: " + temp, Debug.INFO);
        
        String [] items = temp.split(",");
        
        if(this.listName.equalsIgnoreCase(""))
        {
            this.listName = "List " + listIndex;
        }
        
        for(int i = 0; i < items.length; i++)
        {
            if(!items[i].equalsIgnoreCase(""))
            {
                Debug.Writeln("Adding Channel: " + items[i], Debug.INFO);
                channels.add(items[i]);
            }
        }
        
    }
    public boolean Exists(String ChannelNumber)
    {
        
        return channels.contains(ChannelNumber);
        
    }
    
    public Object [] GetChannels()
    {
        Channels sageChannels = new Channels();
        
        for(int i = sageChannels.size() - 1; i > 0; i--)
        {
            if(!this.Exists(sageChannels.get(i).getChannelNumber()))
            {
                sageChannels.remove(i);
            }
        }
        
        return sageChannels.UnwrapObject();
    }
    
    
    public String GetName()
    {
        return this.listName;
    }
    
    public void SetName(String listName)
    {
        this.listName = listName;
        Configuration.SetServerProperty(propertyPrefix + listIndex + ".listname", listName);
    }
    
    public int GetIndex()
    {
        return this.listIndex;
    }
        
    public void Add(String ChannelNumber)
    {
        if(!this.Exists(ChannelNumber) && !ChannelNumber.equalsIgnoreCase(""))
        {
            channels.add(ChannelNumber);
        }
        
        Configuration.SetServerProperty(propertyPrefix + listIndex, this.toString());
    }
    
    public void Remove(String ChannelNumber)
    {
        channels.remove(ChannelNumber);
        
        Configuration.SetServerProperty(propertyPrefix + listIndex, this.toString());
    }
    
    @Override
    public String toString()
    {
        String ret = "";
        
        for(int i = 0; i < channels.size(); i++)
        {
            ret += channels.get(i) + ",";
        }
        
        if(ret.length() > 0)
        {
            ret = ret.substring(0, ret.length() - 1);
        }
        
        return ret;
    }
    
}

package jvl.sage.api;

import jvl.sage.SageObject;


public class Show extends SageObject
{
    
    private Object show;
    
    public Show(Object show)
    {
        this.show = show;
    }
    
    public int GetSeasonNumber()
    {
        int response = 0;
        
        try
        {
            response = callApiInt("GetShowSeasonNumber", this.show);
        }
        catch (Exception ex)
        {
            System.out.println("Error getting show season number: " + ex.getMessage());
        }
        
        return response;
    }
    
    public int GetEpisodeNumber()
    {
        int response = 0;
        
        try
        {
            response = callApiInt("GetShowEpisodeNumber", this.show);
        }
        catch (Exception ex)
        {
            System.out.println("Error getting show season number: " + ex.getMessage());
        }
        
        return response;
    }

    public Airing GetAiring()
    {
        return new Airing(this.UnwrapObject());
    }

    
    @Override
    public Object UnwrapObject() 
    {
        return this.show;
    }
    
}

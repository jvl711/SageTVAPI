package jvl.sage.api;

import java.util.Comparator;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;

public class Show extends SageObject
{
    
    private Object show;
    
    public Show(Object show)
    {
        this.show = show;
    }
    
    public MediaFile GetMediaFile()
    {
        return new MediaFile(this.UnwrapObject());
    }
    
    public Airing GetAiring()
    {
        return new Airing(this.UnwrapObject());
    }
    
    public int GetSeasonNumber() throws SageCallApiException 
    {
        int response = 0;
        
        response = callApiInt("GetShowSeasonNumber", this.show);
        
        return response;
    }
    
    public int GetEpisodeNumber() throws SageCallApiException
    {
        int response = 0;
        
        response = callApiInt("GetShowEpisodeNumber", this.show);
        
        return response;
    }

    public String GetShowTitle() throws SageCallApiException
    {
        return callApiString("GetShowTitle", this);
    }
 
    public char GetShowTitleSearchChar() throws SageCallApiException
    {
        String title = this.GetSortableShowTitle();
        
        if(title.length() > 0)
        {
            return this.GetSortableShowTitle().charAt(0);
        }
        else
        {
            return ' ';
        }
    }
    
    public String GetSortableShowTitle() throws SageCallApiException
    {
        String title = this.GetShowTitle();
        String ret = title;
        
        if(title.startsWith("The "))
        {
            ret = title.replaceFirst("The ", "");
        }
        else if(title.startsWith("A "))
        {
            ret = title.replaceFirst("A ", "");
        }
        else if(title.startsWith("\"") && title.endsWith("\""))
        {
            //Remove the beginig and end Quote
            ret = title.substring(1, title.length() - 1);
        }
        else if(title.startsWith("'") && title.endsWith("'"))
        {
            //Remove the beginig and end Quote
            ret = title.substring(1, title.length() - 1);
        }
        
        return ret;
    }

    
    @Override
    public Object UnwrapObject() 
    {
        return this.show;
    }
    
}

class SortableShowTitleCompaator implements Comparator<Show>
{

    @Override
    public int compare(Show t, Show t1) 
    {
        try 
        {   
            return t.GetSortableShowTitle().compareTo(t1.GetSortableShowTitle());
            
        } 
        catch (SageCallApiException ex) 
        {
            
        }
        
        return 0;
        
    }
    
}
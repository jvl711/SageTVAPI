
package jvl.sage.api;

import java.util.logging.Level;
import java.util.logging.Logger;
import jvl.sage.Debug;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;

public class Airing extends SageObject
{
    private Object airing;
    
    public Airing(Object airing)
    {
        this.airing = airing;
    }
    
    public Show GetShow()
    {
        return new Show(this.UnwrapObject());
    }
    
    public MediaFile GetMediaFile()
    {
        return new MediaFile(this.UnwrapObject());
    }
    
    public boolean IsWatched() throws SageCallApiException
    {
        boolean response = false;
        
        response = callAPIBoolean("IsWatched", this.airing);
        
        return response;
    }
    
    /**
     * Set or clear the watch status of a airing
     * @param watched True if you want to set the airing watched
     *                False if you want to set the airing as not watched
     */
    public void SetWatchedStatus(boolean watched) throws SageCallApiException
    {
        boolean response = false;
        
        if(watched)
        {
            callApi("SetWatched", this.airing);
        }
        else
        {
            callApi("ClearWatched", this.airing);
        }
        
    }
    
    public long GetDuration() throws SageCallApiException
    {
        long ret = 0;

        ret = callApiLong("GetAiringDuration", this.airing);
                
        return ret;
    }
    
    public long GetWatchedDuration() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetWatchedDuration", this.airing);
        
        return ret;
    }
    
    public int GetWatchedPercent() throws SageCallApiException
    {
        float temp = ((this.GetWatchedDuration() / this.GetDuration()) * 100);
        int ret = java.lang.Math.round(temp);
        
        return ret;
    }
    
    /**
     * Attempt to determine if the there is an actual file for the airing.
     * @return true if MediaFile.GetSize > 0 else false
     */
    public boolean ExistsOnDisk()
    {
        long size;
        
        try 
        {
            size = this.GetMediaFile().GetSize();
        } 
        catch (SageCallApiException ex) 
        {
            Debug.Writeln("Called Airing.ExistsOnDisk() - SageCallApiException.  I assume this means the object no longer exists", Debug.INFO);
            size = 0;
        }
        
        if(size > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public Object UnwrapObject() 
    {
        return this.airing;
    }
    
}

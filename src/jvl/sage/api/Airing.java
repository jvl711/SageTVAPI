
package jvl.sage.api;

import jvl.sage.Debug;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;

public class Airing extends SageObject
{
    private Object airing;
    
    public Airing(Object airing)
    {
        
        try
        {
            if(Airing.IsAiringObject(airing))
            {
                this.airing = airing;
            }
            else if(MediaFile.IsMediaFileObject(airing))
            {
                this.airing = MediaFile.GetAiring(airing);
            }
            else
            {     
                throw new Exception("Unknown object type passed");
            }
        }
        catch(Exception ex)
        {
            throw new RuntimeException("JVL - Error constructing Airing.  The object passed was not an Airing or MediaFile");
        }
        
    }
    
    /**
     * Returns true if the argument is an Airing object. Automatic type conversion is NOT done in this call.
     * 
     * @param testObject the object to test
     * @return true if the argument is an Airing object
     * @throws SageCallApiException 
     */
    public static boolean IsAiringObject(Object testObject) throws SageCallApiException
    {
        return Airing.callAPIBoolean("IsAiringObject", testObject);
    }
    
    /**
     * Adds a new Airing object to the database. This call should be used with caution.
     * 
     * @param ShowExternalID a GUID which uniquely identifies the Show that correlates with this Airing, this Show should already have been added
     * @param StationID the GUID which uniquely identifies a "Station" (sort of like a Channel)
     * @param StartTime the time at which the new Airing starts
     * @param Duration the duration of the new Airing in milliseconds
     * @return the newly added Airing
     * @throws SageCallApiException 
     */
    public static Object AddAiring(String ShowExternalID, int StationID, long StartTime, long Duration) throws SageCallApiException
    {
        return Airing.callApiObject("AddAiring", ShowExternalID, StationID, StartTime, Duration);
    }
    
    /**
     * Returns true if the argument is an Airing object. Automatic type conversion is NOT done in this call.
     * 
     
     * @return true if the argument is an Airing object
     * @throws SageCallApiException 
     */
//    public boolean IsAiringObject() throws SageCallApiException
//    {
//        return Airing.callAPIBoolean("IsAiringObject", this.airing);
//    }
    
    /**
     * Gets the MediaFile object which corresponds to this Airing object
     * 
     * @param airing Airing to get media file for
     * @return the MediaFile object which corresponds to this Airing object, 
     * or null if it has no associated MediaFile
     * @throws SageCallApiException 
     */
    public static Object GetMediaFileForAiring(Object airing) throws SageCallApiException
    {
        return Airing.callApiObject("GetMediaFileForAiring", airing);
    }
    
    public static Object GetShowForAiring(Object airing) throws SageCallApiException
    {
        return Airing.callApiObject("GetShow", airing);
    }
    
    
    public Show GetShow() throws SageCallApiException
    {
        return new Show(airing);
    }
    
    public MediaFile GetMediaFile() throws SageCallApiException
    {
        Object temp = Airing.GetMediaFileForAiring(this.airing);
        
        if(temp == null)
        {
            return null;
        }
        
        return new MediaFile(temp);
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
    
    /***
     * Gets the start time of this Airing. The time is in Java time units, which are milliseconds since Jan 1, 1970 GMT
     * 
     * If this is an import file than this date will be the date the file was imported
     * 
     * @return the start time of this Airing
     * @throws SageCallApiException 
     */
    public long GetAiringStartTime() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetAiringStartTime", this.airing);
                
        return ret;
    }
    
    /***
     * Gets the end time of this Airing. The time is in Java time units, which are milliseconds since Jan 1, 1970 GMT
     * 
     * @return the end time of this Airing
     * @throws SageCallApiException 
     */
    public long GetAiringEndTime() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetAiringEndTime", this.airing);
                
        return ret;
    }
    
    public long GetScheduleStartTime() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetScheduleStartTime", this.airing);
                
        return ret;
    }
    
    public long GetScheduleEndTime() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetScheduleEndTime", this.airing);
                
        return ret;
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
    
    public long GetLatestWatchedTime() throws SageCallApiException
    {
        long ret = 0;
        
        ret = callApiLong("GetLatestWatchedTime", this.airing);
        
        return ret;
    }
    
    public int GetWatchedPercent() throws SageCallApiException
    {
        double temp = (((this.GetWatchedDuration() * 1.0) / (this.GetDuration() * 1.0)) * 100.0);
        int ret = (int)java.lang.Math.round(temp);
        
        return ret;
    }
    
    public int GetAiredPercent() throws SageCallApiException
    {
        long currentTime = 0;
        long startTime = 0;
        long endTime = 0;
        long duration = 0;
        long progress = 0;
        double percent = 0;
        
        currentTime = callApiLong("Time");
        startTime = this.GetAiringStartTime();
        endTime = this.GetAiringEndTime();
        
        if(currentTime < startTime)
        {
            //Has not started yet.  Return 0%
            return 0;
        }
        else if (currentTime > endTime)
        {
            //Airing complete.  Return 100%
            return 100;
        }
        
        duration = endTime - startTime;
        progress = currentTime - startTime;        
        
        percent = ((progress *1.0) / (duration * 1.0) * 100.0 );
        
        return (int)java.lang.Math.round(percent);
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

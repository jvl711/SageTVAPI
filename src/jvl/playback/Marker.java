package jvl.playback;

public class Marker
{
    private int index;
    private long startTime;
    private long endTime;
    private long offsetTime;
    private long mediaStartTime;
    private long mediaEndTime;
    private long mediaFileDuration;
    private String name;
    private MarkerType markerType;
    
    /**
     * Constructs a marker object
     * 
     * @param markerType The type of marker.  For instance Commercial vs Chapter
     * @param index The index relative to the other markers
     * @param startTime Start of the marker
     * @param endTime End of the marker
     * @param offsetTime A time offset of where the marker is in relation to the recording time of the file
     * @param mediaStartTime The start time of the mediaFile
     * @param mediaEndTime The end time of the mediaFile
     *                   
     */
    public Marker(MarkerType markerType, int index, long startTime, long endTime, long offsetTime, long mediaStartTime, long mediaEndTime)
    {
        this.markerType = markerType;
        this.index = index;
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
        this.mediaEndTime = mediaEndTime;
        this.mediaStartTime = mediaStartTime;
        this.mediaFileDuration = this.mediaEndTime - this.mediaStartTime;
        this.name = "";
    }
    
    public Marker(MarkerType markerType, int index, String name, long startTime, long endTime, long offsetTime, long mediaStartTime, long mediaEndTime)
    {
        this.markerType = markerType;
        this.index = index;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
        this.mediaEndTime = mediaEndTime;
        this.mediaStartTime = mediaStartTime;
        this.mediaFileDuration = this.mediaEndTime - this.mediaStartTime;
        
    }
    
    public int GetIndex()
    {
        return index;
    }
    
    public String GetName()
    {
        return this.name;
    }
    
    public MarkerType GetMarkerType()
    {
        return this.markerType;
    }
    
    public long GetStartTime()
    {
        return startTime + offsetTime;
    }
    
    public long GetEndTime()
    {
        return endTime + offsetTime;
    }
    
    /**
     * Determines if the time is within the start time plus the range 
     * 
     * testime beteen starttime and startime + range
     * 
     * @param testtime Time to test
     * @param range How far past start time and still a hit
     * @return true if it is a hit
     */
    public boolean IsHit(long testtime, long range)
    {
        if(testtime >= this.GetStartTime() && (testtime) <= (this.GetStartTime() + range))
        {
            return true;
        }
        
        return false;
    }
    
    public boolean IsInside(long testtime)
    {
        if(testtime >= this.GetStartTime() && testtime <= this.GetEndTime())
        {
            return true;
        }
        
        return false;
    }
    
    public double GetStartPercent()
    {
        long markerStartDuration = this.GetStartTime() - this.mediaStartTime;

        double temp = ((markerStartDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetEndPercent()
    {
        long markerEndDuration = this.GetEndTime() - this.mediaStartTime;

        double temp = ((markerEndDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetDurationPercent()
    {
        return this.GetEndPercent() - this.GetStartPercent();
    }
    
    @Override
    public String toString()
    {
        if(this.GetName().equalsIgnoreCase("chapter " + (index + 1)))
        {
            return this.GetName();
        }
        else
        {
            return "Chapter " + (index + 1) + " - " + this.GetName();
        }
        
    }
}

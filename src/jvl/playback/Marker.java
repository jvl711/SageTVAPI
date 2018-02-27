package jvl.playback;

public class Marker
{
    private long startTime;
    private long endTime;
    private long offsetTime;
    private long mediaStartTime;
    private long mediaEndTime;
    private long mediaFileDuration;
    private MarkerType markerType;
    
    /**
     * Constructs a marker object
     * 
     * @param markerType The type of marker.  For instance Commercial vs Chapter
     * @param startTime Start of the marker
     * @param endTime End of the marker
     * @param offsetTime A time offset of where the marker is in relation to the
     * @param mediaStartTime The start time of the mediaFile
     * @param mediaEndTime The end time of the mediaFile
     *                   recording time of the file
     */
    public Marker(MarkerType markerType, long startTime, long endTime, long offsetTime, long mediaStartTime, long mediaEndTime)
    {
        this.markerType = markerType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
        this.mediaEndTime = mediaEndTime;
        this.mediaStartTime = mediaStartTime;
        this.mediaFileDuration = this.mediaEndTime - this.mediaStartTime;
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
    
    public boolean IsHit(long testtime, long range)
    {
        if(testtime >= this.GetStartTime() && (testtime) <= (this.GetStartTime() + range))
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
}

package jvl.comskip;

import java.util.Date;

public class Marker 
{
    private long startTime;
    private long endTime;
    private long offsetTime;
    private long mediaFileStartTime;
    private long mediaFileEndTime;
    private long mediaFileDuration;
    
    /**
     * Constructs a marker object
     * @param startTime Start of the marker
     * @param endTime End of the marker
     * @param offsetTime A time offset of where the marker is in relation to the
     * @param mediaFileStartTime The start time of the mediaFile
     * @param mediaFileEndTime The end time of the mediaFile
     *                   recording time of the file
     */
    public Marker(long startTime, long endTime, long offsetTime, long mediaFileStartTime, long mediaFileEndTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
        this.mediaFileEndTime = mediaFileEndTime;
        this.mediaFileStartTime = mediaFileStartTime;
        this.mediaFileDuration = this.mediaFileEndTime - this.mediaFileStartTime;
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
        if(this.GetStartTime() <= testtime && this.GetStartTime() >= testtime + range)
        {
            return true;
        }
        
        return false;
    }
    
    public double GetStartPercent()
    {
        long markerStartDuration = this.GetStartTime() - this.mediaFileStartTime;

        double temp = ((markerStartDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetEndPercent()
    {
        long markerEndDuration = this.GetEndTime() - this.mediaFileStartTime;

        double temp = ((markerEndDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetDurationPercent()
    {
        return this.GetEndPercent() - this.GetStartPercent();
    }
}

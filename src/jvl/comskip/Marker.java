package jvl.comskip;

import java.util.Date;

public class Marker 
{
    private long startTime;
    private long endTime;
    private long offsetTime;
    private long airingStartTime;
    private long airingEndTime;
    private long airingDuration;
    
    /**
     * Constructs a marker object
     * @param startTime Start of the marker
     * @param endTime End of the marker
     * @param offsetTime A time offset of where the marker is in relation to the
     * @param airingStartTime The start time of the airing
     * @param airingEndTime The end time of the airing
     *                   recording time of the file
     */
    public Marker(long startTime, long endTime, long offsetTime, long airingStartTime, long airingEndTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
        this.airingEndTime = airingEndTime;
        this.airingStartTime = airingStartTime;
        this.airingDuration = this.airingEndTime - this.airingStartTime;
    }
    
    public long GetStartTime()
    {
        return startTime + offsetTime;
    }
    
    public long GetEndTime()
    {
        return endTime + offsetTime;
    }
    
    public int getAiringStartPercent()
    {
        long markerStartDuration = this.GetStartTime() - this.airingStartTime;

        double temp = ((markerStartDuration * 1.0) / (airingDuration * 1.0) * 100.0);
        int ret = (int)java.lang.Math.round(temp);
        
        return ret;
    }
    
    public int getAiringEndPercent()
    {
        long markerEndDuration = this.GetEndTime() - this.airingStartTime;

        double temp = ((markerEndDuration * 1.0) / (airingDuration * 1.0) * 100.0);
        int ret = (int)java.lang.Math.round(temp);
        
        return ret;
    }
    
    public int getMarkerDurationPercent()
    {
        return this.getAiringEndPercent() - this.getAiringStartPercent();
    }
}

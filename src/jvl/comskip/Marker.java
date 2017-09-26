package jvl.comskip;

import java.util.Date;

public class Marker 
{
    private long startTime;
    private long endTime;
    private long offsetTime;
    
    /**
     * Constructs a marker object
     * @param startTime Start of the marker
     * @param endTime End of the marker
     * @param offsetTime A time offset of where the marker is in relation to the
     *                   recording time of the file
     */
    public Marker(long startTime, long endTime, long offsetTime)
    {
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetTime = offsetTime;
    }
    
    public long GetStartTime()
    {
        return startTime + offsetTime;
    }
    
    public long GetEndTime()
    {
        return endTime + offsetTime;
    }
}

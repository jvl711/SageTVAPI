
package jvl.sage.api;

import java.io.File;
import jvl.sage.SageCallApiException;



public class MediaFileSegment
{
    public static final String EDL_EXT = "edl";
    
    private MediaFile mediaFile;
    private int segment;
    private long mediaFileDuration;
    private String filePath;
    
    
    public MediaFileSegment(MediaFile mediaFile, int segment, String filePath) throws SageCallApiException
    {
        this.mediaFile = mediaFile;
        this.segment = segment;
        this.mediaFileDuration = mediaFile.GetFileEndTime() - mediaFile.GetFileStartTime();
        this.filePath = filePath;
    }
    
    public long GetStartTime() throws SageCallApiException
    {
        return this.mediaFile.GetStartForSegment(this.segment);
    }
    
    public long GetEndTime() throws SageCallApiException
    {
        return this.mediaFile.GetEndForSegment(this.segment);
    }
    
    public long GetDuration() throws SageCallApiException
    {
        return this.mediaFile.GetDurationForSegment(segment);
    }
    
    public int GetStartPercent() throws SageCallApiException
    {
        long segmentStartDuration = this.GetStartTime() - this.mediaFile.GetFileStartTime();

        double temp = ((segmentStartDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        int ret = (int)java.lang.Math.round(temp);
        
        return ret;
    }
    
    public int GetEndPercent() throws SageCallApiException
    {
        long segmentEndDuration = this.GetEndTime() - this.mediaFile.GetFileStartTime();

        double temp = ((segmentEndDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        int ret = (int)java.lang.Math.round(temp);
        
        return ret;
    }
    
    public int GetDurationPercent() throws SageCallApiException
    {
        return this.GetEndPercent() - this.GetStartPercent();
    }
    
    public String getEDLFileName()
    {
        return filePath.substring(0, filePath.lastIndexOf(".") + 1) + EDL_EXT;
    }
    
    public boolean hasComskipFile() throws SageCallApiException
    {
        String contents = Utility.GetFileAsString(new File(this.getEDLFileName()));
        
        return contents.length() > 0;
    }
}

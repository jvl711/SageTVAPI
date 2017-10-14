
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
        
        if(mediaFile.IsFileCurrentlyRecording())
        {
            this.mediaFileDuration = mediaFile.GetAiring().GetAiringEndTime() - mediaFile.GetAiring().GetAiringStartTime();
        }
        else
        {
            this.mediaFileDuration = mediaFile.GetFileEndTime() - mediaFile.GetFileStartTime();
        }
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
    
    public double GetStartPercent() throws SageCallApiException
    {
        long startTime;
        
        if(mediaFile.IsFileCurrentlyRecording())
        {
            startTime = mediaFile.GetAiring().GetAiringStartTime();
        }
        else
        {
            startTime = this.mediaFile.GetFileStartTime();
        }
        
        long segmentStartDuration = this.GetStartTime() - startTime;

        double temp = ((segmentStartDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetEndPercent() throws SageCallApiException
    {
        long startTime;
        
        if(mediaFile.IsFileCurrentlyRecording())
        {
            startTime = mediaFile.GetAiring().GetAiringStartTime();
        }
        else
        {
            startTime = this.mediaFile.GetFileStartTime();
        }
        
        long segmentEndDuration = this.GetEndTime() - startTime;

        double temp = ((segmentEndDuration * 1.0) / (mediaFileDuration * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetDurationPercent() throws SageCallApiException
    {
        return this.GetEndPercent() - this.GetStartPercent();
    }
    
    public String GetEDLFileName()
    {
        return filePath.substring(0, filePath.lastIndexOf(".") + 1) + EDL_EXT;
    }
    
    public boolean HasComskipFile() throws SageCallApiException
    {
        String contents = Utility.GetFileAsString(new File(this.GetEDLFileName()));
        
        return contents.length() > 0;
    }
}

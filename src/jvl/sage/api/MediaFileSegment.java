
package jvl.sage.api;

import java.io.File;
import jvl.sage.SageCallApiException;



public class MediaFileSegment
{
    public static final String EDL_EXT = "edl";
    
    private MediaFile mediaFile;
    private int segment;
    //private long mediaFileDuration;
    private String filePath;
    private String fileName;
    
    
    public MediaFileSegment(MediaFile mediaFile, int segment, String filePath) throws SageCallApiException
    {
        this.mediaFile = mediaFile;
        this.segment = segment;
        
        
        
//        if(mediaFile.IsFileCurrentlyRecording())
//        {
//            this.mediaFileDuration = mediaFile.GetAiring().GetAiringEndTime() - mediaFile.GetAiring().GetAiringStartTime();
//        }
//        else
//        {
//            this.mediaFileDuration = mediaFile.GetFileEndTime() - mediaFile.GetFileStartTime();
//        }
        

        this.filePath = filePath;
        File file = new File(filePath);
        this.fileName = file.getName();
    }
    
//    public long GetMediaDuration() throws SageCallApiException
//    {
//        return mediaFile.GetAiring().GetScheduleEndTime() - mediaFile.GetAiring().GetScheduleStartTime();
//    }
    
    
    public String GetFilePath()
    {
        return this.filePath;
    }
    
    public String GetFileName()
    {
        return this.fileName;
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
    
    /*
     * StartTime Percent based on the scheduled start time of airing.
    */
    public double GetStartPercent() throws SageCallApiException
    {
        //long startTime = mediaFile.GetAiring().GetScheduleStartTime();
        
        /*
        if(mediaFile.IsFileCurrentlyRecording())
        {
            startTime = mediaFile.GetAiring().GetAiringStartTime();
        }
        else
        {
            startTime = this.mediaFile.GetFileStartTime();
        }
        */
        
        long segmentStartDuration = this.GetStartTime() - this.mediaFile.GetMediaStartTime();
        
        double temp = ((segmentStartDuration * 1.0) / (this.mediaFile.GetMediaDuration() * 1.0) * 100.0);
        //int ret = (int)java.lang.Math.round(temp);
        
        return temp;
    }
    
    public double GetEndPercent() throws SageCallApiException
    {
        //long startTime = mediaFile.GetAiring().GetScheduleStartTime();
        
        /*
        if(mediaFile.IsFileCurrentlyRecording())
        {
            startTime = mediaFile.GetAiring().GetAiringStartTime();
        }
        else
        {
            startTime = this.mediaFile.GetFileStartTime();
        }
        */
        long segmentEndDuration = this.GetEndTime() - this.mediaFile.GetMediaStartTime();

        double temp = ((segmentEndDuration * 1.0) / (this.mediaFile.GetMediaDuration() * 1.0) * 100.0);
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

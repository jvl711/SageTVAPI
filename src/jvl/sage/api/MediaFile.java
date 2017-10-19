
package jvl.sage.api;

import java.io.File;
import java.util.ArrayList;
import jvl.comskip.Marker;
import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;


public class MediaFile extends SageObject
{
    private Object mediafile;

    public MediaFile(Object mediafile)
    {
        //TODO:  Check to see what object type this is, and convert accordingly.
        
        this.mediafile = mediafile;
    }

    /**
     * Returns true if the specified object is a MediaFile object. No automatic 
     * type conversion will be performed on the argument. This will return false 
     * if the argument is a MediaFile object, BUT that object no longer exists 
     * in the SageTV database.
     * 
     * @param testObject The object to test
     * @return True if it is a media file object.  False otherwise.
     * @throws SageCallApiException 
     */
    public static boolean IsMediaFileObject(Object testObject) throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsMediaFileObject", testObject);
    }
    
    /**
     * Returns true if the specified object is a MediaFile object. No automatic 
     * type conversion will be performed on the argument. This will return false 
     * if the argument is a MediaFile object, BUT that object no longer exists 
     * in the SageTV database.
     * 
     * @return True if it is a media file object.  False otherwise.
     * @throws SageCallApiException 
     */
//    public boolean IsMediaFileObject() throws SageCallApiException
//    {
//        return MediaFile.callAPIBoolean("IsMediaFileObject", this.mediafile);
//    }

    public static Object GetMediaFileAiring(Object mediaFile) throws SageCallApiException
    {
        return MediaFile.callApiObject("GetMediaFileAiring", mediaFile);
    }
    
    public Object GetMediaFileAiring() throws SageCallApiException
    {
        return MediaFile.callApiObject("GetMediaFileAiring", this.mediafile);
    }
    
    /**
     * This will be the earlier of the Media Start or Scheduled start.
     * 
     * @return Long the time the file was recorded or imported in real time
     * @throws SageCallApiException 
     */
    public long GetMediaStartTime() throws SageCallApiException
    {
        long temp = this.GetFileStartTime();
        
        if(this.GetAiring().GetAiringStartTime() < temp)
        {
            temp = this.GetAiring().GetAiringStartTime();
        }
        
        if(this.GetAiring().GetScheduleStartTime() < temp)
        {
            temp = this.GetAiring().GetScheduleStartTime();
        }
        
        return temp;
    }
    
    //This will be the latest of the Media Start or Scheduled end
    public long GetMediaEndTime() throws SageCallApiException
    {
        long temp = this.GetFileEndTime();
        
        if(this.GetAiring().GetAiringEndTime() > temp)
        {
            temp = this.GetAiring().GetAiringEndTime();
        }
        
        if(this.GetAiring().GetScheduleEndTime() > temp)
        {
            temp = this.GetAiring().GetScheduleEndTime();
        }
            
        return temp;
    }

    public long GetMediaDuration() throws SageCallApiException
    {
        return this.GetMediaEndTime() - this.GetMediaStartTime();
    }
    
    public Show GetShow()
    {
        return new Show(this.UnwrapObject());
    }
    
    public Airing GetAiring()
    {
        return new Airing(this.UnwrapObject());
    }
    
    public String GetVideoResolution() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Resolution");
        
        return ret;
    }
    public String GetVideoAspect() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Aspect");
        
        return ret;
    }
    
    public String GetVideoBitrate() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Bitrate");
        
        return ret;
    }
    
    public String GetVideoCodec() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Codec");
        
        return ret;
    }
    
    public String GetVideoHeight() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Height");
        
        return ret;
    }
    
    public String GetVideoWidth() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Width");
        
        return ret;
    }
    
    public String GetVideoFPS() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.FPS");
        
        return ret;
    }
    
    /*
     Format.Video.Codec
     Format.Video.Resolution
     Format.Video.Aspect
     Format.Video.Bitrate
     Format.Video.Width
     Format.Video.Height
     Format.Video.FPS
     Format.Video.Interlaced 
     Format.Video.Progressive
     Format.Video.Index
     Format.Video.ID
     Format.Audio.NumStreams
     Format.Audio[.#].Codec
     Format.Audio[.#].Channels
     Format.Audio[.#].Language
     Format.Audio[.#].SampleRate
     Format.Audio[.#].BitsPerSample
     Format.Audio[.#].Index
     Format.Audio[.#].ID
     Format.Subtitle.NumStreams
     Format.Subtitle[.#].Codec
     Format.Subtitle[.#].Language
     Format.Subtitle[.#].Index
     Format.Subtitle[.#].ID
     Format.Container
    */
    
    public String GetMetadata(String name) throws SageCallApiException
    {
        String ret;
        
        ret = SageAPI.callApiString("GetMediaFileMetadata", this.mediafile, name);
        
        return ret;
    }
 
    public long GetSize() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetSize", this.mediafile);
        
        return ret;
    }
    
    public boolean IsFileCurrentlyRecording() throws SageCallApiException
    {
        boolean ret;
        
        ret = SageAPI.callAPIBoolean("IsFileCurrentlyRecording", mediafile);
        
        return ret;
    }
    
    public boolean DeleteFile() throws SageCallApiException
    {
        boolean ret;
        
        ret = SageAPI.callAPIBoolean("DeleteFile", mediafile);
        
        return ret;
    }
    
    public File [] GetSegmentFiles() throws SageCallApiException
    {
        File [] ret;
        
        ret = (File [])SageAPI.callApiArray("GetSegmentFiles", mediafile);
        
        return ret;
    }
    
    public long GetStartForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetStartForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    public long GetEndForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetEndForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    
    public long GetDurationForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetDurationForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    public long GetFileStartTime() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetFileStartTime", mediafile);
        
        return ret;
    }
    
    public long GetFileEndTime() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetFileEndTime", mediafile);
        
        return ret;
    }
    
    public MediaFileSegment [] GetMediaFileSegments() throws SageCallApiException
    {
        File [] files = this.GetSegmentFiles();
        MediaFileSegment [] segments = new MediaFileSegment[files.length];
        
        for(int i = 0; i < files.length; i++)
        {
            MediaFileSegment mfs = new MediaFileSegment(this, i, files[i].getAbsolutePath());
            segments[i] = mfs;
        }
        
        return segments;
    }
    
    public Marker [] GetCommercialMarkers() throws SageCallApiException
    {
        MediaFileSegment [] segments = this.GetMediaFileSegments();
        ArrayList<Marker> temp = new ArrayList<Marker>();
        
        for(int i = 0; i < segments.length; i++)
        {
            String fileContents = Utility.GetFileAsString(new File(segments[i].GetEDLFileName()));
            
            if(!fileContents.equals(""))
            {
                String [] lines = fileContents.split("\n");
                
                for(int j = 0; j < lines.length; j++)
                {   
                    String [] cuttimes = lines[j].split("\t");

                    long startTime = (long)(Double.parseDouble(cuttimes[0]) * 1000);
                    long endTime = (long)(Double.parseDouble(cuttimes[1]) * 1000);
                   
                    Marker marker = new Marker(startTime, endTime, segments[i].GetStartTime(), this.GetMediaStartTime(), this.GetMediaEndTime());
                    temp.add(marker);
                }                
            }
            else
            {
                System.out.println("Debug - edl file was empty or not found: " + segments[i].GetEDLFileName());
            }
        }
        
        return (Marker [])temp.toArray(new Marker[temp.size()]);
    }
    
    @Override
    public String toString() 
    {
        String output = "";
        
        try
        {
            MediaFileSegment segments [] = this.GetMediaFileSegments();
            output = "";
            
            for(int i = 0; i < segments.length; i++)
            {
                output += "Segment " + i + ": ";
                //output += " StartTime = " + markers[i].GetStartTime();
                //output += " EndTime = " + markers[i].GetEndTime();
                output += " Start Percent = " + segments[i].GetStartPercent();
                output += " End Percent = " + segments[i].GetEndPercent();
                output += " Duration Percent = " + segments[i].GetDurationPercent() + "\n";
            }
        }
        catch(Exception ex)
        {
            
        }
        
        if(output.length() == 0)
        {
            output = "No Segments!\n";
        }
        
        try
        {
            Marker [] markers = this.GetCommercialMarkers();
            //output = "";
            
            for(int i = 0; i < markers.length; i++)
            {
                output += "Marker " + i + ": ";
                //output += " StartTime = " + markers[i].GetStartTime();
                //output += " EndTime = " + markers[i].GetEndTime();
                output += " Start Percent = " + markers[i].GetStartPercent();
                output += " End Percent = " + markers[i].GetEndPercent();
                output += " Duration Percent = " + markers[i].GetDurationPercent() + "\n";
            }
        }
        catch(Exception ex)
        {
            output += " Error getting markers: " + ex.getMessage();
            ex.printStackTrace();
        }
        if(output.length() == 0)
        {
            output = "No Markers!\n";
        }

        return output;
    }
    
    @Override
    public Object UnwrapObject() 
    {
        return mediafile;
    }
    
}

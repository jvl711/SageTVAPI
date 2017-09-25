
package jvl.sage.api;

import java.io.File;
import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;


public class MediaFile extends SageObject
{
    private Object mediafile;

    public MediaFile(Object mediafile)
    {
        this.mediafile = mediafile;
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
    
    @Override
    public Object UnwrapObject() 
    {
        return mediafile;
    }
    
}

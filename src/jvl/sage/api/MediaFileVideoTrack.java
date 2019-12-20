
package jvl.sage.api;

import java.text.DecimalFormat;


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
*/

public class MediaFileVideoTrack
{
    private int tracknum;
    private String codec;
    private String aspect;
    private int width;
    private int height;
    private double fps;
    private boolean progressive;
    
    public MediaFileVideoTrack(int tracknum, String codec, String aspect, int width, int height, double fps, boolean progressive)
    {
        this.tracknum = tracknum;
        this.codec = codec;
        this.aspect = aspect;
        this.width = width;
        this.height = height;
        this.fps = fps;
        this.progressive = progressive;
    }
    
    public String GetCodecString()
    {
        if(this.codec.equalsIgnoreCase("mpeg2-video"))
        {
            return "MPEG2";
        }
        else
        {
            return codec;
        }
    }
    
    public String GetFPSString()
    {
        DecimalFormat format = new DecimalFormat("##.###");
        
        return format.format(fps);
    }
    
    public String GetAspectRatio()
    {
        return this.aspect;
    }
    
    public String GetResolutionString()
    {
        String resolution;
        
        if(this.width >= 3840)
        {
            resolution = "2160";
        }
        else if(this.width >= 1920)
        {
            resolution = "1080";
        }
        else if(this.width >= 1280)
        {
            resolution = "720";
        }
        else if(this.width >= 640)
        {
            resolution = "480";
        }
        else
        {
            resolution = "<480";
        }
            
        if(this.progressive)
        {
            resolution += "p";
        }
        else
        {
            resolution += "i";
        }
        
        return resolution;
    }
    
    public String toString(int format)
    {
        return this.GetResolutionString() + ", " + this.GetAspectRatio() + ", " + this.GetFPSString() + "fps, " + this.GetCodecString();
    }
    
    @Override
    public String toString()
    {
        return this.toString(1);
    }
}

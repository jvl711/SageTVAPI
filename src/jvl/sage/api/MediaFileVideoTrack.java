
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
    public int tracknum;
    public String codec;
    public int width;
    public int height;
    public double fps;
    public boolean progressive;
    
    public MediaFileVideoTrack(int tracknum, String codec, int width, int height, double fps, boolean progressive)
    {
        this.tracknum = tracknum;
        this.codec = codec;
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
    
}

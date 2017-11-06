
package jvl.sage.api;


public class MediaFileSubtitleTrack 
{
    //private MediaFile mediaFile;
    private int tracknum;
    private String desription;
    
    /*
    public MediaFileSubtitle(MediaFile mediaFile, int tracknum, String description)
    {
        this.mediaFile = mediaFile;
        this.tracknum = tracknum;
        this.desription = description;
    }
    */
    
    public MediaFileSubtitleTrack(int tracknum, String description)
    {
        
        this.tracknum = tracknum;
        this.desription = description;
    }
    
    /*
    public MediaFile GetMediaFile()
    {
        return this.mediaFile;
    }
    */
    
    public int GetTrackNumber()
    {
        return this.tracknum;
    }
    
    @Override
    public String toString()
    {
        return this.desription;
    }
}

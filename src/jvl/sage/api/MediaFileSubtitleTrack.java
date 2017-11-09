
package jvl.sage.api;


public class MediaFileSubtitleTrack 
{
    //private MediaFile mediaFile;
    private int tracknum;
    private String description;
    
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
        this.description = description;
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
    
    public String GetDescription()
    {
        return this.description;
    }
    
    public static MediaFileSubtitleTrack GetNullTrack()
    {
        return new MediaFileSubtitleTrack(-1, "None");
    }
    
    @Override
    public boolean equals(Object test)
    {
        if(test == null || !(test instanceof  MediaFileSubtitleTrack))
        {
            return false;
        }

        return this.tracknum == ((MediaFileSubtitleTrack)test).tracknum && this.description.equals(((MediaFileSubtitleTrack)test).description);
    }

    @Override
    public int hashCode() 
    {
        int hash = 7;
        hash = 79 * hash + this.tracknum;
        hash = 79 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString()
    {
        return this.description;
    }
}

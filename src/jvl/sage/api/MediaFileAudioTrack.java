
package jvl.sage.api;


public class MediaFileAudioTrack 
{
    private int tracknum;
    private String description;
    
    public MediaFileAudioTrack(int tracknum, String description)
    {
        this.tracknum = tracknum;
        this.description = description;
    }
    
    public int GetTrackNumber()
    {
        return this.tracknum;
    }

    public String GetDescription()
    {
        return this.description;
    }
    
    public static MediaFileAudioTrack GetNullTrack()
    {
        return new MediaFileAudioTrack(-1, "Off");
    }
    
    @Override
    public boolean equals(Object test)
    {
        if(test == null || !(test instanceof  MediaFileAudioTrack))
        {
            return false;
        }
        
        return this.tracknum == ((MediaFileAudioTrack)test).tracknum && this.description.equals(((MediaFileAudioTrack)test).description);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + this.tracknum;
        hash = 23 * hash + (this.description != null ? this.description.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString()
    {
        return this.description;
    }
}

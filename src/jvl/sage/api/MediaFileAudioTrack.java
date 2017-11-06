
package jvl.sage.api;


public class MediaFileAudioTrack 
{
    private int tracknum;
    private String desription;
    
    public MediaFileAudioTrack(int tracknum, String description)
    {
        this.tracknum = tracknum;
        this.desription = description;
    }
    
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

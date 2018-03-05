
package jvl.playback;

/**
 * Determines playback mode. Single, Multiple, Random, etc...
 * 
 * @author jvl711
 */
public enum PlaybackOptions 
{
    LIVE_TV(1, "Live TV"),
    SINGLE(2, "Single"),
    MULTIPLE(3, "Multiple All"),
    MULTIPLE_UNWATCHED(4, "Multiple Unwatched"),
    MULTIPLE_RANDOM(5, "Multiple Random");
    
    private int id;
    private String desc;
    
    private PlaybackOptions(int id, String desc)
    {
        this.id = id;
        this.desc = desc;
    }
    
    public int GetId()
    {
        return id;
    }
    
    public String GetDesc()
    {
        return desc;
    }
    
    @Override
    public String toString()
    {
        return desc;
    }
}

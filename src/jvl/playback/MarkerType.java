
package jvl.playback;


public enum MarkerType 
{
    COMMERCIAL(1, "Commercial"),
    CHAPTER(2, "Chapter");
    
    private int type;
    private String desc;
    
    private MarkerType(int type, String desc)
    {
        this.type = type;
        this.desc = desc;
    }
    
    public int GetType()
    {
        return type;
    }
    
    public String getDescription()
    {
        return desc;
    }
}

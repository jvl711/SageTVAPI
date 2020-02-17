
package jvl.sage.api;


public class MediaFileSubtitleTrack 
{
    //private MediaFile mediaFile;
    private int tracknum;
    private String description;
    private String codec;
    private String language;
    private boolean forced;
    
    /*
    public MediaFileSubtitle(MediaFile mediaFile, int tracknum, String description)
    {
        this.mediaFile = mediaFile;
        this.tracknum = tracknum;
        this.desription = description;
    }
    */
    
    public MediaFileSubtitleTrack(int tracknum, String description, String codec, String language, boolean forced)
    {
        
        this.tracknum = tracknum;
        this.description = description;
        this.codec = codec;
        this.language = language;
        this.forced = forced;
    }
    
    public int GetTrackNumber()
    {
        return this.tracknum;
    }
    
    public String GetDescription()
    {
        return this.description;
    }
    
    public String GetLanguage()
    {
        return this.language;
    }
    
    public String GetCodecString()
    {
        if(this.GetCodec().equalsIgnoreCase("0x0000"))
        {
            return "TEXT";
        }
        else
        {
            return this.GetCodec();
        }
    }
    
    public String GetCodec()
    {
        return this.codec;
    }
    
    public boolean isForced()
    {
        return this.forced;
    }
    
    public String GetLanguageString()
    {
        
        //https://www.loc.gov/standards/iso639-2/php/code_list.php
        //This may be the full list
        
        if(this.GetLanguage().equalsIgnoreCase("eng"))
        {
            return "English";
        }
        else if(this.GetLanguage().equalsIgnoreCase("fre"))
        {
            return "French";
        }
        else if(this.GetLanguage().equalsIgnoreCase("spa"))
        {
            return "Spanish";
        }
        if(this.GetLanguage().equals(""))
        {
            return "Unknown";
        }
        else
        {
            return this.GetLanguage();
        }
        
    }
    
    public static MediaFileSubtitleTrack GetNullTrack()
    {
        return new MediaFileSubtitleTrack(-1, "Off", "", "", false);
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
    
    public String toString(int format)
    {
        if(format == 1)
        {
            return this.GetLanguageString() + ", " + this.GetCodecString() + (this.isForced() ? " [Forced]" : "");
        }
        else
        {
            if(!this.GetLanguageString().equals("") && !this.GetCodecString().equals(""))
            {
                return (this.GetTrackNumber() + 1) + " - " + this.GetLanguageString() + ", " + this.GetCodecString() + (this.isForced() ? " [Forced]" : "");
            }
            else
            {    
                return this.GetDescription();
            }
        }
    }
    
    @Override
    public String toString()
    {
        return this.toString(-1);
    }
}

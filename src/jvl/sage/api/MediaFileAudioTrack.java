
package jvl.sage.api;


public class MediaFileAudioTrack 
{
    private String description;
    private int tracknum;
    private String codec;
    //private String codecDisplay;
    private int channels;
    //private String channelsDisplay;
    private String language;
    private int bitrate;
    private int samplerate;
    
    
    
    public MediaFileAudioTrack(int tracknum, String description, String codec, String channels, int bitrate, int samplerate, String language)
    {
        this.tracknum = tracknum;
        this.description = description;
        
        this.codec = codec;
        
        
        try{ this.channels = Integer.parseInt(channels); } catch(Exception ex) { this.channels = 0; }
        
        this.bitrate = bitrate;
        this.samplerate = samplerate;
        
        this.language = language;
    }
    
    public int GetBitrate()
    {
        return this.bitrate;
    }
    
    public String GetBitrateString()
    {
        if(this.bitrate > 0)
        {
            return (this.bitrate / 1000) + " kbps";
        }
        
        return "";
    }
    
    public int GetSampleRate()
    {
        return this.samplerate;
    }
    
    public String GetSampleRateString()
    {
        if(this.samplerate > 0)
        {
            return (this.samplerate / 1000) + ".0 KHz";
        }
        
        return "";
    }
    
    public int GetTrackNumber()
    {
        return this.tracknum;
    }

    
    
    public String GetDescription()
    {
        return this.description;
    }
    
    public String GetCodec()
    {
        return this.codec;
    }
    
    public String GetCodecString()
    {
        if(codec.equalsIgnoreCase("DCA"))
        {
            return "DTS";
        }
        else
        {
            return codec;
        }
    }
    
    public int GetChannels()
    {
        return this.channels;
    }
    
    public String GetChannelString()
    {
        if(this.channels > 2)
        {
            return (this.channels - 1) + ".1";
        }
        else 
        {
            return this.channels + "";
        }
    }
    
    public String GetLanguage()
    {
        return this.language;
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
    
    public static MediaFileAudioTrack GetNullTrack()
    {
        return new MediaFileAudioTrack(-1, "Off", "", "", 0, 0, "");
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
    
    public String toString(int format)
    {
        String temp;
        
        if(format == 1)
        {
            temp = this.GetLanguageString();
            
            if(!this.GetBitrateString().equals(""))
            {
                temp += ", " + this.GetBitrateString();
            }
                
            if(!this.GetSampleRateString().equalsIgnoreCase(""))
            {
                temp += ", " + this.GetSampleRateString();
            }
                   
            temp +=  ", " + this.GetChannelString() + " channels, " + this.GetCodecString(); 
        }
        else
        {
            temp = this.GetLanguageString() + ", " + this.GetCodecString() + ", " + this.GetChannelString();
        }
        
        return temp;
    }
    
    @Override
    public String toString()
    {
        return toString(-1);
    }
}

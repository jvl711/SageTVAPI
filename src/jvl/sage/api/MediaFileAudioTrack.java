
package jvl.sage.api;


public class MediaFileAudioTrack 
{
    private String description;
    private int tracknum;
    private String codec;
    private String codecDisplay;
    private int channels;
    private String channelsDisplay;
    private String language;
    private int bitrate;
    private int samplerate;
    
    
    
    public MediaFileAudioTrack(int tracknum, String description, String codec, String channels, int bitrate, int samplerate, String language)
    {
        this.tracknum = tracknum;
        this.description = description;
        
        this.codec = codec;
        if(codec.equalsIgnoreCase("DCA"))
        {
            this.codecDisplay = "DTS";
        }
        else
        {
            this.codecDisplay = codec;
        }
        
        try{ this.channels = Integer.parseInt(channels); } catch(Exception ex) { this.channels = 0; }
        
        if(this.channels > 2)
        {
            this.channelsDisplay = (this.channels - 1) + ".1";
        }
        else 
        {
            this.channelsDisplay = channels;
        }
        
        this.bitrate = bitrate;
        this.samplerate = samplerate;
        
        this.language = language;
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
    
    public int GetChannels()
    {
        return this.channels;
    }
    
    public String GetChannelString()
    {
        return this.channelsDisplay;
    }
    
    public String GetLanguages()
    {
        return this.language;
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
            String rate = (this.bitrate / 1024) + " kbps";
            String sampleRateDisplay = (this.samplerate / 1000) + ".0 KHz";
                   
            temp = this.language + ", " + rate + ", " + sampleRateDisplay + ", " + this.channelsDisplay + " channels, " + this.codecDisplay; 
        }
        else
        {
            temp = this.codec + " " + this.channelsDisplay;

            if (!this.language.isEmpty())
            {
                temp += " - " + this.language;
            }
        }
        
        return temp;
    }
    
    @Override
    public String toString()
    {
        return toString(-1);
    }
}

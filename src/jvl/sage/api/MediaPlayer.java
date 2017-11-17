
package jvl.sage.api;

import java.util.ArrayList;
import jvl.sage.Debug;
import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;

public class MediaPlayer extends SageAPI
{
    public static long GetMediaTime(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callApiLong(context, "GetMediaTime");
    }
    
    public static boolean IsMediaPlayerLoaded(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "IsMediaPlayerFullyLoaded");
    }
    
    public static boolean IsCurrentMediaFileRecording(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "IsCurrentMediaFileRecording");
    }
    
    public static boolean HasMediaFile(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "HasMediaFile");
    }
    
    public static Object GetCurrentMediaFile(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callApiObject(context, "GetCurrentMediaFile");

    }
    
    public static int GetChapterCount(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callApiInt(context, "GetDVDNumberOfChapters");
    }
    
    public static int GetCurrentChapter(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callApiInt(context, "GetDVDCurrentChapter");
    }
    
    public static void SetChapter(UIContext context, int chapternum) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "DVDChapterSet", chapternum);
    }
    
    /**
     * Returns the currently selected Subtitle, or the NullTrack which represents 
     * that there is no track selected.  The TrackNmber is -1 and None is the desc
     * @param context The current UI context the video is playing in.
     * @return MediaFileSubtitleTrack describing witch track is selected if one is
     * @throws SageCallApiException 
     */
    public static MediaFileSubtitleTrack GetCurrentSubtitleTrack(UIContext context) throws SageCallApiException
    {
        Debug.Writeln("GetCurrentSubtitleTrack Called", Debug.INFO);
        
        ArrayList<MediaFileSubtitleTrack> subtitles = MediaPlayer.GetSubtitleTracks(context);
        String currentDesc = MediaPlayer.callApiString(context, "GetDVDCurrentSubpicture");
        
        Debug.Writeln("Current Subtitle Desc: " + currentDesc, Debug.INFO);
        
        for(int i = 0; i < subtitles.size(); i++)
        {
            if(subtitles.get(i).GetDescription().equals(currentDesc))
            {
                Debug.Writeln("Subtitle found in collection: " + subtitles.get(i), Debug.INFO);
                return subtitles.get(i);
            }
        }
        
        Debug.Writeln("Subtitle not found in collection returning null track", Debug.INFO);
        return MediaFileSubtitleTrack.GetNullTrack();
    }
    
    public static ArrayList<MediaFileSubtitleTrack> GetSubtitleTracks(UIContext context) throws SageCallApiException
    {
        Debug.Writeln("GetSubtitleTracks Called", Debug.INFO);
        
        ArrayList<MediaFileSubtitleTrack> subtitles = new ArrayList<MediaFileSubtitleTrack>(); 
    
        String [] temp = (String [])MediaPlayer.callApiArray(context, "GetDVDAvailableSubpictures");
        
        Debug.Writeln("\tGetting subtitles count: " + temp.length, Debug.INFO);
        
        for(int i = 0; i < temp.length; i++)
        {
            Debug.Writeln("\tAdding subtitle to collection: " + temp[i], Debug.INFO);
            
            subtitles.add(new MediaFileSubtitleTrack(i, temp[i]));
        }   
        
        //Add the null selected track as position 0
        Debug.Writeln("\tAdding Null Track to collection", Debug.INFO);
        subtitles.add(0, MediaFileSubtitleTrack.GetNullTrack());
        
        return subtitles;
    }
    
    public static void SetSubtitleTrack(UIContext context, int tracknum) throws SageCallApiException
    {
        Debug.Writeln("SetSubtitleTrack Called: " + tracknum, Debug.INFO);
        
        //If it is -1 than turn off subtitles
        if(tracknum == -1)
        {
            Debug.Writeln("\tNull Track Passed, checking to see if subtitles should be turned off", Debug.INFO);
            if(!MediaPlayer.GetCurrentSubtitleTrack(context).equals(MediaFileSubtitleTrack.GetNullTrack()))
            {
                Debug.Writeln("\tSubtitles on. Toggling display off", Debug.INFO);
                MediaPlayer.callApi(context, "DVDSubtitleToggle");
            }
        }
        else
        {
            Debug.Writeln("\tSetting subtitle track: " + tracknum, Debug.INFO);
            MediaPlayer.callApi(context, "DVDSubtitleChange", tracknum);
        }
    }
    
    public static void SetSubtitleTrack(UIContext context, MediaFileSubtitleTrack sub) throws SageCallApiException
    {
        Debug.Writeln("SetSubtitleTrack called with TrackNum: " + sub.GetTrackNumber(), Debug.INFO);
        
        MediaPlayer.SetSubtitleTrack(context, sub.GetTrackNumber());
    }
    
    public static MediaFileAudioTrack GetCurrentAudioTrack(UIContext context) throws SageCallApiException
    {
        Debug.Writeln("GetCurrentAudioTrack Called", Debug.INFO);
        String current = MediaPlayer.callApiString(context, "GetDVDCurrentLanguage");
        Debug.Writeln("\tcurrent: " + current, Debug.INFO);
        ArrayList<MediaFileAudioTrack> audio = MediaPlayer.GetAudioTracks(context);
        
        //Work around where sage says empty when there is only one track
        if(current.isEmpty() && audio.size() == 1)
        {
            return audio.get(0);
        }
        
        for(int i = 0; i < audio.size(); i++)
        {
            if(audio.get(i).GetDescription().equals(current))
            {
                return audio.get(i);
            }
        }
        
        return MediaFileAudioTrack.GetNullTrack();
    }
    
    public static ArrayList<MediaFileAudioTrack> GetAudioTracks(UIContext context) throws SageCallApiException
    {
        Debug.Writeln("GetAudioTracks Called", Debug.INFO);
                
        String [] tracks = (String [])MediaPlayer.callApiArray(context, "GetDVDAvailableLanguages");
        
        ArrayList<MediaFileAudioTrack> audio = new ArrayList<MediaFileAudioTrack>();
        
        Debug.Writeln("\tAudio tracks returned: " + tracks.length, Debug.INFO);
        
        MediaFile mediaFile = new MediaFile(MediaPlayer.GetCurrentMediaFile(context));
        
        for(int i = 0; i < tracks.length; i++)
        {
            audio.add(new MediaFileAudioTrack(i, tracks[i], mediaFile.GetAudioCodec(i), mediaFile.GetAudioChannels(i), mediaFile.GetAudioLanguage(i)));
        }
    
        //Check to see if MediaFile has Audio Data
        if(tracks.length == 0 && (!mediaFile.GetAudioChannels(0).isEmpty()))
        {
            audio.add(new MediaFileAudioTrack(0, mediaFile.GetAudioCodec(0) + " " + mediaFile.GetAudioChannels(0) , mediaFile.GetAudioCodec(0), mediaFile.GetAudioChannels(0), mediaFile.GetAudioLanguage(0)));
        }
        
        return audio;
    }
    
    public static void SetAudioTrack(UIContext context, int tracknum) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "DVDAudioChange", tracknum);
    }
    
    public static void SetAudioTrack(UIContext context, MediaFileAudioTrack audio) throws SageCallApiException
    {
        //Can not curently turn off audio
        if(audio.equals(MediaFileAudioTrack.GetNullTrack()))
        {
            return;
        }
        
        MediaPlayer.callApi(context, "DVDAudioChange", audio.GetTrackNumber());
    }
    
    public static void Seek(UIContext context, long time) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "Seek", time);
    }
    
    
}


package jvl.sage.api;

import java.util.ArrayList;
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
        return MediaPlayer.callAPIBoolean(context, "GetCurrentMediaFile");
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
    
    public static String [] GetCurrentSubtitleTrack(UIContext context) throws SageCallApiException
    {
        //TODO: Determine if this really returns an array,
        return (String [])MediaPlayer.callApiArray("GetDVDCurrentSubpicture");
    }
    
    public static ArrayList<MediaFileSubtitleTrack> GetSubtitleTracks(UIContext context) throws SageCallApiException
    {
        ArrayList<MediaFileSubtitleTrack> subtitles = new ArrayList<MediaFileSubtitleTrack>(); 
        
        String [] temp = (String [])MediaPlayer.callApiArray("GetDVDAvailableSubpictures");
        
        for(int i = 0; i < temp.length; i++)
        {
            subtitles.add(new MediaFileSubtitleTrack(i, temp[i]));
        }   
        
        return subtitles;
    }
    
    public static void SetSubtitleTrack(UIContext context, int tracknum) throws SageCallApiException
    {
        //If it is -1 than turn off subtitles
        if(tracknum == -1)
        {
            if(MediaPlayer.GetCurrentSubtitleTrack(context) != null)
            {
                MediaPlayer.callApi(context, "DVDSubtitleToggle");
            }
        }
        else
        {
        
            MediaPlayer.callApi(context, "DVDSubtitleChange", tracknum);
        }
    }
    
    public static void SetSubtitleTrack(UIContext context, MediaFileSubtitleTrack sub) throws SageCallApiException
    {
        MediaPlayer.SetSubtitleTrack(context, sub.GetTrackNumber());
    }
    
    public static String GetCurrentAudioTrack(UIContext context) throws SageCallApiException
    {
        return MediaPlayer.callApiString("GetDVDCurrentLanguage");
    }
    
    public static String [] GetAudioTracks(UIContext context) throws SageCallApiException
    {
        return (String [])MediaPlayer.callApiArray("GetDVDAvailableLanguages");
    }
    
    public static void SetAudioTrack(UIContext context, int tracknum) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "DVDAudioChange", tracknum);
    }
    
    public static void Seek(UIContext context, long time) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "Seek", time);
    }
    
    
}

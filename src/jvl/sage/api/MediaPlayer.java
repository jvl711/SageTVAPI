
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
    
    /**
     * Returns the currently selected Subtitle, or the NullTrack which represents 
     * that there is no track selected.  The TrackNmber is -1 and None is the desc
     * @param context The current UI context the video is playing in.
     * @return MediaFileSubtitleTrack describing witch track is selected if one is
     * @throws SageCallApiException 
     */
    public static MediaFileSubtitleTrack GetCurrentSubtitleTrack(UIContext context) throws SageCallApiException
    {
        ArrayList<MediaFileSubtitleTrack> subtitles = MediaPlayer.GetSubtitleTracks(context);
        String currentDesc = MediaPlayer.callApiString(context, "GetDVDCurrentSubpicture");
        
        for(int i = 0; i < subtitles.size(); i++)
        {
            if(subtitles.get(i).GetDescription().equals(currentDesc))
            {
                return subtitles.get(i);
            }
        }
        
        return MediaFileSubtitleTrack.GetNullTrack();
    }
    
    public static ArrayList<MediaFileSubtitleTrack> GetSubtitleTracks(UIContext context) throws SageCallApiException
    {
        ArrayList<MediaFileSubtitleTrack> subtitles = new ArrayList<MediaFileSubtitleTrack>(); 
        
        String [] temp = (String [])MediaPlayer.callApiArray("GetDVDAvailableSubpictures");
        
        for(int i = 0; i < temp.length; i++)
        {
            subtitles.add(new MediaFileSubtitleTrack(i, temp[i]));
        }   
        
        //Add the none selected track as position 0
        subtitles.add(0, MediaFileSubtitleTrack.GetNullTrack());
        
        return subtitles;
    }
    
    public static void SetSubtitleTrack(UIContext context, int tracknum) throws SageCallApiException
    {
        //If it is -1 than turn off subtitles
        if(tracknum == -1)
        {
            if(!MediaPlayer.GetCurrentSubtitleTrack(context).equals(MediaFileSubtitleTrack.GetNullTrack()))
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

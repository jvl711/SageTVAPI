
package jvl.sage.api;

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
    
    public static void Seek(UIContext context, long time) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "Seek", time);
    }
    
    
}

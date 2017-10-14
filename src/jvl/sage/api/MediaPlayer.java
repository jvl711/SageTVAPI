
package jvl.sage.api;

import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;

public class MediaPlayer extends SageAPI
{
    public static long GetMediaTime(String context) throws SageCallApiException
    {
        return MediaPlayer.callApiLong(context, "GetMediaTime");
    }
    
    public static boolean IsMediaPlayerLoaded(String context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "IsMediaPlayerFullyLoaded");
    }
    
    public static boolean IsCurrentMediaFileRecording(String context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "IsCurrentMediaFileRecording");
    }
    
    public static boolean HasMediaFile(String context) throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean(context, "HasMediaFile");
    }
    
    public static void Seek(String context, long time) throws SageCallApiException
    {
        MediaPlayer.callApi(context, "Seek", time);
    }
}

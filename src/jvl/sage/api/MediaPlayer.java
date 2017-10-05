
package jvl.sage.api;

import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;

public class MediaPlayer extends SageAPI
{
    public static long GetMediaTime() throws SageCallApiException
    {
        return MediaPlayer.callApiLong("GetMediaTime");
    }
    
    public static boolean IsMediaPlayerLoaded() throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean("IsMediaPlayerFullyLoaded");
    }
    
    public static boolean HasMediaFile() throws SageCallApiException
    {
        return MediaPlayer.callAPIBoolean("HasMediaFile");
    }
    
    public static void Seek(long time) throws SageCallApiException
    {
        MediaPlayer.callApi("Seek", time);
    }
}

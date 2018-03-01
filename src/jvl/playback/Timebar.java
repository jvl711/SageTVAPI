
package jvl.playback;

import java.util.ArrayList;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFileAudioTrack;
import jvl.sage.api.MediaFileSegment;
import jvl.sage.api.MediaFileSubtitleTrack;
import jvl.sage.api.MediaPlayer;
import jvl.sage.api.UIContext;


/**
 * This class is to provide functionality to help with the displaying of timebar
 * data on screen.  It also provides automatic commercial skip capabilities
 * @author jolewis
 */
public class Timebar extends Thread
{
    private UIContext context;
    private MediaFile mediaFile;
    private Marker [] commercials;
    private Marker [] chapters;
    private boolean comThreadRun;
    private long sleepCommThread;
    private long sleepOnSkip;
    private long commHitRange;
    
    private static final int DEFAULT_SLEEP_ON_SKIP = 15000;
    private static final int DEFAULT_COMM_HIT_RANGE = 5000;
    

    public Timebar(String context, MediaFile mediaFile) throws SageCallApiException
    {
        this.context = new UIContext(context);
        this.mediaFile = mediaFile;
     
        String mediaType = mediaFile.GetMetadata("MediaType");
        
        try
        {    
            this.commercials = mediaFile.GetCommercialMarkers();
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Exception getting commercial markers.  Setting markers to null.");
            System.out.println("JVL - Error Message: " + ex.getMessage());
            this.commercials = null;
        }
       
        //If it is a movie attempt to load chapters
        if(mediaType.equalsIgnoreCase("movie"))
        {
            try
            {
                chapters = mediaFile.GetChapterMarkers();
            }
            catch(Exception ex)
            {
                System.out.println("JVL - Exception getting chapter markers.  Setting chapters to null.");
                System.out.println("JVL - Error Message: " + ex.getMessage());
                this.chapters = null;
            }
        }
        
        
        this.comThreadRun = false;
        this.sleepCommThread = 0;
        this.sleepOnSkip = Timebar.DEFAULT_SLEEP_ON_SKIP;
        this.commHitRange = Timebar.DEFAULT_COMM_HIT_RANGE;
        
        
    }
    
    /**
     * This is the time the timebar starts at. This will include the padding, 
     * and scheduled recording time where a portion of the recording may have been missed.
     * 
     * @return Returns A time in ticks of when the recording started or when it was imported
     * @throws SageCallApiException 
     */
    public long GetStartTime() throws SageCallApiException
    {
        return this.mediaFile.GetMediaStartTime();
    }
    
    /**
     * This is the time the timebar ends at. This will include the padding, and 
     * scheduled recording time where a portion of the recording may have been missed.
     * 
     * @return Returns A time in ticks of when the media file ends.
     * @throws SageCallApiException 
     */
    public long GetEndTime() throws SageCallApiException
    {
        return this.mediaFile.GetMediaEndTime();
    }
    
    public long GetDuration() throws SageCallApiException 
    {
        return this.GetEndTime() - this.GetStartTime();
    }
    
    
    
    /***
     * Returns current watch time if the file is not currently loaded, otherwise
     * the current media player time in relation to the timebar start time.
     * 
     * For example when playback is at the beging of the file it will return 0.
     * When playback is at the end of the file it will return a value = to the
     * duration.
     * 
     * @return
     * @throws SageCallApiException 
     */
    public long GetPlaybackTime() throws SageCallApiException
    {
        
        if(MediaPlayer.IsMediaPlayerLoaded(context))
        {
            //The documentation says that playbacktime is realative to airing start time.
            //return MediaPlayer.GetMediaTime(this.context) - this.mediaFile.GetAiring().GetAiringStartTime();
            return MediaPlayer.GetMediaTime(this.context) - this.GetStartTime();
        }
        else
        {
            //The watchedduration is in relation to the start time of the airing.  This converts to the timebar start time.
            long airingCurrnetWathcedTime = mediaFile.GetAiring().GetWatchedDuration() + mediaFile.GetAiring().GetAiringStartTime();
            return airingCurrnetWathcedTime - this.GetStartTime();
        }
        
    }
    
    public double GetPlaybackPercent() throws SageCallApiException
    {
        double temp;
        
        //TODO: For some reason when an import moves this gets all messed up.
        
        
        
        /*
         * When a recording moves the import time changes, so that probable changes
         * the playback time
        */
        
        if(mediaFile.GetAiring().IsWatched())
        {
            return 100.0;
        }
        
        
        if(mediaFile.IsFileCurrentlyRecording())
        {            
            //temp = ((this.GetPlaybackTime() * 1.0) / (this.GetDuration() * 1.0) * 100.0) - this.GetPlaybackStartPercent();
            temp = ((this.GetPlaybackTime() * 1.0) / (this.GetDuration() * 1.0) * 100.0);
        }
        else
        {
            temp = ((this.GetPlaybackTime() * 1.0) / (this.GetDuration() * 1.0) * 100.0);
        }
        
        return temp;
    }
    
    public double GetPlaybackStartPercent() throws SageCallApiException
    {
        return this.mediaFile.GetMediaFileSegments()[0].GetStartPercent();
    }
    
    public long GetPreviousMarker() throws SageCallApiException
    {
        //Take some time off of the GetMediaTime to allow for multiple skip backs
        if(this.HasCommercialMarkers())
        {
            for(int i = commercials.length - 1; i >= 0; i--)
            {
                if(commercials[i].GetEndTime() < (MediaPlayer.GetMediaTime(this.context) - 3000))
                {
                    return commercials[i].GetEndTime();
                }
                else if(commercials[i].GetStartTime() < (MediaPlayer.GetMediaTime(this.context) - 3000))
                {
                    return commercials[i].GetStartTime();
                }
            }
        }
        return -1;
    }
    
    public long GetNextCommercialMarker() throws SageCallApiException
    {
        if(this.HasCommercialMarkers())
        {
            for(int i = 0; i < commercials.length; i++)
            {
                if(commercials[i].GetStartTime() > MediaPlayer.GetMediaTime(this.context))
                {
                    return commercials[i].GetStartTime();
                }
                else if(commercials[i].GetEndTime() > MediaPlayer.GetMediaTime(this.context))
                {
                    return commercials[i].GetEndTime();
                }
            }
        }
        
        return -1;
    }
    
    public Marker GetCurrentChapterMarker() throws SageCallApiException
    {
        if(this.HasChapterMarkers())
        {
            for(int i = 0; i < chapters.length; i++)
            {
                if(chapters[i].IsInside(MediaPlayer.GetMediaTime(this.context)))
                {
                    return chapters[i];
                }
            }
                
        }
        
        return null;
    }
    
    public long GetNextCommercialMarkerEnd() throws SageCallApiException
    {
        if(this.HasCommercialMarkers())
        {
            for(int i = 0; i < commercials.length; i++)
            {
                if(commercials[i].GetStartTime() > MediaPlayer.GetMediaTime(this.context) || commercials[i].GetEndTime() > MediaPlayer.GetMediaTime(this.context))
                {
                    return commercials[i].GetEndTime();
                }
            }
        }
        
        return -1;
    }
    
    public void SkipToNextMarker() throws SageCallApiException
    {
        if(this.HasCommercialMarkers())
        {
            this.SleepCommThread();
            long markerTime = this.GetNextCommercialMarker();

            if(markerTime > 0 )
            {
                MediaPlayer.Seek(this.context, markerTime);
            }
        }
    }
    
    public MediaFileSegment [] GetMediaFileSegments() throws SageCallApiException
    {
        return this.mediaFile.GetMediaFileSegments();
    }
    
    public Marker [] GetCommercialMarkers()
    {
        return this.commercials;
    }
    
    public boolean HasCommercialMarkers()
    {
        return (this.commercials != null && this.commercials.length > 0);
    }
    
    public boolean HasChapterMarkers()
    {
        return (this.chapters != null && this.chapters.length > 0);
    }
    
    public void SkipToPreviousMarker() throws SageCallApiException
    {
        if(this.HasCommercialMarkers())
        {
            this.SleepCommThread();
            long markerTime = this.GetPreviousMarker();

            if(markerTime > 0 )
            {
                MediaPlayer.Seek(this.context, markerTime);
            }
        }
    }
    
    public void SkipToNextMarkerEnd() throws SageCallApiException
    {
        if(this.HasCommercialMarkers())
        {
            this.SleepCommThread();
            long markerTime = this.GetNextCommercialMarkerEnd();

            if(markerTime > 0 )
            {
                MediaPlayer.Seek(this.context, markerTime);
            }
        }
    }

    private void SleepCommThread()
    {
        this.sleepCommThread = this.sleepOnSkip;
    }
    
    public void StartCommSkipThread()
    {
        if(!comThreadRun && this.HasCommercialMarkers())
        {
            comThreadRun = true;
            this.start();
        }
    }
    
    public void StopCommSkipThread()
    {
        comThreadRun = false;
    }
    
    public void SetSleepOnSkipTime(long time)
    {
        this.sleepOnSkip = time;
    }
    
    public long GetSleepOnSkipTime()
    {
        return this.sleepOnSkip;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Subtitle Calls">
    
    public ArrayList<MediaFileSubtitleTrack> GetSubtitleTracks() throws SageCallApiException
    {   
        return MediaPlayer.GetSubtitleTracks(context);
    }
    
    public void SetSubtitleTrack(MediaFileSubtitleTrack subtitle) throws SageCallApiException
    {
        MediaPlayer.SetSubtitleTrack(context, subtitle);
    }
    
    public MediaFileSubtitleTrack GetCurrentSubtitleTrack() throws SageCallApiException
    {
        return MediaPlayer.GetCurrentSubtitleTrack(context);
    }
    
    public int GetCurrentSubtitleTrackIndex() throws SageCallApiException
    {
        MediaFileSubtitleTrack current = MediaPlayer.GetCurrentSubtitleTrack(context);
        ArrayList<MediaFileSubtitleTrack> subtitles = this.GetSubtitleTracks();
        
        for(int i = 0; i < subtitles.size(); i++)
        {
            if(subtitles.get(i).equals(current))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Audio Track Calls">
    
    public ArrayList<MediaFileAudioTrack> GetAudioTracks() throws SageCallApiException
    {   
        return MediaPlayer.GetAudioTracks(context);
    }
    
    public void SetAudioTrack(MediaFileAudioTrack audio) throws SageCallApiException
    {
        MediaPlayer.SetAudioTrack(context, audio);
    }
    
    public MediaFileAudioTrack GetCurrentAudioTrack() throws SageCallApiException
    {
        return MediaPlayer.GetCurrentAudioTrack(context);
    }
    
    public int GetCurrentAudioTrackIndex() throws SageCallApiException
    {
        MediaFileAudioTrack current = MediaPlayer.GetCurrentAudioTrack(context);
        ArrayList<MediaFileAudioTrack> audio = this.GetAudioTracks();
        
        for(int i = 0; i < audio.size(); i++)
        {
            if(audio.get(i).equals(current))
            {
                return i;
            }
        }
        
        return -1;
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Timebar Thread">
    
    @Override
    public void run() 
    {
        System.out.println("jvl.sage.Timebar - Commercial Skipping thread started");
        
        while(comThreadRun)
        {
            try 
            {
                if(this.sleepCommThread <= 0)
                {
                    for(int i = 0; i < commercials.length; i++)
                    {
                        if(commercials[i].IsHit(MediaPlayer.GetMediaTime(this.context), this.commHitRange))
                        {
                            //System.out.println("jvl.sage.Timebar - Commercial Hit...  Skipping to end of marker");
                            this.SleepCommThread();
                            MediaPlayer.Seek(this.context, commercials[i].GetEndTime());

                        }
                    }
                }
                else
                {
                    this.sleepCommThread = this.sleepCommThread - 1000;
                }
                
                Thread.sleep(1000);
            } 
            catch (SageCallApiException ex){ }
            catch(InterruptedException ex2){ }
        }
        
        System.out.println("jvl.sage.Timebar - Commercial Skipping thread stopped");
    }
    //</editor-fold>
}

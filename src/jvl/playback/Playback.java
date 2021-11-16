package jvl.playback;

import java.util.logging.Logger;
import jvl.logging.Logging;
import jvl.sage.Debug;
import jvl.sage.SageCallApiException;
import jvl.sage.api.Airing;
import jvl.sage.api.Airings;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.MediaPlayer;
import jvl.sage.api.UIContext;
import jvl.sage.api.Widget;

/**
 * This is used to describe playback options. What to do when playback finishes.
 * Play another media file, go back to another screen.  Show a show as potential
 * next playback
 * 
 * @author jvl711
 */
public class Playback
{
    private static final int DEFAULT_PLAYNEXT_TIME_SECONDS = 15;
    private static final int DEFAULT_SKIP_TIME_SECONDS = 10;
    private static final int DEFAULT_SKIP2_TIME_SECONDS = 30;
    private static final int DEFAULT_SKIP3_TIME_SECONDS = 60;
    private int playnextTime;
    private Airings airings;
    private int index;
    private PlaybackOptions playbackOptions;
    private UIContext uicontext;
    private boolean cancelPlayNext;
    private PlayNextThread playNextThread;
    private Widget returnMenu;
    private Timebar timebar;
   
    
    
    private int currentPlayNextTime;
    
    private static final Logger LOG = Logging.getLogger(Playback.class.getName());
    
    /**
     * Attempts to determine the object type of the media passed in.  The index
     * of the created instance will be defaulted to 0

     * @see jvl.sage.api.MediaFile
     * @see jvl.sage.api.MediaFiles
     * @see jvl.sage.api.Airing
     * @see jvl.sage.api.Airings
     * 
     * @param context The UI that is being controlled
     * @param media Accepts jvl.sage.api.MediaFile, jvl.sage.api.MediaFiles, 
     *      jvl.sage.api.Airing, jvl.sage.api.Airings, or the built in MediaFile or Airing
     */
    public Playback(String context, Object media) throws SageCallApiException
    {
        this(context, media, PlaybackOptions.SINGLE, 0);
    }
    
    /**
     * Attempts to determine the object type of the media passed in.  If it is
     * a list based object than it.
     * 
     * If the object passed is not a list based object than the index will be set
     * to 0 regardless of what was passed in.
     * 
     * @see jvl.sage.api.MediaFile
     * @see jvl.sage.api.MediaFiles
     * @see jvl.sage.api.Airing
     * @see jvl.sage.api.Airings
     * 
     * @param context The UI that is being controlled
     * @param media Accepts jvl.sage.api.MediaFile, jvl.sage.api.MediaFiles, 
     *      jvl.sage.api.Airing, jvl.sage.api.Airings, or the built in MediaFile or Airing
     * @param playbackOptions How to handle playback of multiple files
     * @param index The index in the file to start playback at
     * 
     * @throws SageCallApiException
     */
    public Playback(String context, Object media, PlaybackOptions playbackOptions,  int index) throws SageCallApiException
    {
        LOG.severe("Playback constructor called");
        
        this.index = index;
        this.uicontext = new UIContext(context);
        this.playbackOptions = playbackOptions;
        this.playnextTime = Playback.DEFAULT_PLAYNEXT_TIME_SECONDS;
        
        if(media == null)
        {
            LOG.severe("Constructor passed null media type");
            throw new RuntimeException("JVL Playback - Null media type passed to constructor!");
        }
        
        if(media instanceof MediaFile)
        {
            LOG.info("Constructor creating from jvl.sage.MediaFile");
            
            airings = new Airings();
            airings.add(((MediaFile)media).GetAiring());
            
            index = 0;
        }
        else if(media instanceof Airing)
        {
            LOG.info("Constructor creating from jvl.sage.Airing");
            
            airings = new Airings();
            airings.add((Airing)media);
            
            index = 0;
        }
        else if(media instanceof MediaFiles)
        {
            LOG.info("Constructor creating from jvl.sage.MediaFiles");
            
            MediaFiles mediaFiles = (MediaFiles)media;
            airings = mediaFiles.GetAirings();
        }
        else if(media instanceof Airings)
        {
            LOG.info("Constructor creating from jvl.sage.Airings");
            
            airings = ((Airings)media);
        }
        else
        {
            
            if(MediaFile.IsMediaFileObject(media))
            {
                LOG.info("Constructor creating from SageTV MedisFile");
                
                airings = new Airings();
                MediaFile mediaFile = new MediaFile(media);
                airings.add(mediaFile.GetAiring());
                index = 0;
            }
            else if(Airing.IsAiringObject(media))
            {
                LOG.info("Constructor creating from SageTV Airing");
                
                airings = new Airings();
                airings.add(new Airing(media));
                index = 0;
            }
            else
            {
                String className = "";
                
                if(media != null)
                {
                    className = media.getClass().getSimpleName();
                }
                else
                {
                    className = "NULL";
                }
                
                LOG.severe("Constructor passed unknown media type");
                throw new RuntimeException("JVL Playback - Unknown media type passed to constructor! [" + className + "]");
            }
        }
        
        /*
         * Preload with all unwatched
        */
        if(PlaybackOptions.MULTIPLE_UNWATCHED == playbackOptions)
        {
            Debug.Writeln("Constructor filtering airings for unwatched playback option", Debug.INFO);
            airings = airings.GetUnwatchedAirings();
            index = 0;
        }
        /*
         * Preload with random order of airings
        */
        if(PlaybackOptions.MULTIPLE_RANDOM == playbackOptions)
        {
            Debug.Writeln("Constructor creating airings for random playback option", Debug.INFO);
            Airings randomAirings = new Airings();
            
            for(int i = 0; i < airings.size(); i++)
            {
                randomAirings.add(airings.GetRandomAiring());
            }
            
            airings = randomAirings;
            index = 0;
        }
    }

    /**
     * Add menu that you would like to return too when playback completes or
     * stops
     * @param widget Menu to return too
     * @throws SageCallApiException 
     */
    public void AddReturnMenu(Widget widget) throws SageCallApiException
    {
        Debug.Writeln("AddReturnMenu called with " + widget, Debug.INFO);
        
        if(widget.GetType().equalsIgnoreCase("menu"))
        {
            Debug.Writeln("The widget passed was not of type Menu.  (Not adding it)" + widget, Debug.WARNING);
            this.returnMenu = widget;
        }
    }
    
    public Timebar GetTimebar() throws SageCallApiException
    {
        if(this.timebar == null)
        {
            this.timebar = this.CreateTimebarInstance();
        }
        
        return this.timebar;
    }
    
    public PlaybackOptions GetPlaybackOption()
    {
        return this.playbackOptions;
    }
    
    /**
     * Returns the current MediaFile that the playback is pointing to.
     * 
     * @return MediaFile object
     * @throws SageCallApiException 
     */
    public MediaFile GetCurrentMediaFile() throws SageCallApiException
    {
        return this.GetCurrentAiring().GetMediaFile();   
    }
    
    /**
     * If live tv than it will update the airing to the current media file
     * loaded by the MediaPlayer
     */
    public void UpdateLiveAiring() throws SageCallApiException
    {
        
        if(this.playbackOptions == PlaybackOptions.LIVE_TV && MediaPlayer.HasMediaFile(uicontext))
        {
            Airing airing = new Airing(MediaPlayer.GetCurrentMediaFile(uicontext));
            
            airings = new Airings();
            airings.add(airing);
            
            Debug.Writeln("Current Airing has been updated", Debug.WARNING);
        }
        else
        {
            Debug.Writeln("UpdateLiveAiring called without being PlaybackOptions.LIVE_TV", Debug.WARNING);
        }
    }
    
     /**
     * Returns the current Airing that the playback is pointing to.
     * 
     * @return Airing object
     * @throws SageCallApiException 
     */
    public Airing GetCurrentAiring() throws SageCallApiException
    {
        return this.airings.get(index);   
    }
    
    public MediaFile PeekNextMediaFile() throws SageCallApiException
    {
        if(this.HasMoreMediaFiles())
        {
            return this.airings.get(index + 1).GetMediaFile();
        }
        else
        {
            return null;
        }
    }
    
    public Airing PeekNextAiring()
    {
        if(this.HasMoreMediaFiles())
        {
            return this.airings.get(index + 1);
        }
        else
        {
            return null;
        }
    }
        
    /**
     * Returns the number of files available for playback based on the size
     * of the media collection and the playbackoptions.  Live_TV and Single
     * will return 1.
     * 
     * @return Size of media collection
     */
    public int GetSize()
    {
        if(this.playbackOptions == PlaybackOptions.LIVE_TV || this.playbackOptions == PlaybackOptions.SINGLE)
        {
            return 1;
        }
        else
        {
            return this.airings.size();
        }
             
    }
    
    /**
     * Returns the current index
     * @return Index
     */
    public int GetIndex()
    {
        return this.index;
    }
    
    /**
     * Creates a timebar instance for the current mediafile
     * @return Timebar instance
     * @throws SageCallApiException 
     */
    public Timebar CreateTimebarInstance() throws SageCallApiException
    {
        this.timebar = new Timebar(uicontext.GetName(), this.GetCurrentAiring());
        
        return timebar;
    }
    
    /**
     * Increments the index to the next position and return the next MediaFile
     * 
     * Live TV returns current media file
     * 
     * @return Returns MediaFile object
     * @throws SageCallApiException 
     */
    public MediaFile NextMediaFile() throws SageCallApiException
    {
        if(PlaybackOptions.LIVE_TV == this.playbackOptions)
        {
            return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));   
        }
        else
        {
            if(index >= (this.airings.size() - 1))
            {
                throw new IndexOutOfBoundsException();
            }
            
            index++;
            return this.airings.get(index).GetMediaFile();
        }
    }
    
    public MediaFile PreviousMediaFile() throws SageCallApiException
    {
        if(PlaybackOptions.LIVE_TV == this.playbackOptions)
        {
            return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));   
        }
        else
        {
            if(index == 0)
            {
                throw new IndexOutOfBoundsException();
            }
            
            index--;
            return this.airings.get(index).GetMediaFile();
        }
    }
    
    public boolean HasMoreMediaFiles()
    {
        if(PlaybackOptions.LIVE_TV == this.playbackOptions)
        {
            return false;
        }
        else
        {   
            if(index < (this.airings.size() - 1))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    /**
     * Gets the amount of time it waits before playing the next airing in the 
     * list
     * 
     * @return Time in seconds
     */
    public int GetPlayNextTime()
    {
        return this.playnextTime;
    }
    
    /**
     * Sets the amount of time to wait before playing the next file in the list
     * 
     * @param seconds Time in seconds.  Values less than 0 are set to zero.
     */
    public void SetPlayNextTime(int seconds)
    {
        if(seconds < 0)
        {
            this.playnextTime = 0;
        }
        else
        {
            this.playnextTime = seconds;
        }
    }
    
    public void FastForward() throws SageCallApiException
    {
        
        long skipTime = MediaPlayer.GetMediaTime(uicontext) + (Playback.DEFAULT_SKIP_TIME_SECONDS * 1000);
        Debug.Writeln("FastForward Called - Seek to: " + skipTime, Debug.INFO);
        MediaPlayer.Seek(uicontext, skipTime);
    }
    
    public void Rewind() throws SageCallApiException
    {
        long skipTime = MediaPlayer.GetMediaTime(uicontext) - (Playback.DEFAULT_SKIP_TIME_SECONDS * 1000);
        Debug.Writeln("Rewind Called - Seek to: " + skipTime, Debug.INFO);
        MediaPlayer.Seek(uicontext, skipTime);
    }
    
    public void SkipPreviousMarker() throws SageCallApiException
    {
        if(this.GetTimebar().HasChapterMarkers())
        {
            Debug.Writeln("Skip Backward Called - Has Chapter Markers, skipping to previous marker.", Debug.INFO);
            this.GetTimebar().SkipToPreviousMarker();
        }
        else if(this.GetTimebar().HasCommercialMarkers())
        {
            Debug.Writeln("Skip Backward Called - Has Commercial Markers, skipping to previous marker.", Debug.INFO);
            this.GetTimebar().SkipToPreviousMarker();
        }
    }
    
    public void SkipNextMarker() throws SageCallApiException
    {
        if(this.GetTimebar().HasChapterMarkers())
        {
            Debug.Writeln("Skip Forward Called - Has Chapter Markers, skipping to next marker.", Debug.INFO);
            this.GetTimebar().SkipToNextMarker();
        }
        else if(this.GetTimebar().HasCommercialMarkers())
        {
            Debug.Writeln("Skip Forward Called - Has Commercial Markers, skipping to next marker.", Debug.INFO);
            this.GetTimebar().SkipToNextMarker();
        }
    }
    
    public void SkipForward() throws SageCallApiException
    {
        long skipTime = MediaPlayer.GetMediaTime(uicontext) + (Playback.DEFAULT_SKIP2_TIME_SECONDS * 1000);
        Debug.Writeln("Skip Forward Called - Seek to: " + skipTime, Debug.INFO);
        MediaPlayer.Seek(uicontext, skipTime);
        
    }
    
    public void SkipBackward() throws SageCallApiException
    {
        long skipTime = MediaPlayer.GetMediaTime(uicontext) - (Playback.DEFAULT_SKIP2_TIME_SECONDS * 1000);
        Debug.Writeln("Skip Backward Called - Seek to: " + skipTime, Debug.INFO);
        MediaPlayer.Seek(uicontext, skipTime);   
    }
    
    public void SkipForward2() throws SageCallApiException
    {
        if(this.playbackOptions == PlaybackOptions.MULTIPLE
                || this.playbackOptions == PlaybackOptions.MULTIPLE_RANDOM
                || this.playbackOptions == PlaybackOptions.MULTIPLE_RANDOM)
        {
            Debug.Writeln("Skip Forward 2 Called - MULTIPLE Playack, playing next file", Debug.INFO);
            this.PlayNextFile();
        }
        else
        {
            long skipTime = MediaPlayer.GetMediaTime(uicontext) + (Playback.DEFAULT_SKIP3_TIME_SECONDS * 1000);
            Debug.Writeln("Skip Forward 2 Called - Seek to: " + skipTime, Debug.INFO);
            MediaPlayer.Seek(uicontext, skipTime);
        }
    }
    
    public void SkipBackward2() throws SageCallApiException
    {
        if(this.playbackOptions == PlaybackOptions.MULTIPLE
                || this.playbackOptions == PlaybackOptions.MULTIPLE_RANDOM
                || this.playbackOptions == PlaybackOptions.MULTIPLE_RANDOM)
        {
            Debug.Writeln("Skip BAckward 2 Called - MULTIPLE Playack, playing previous file", Debug.INFO);
            this.PlayPreviousFile();
        }
        else
        {
            long skipTime = MediaPlayer.GetMediaTime(uicontext) - (Playback.DEFAULT_SKIP3_TIME_SECONDS * 1000);
            Debug.Writeln("Skip Backward 2 Called - Seek to: " + skipTime, Debug.INFO);
            MediaPlayer.Seek(uicontext, skipTime);
        }
    }
    
    
    public void Stop() throws SageCallApiException
    {
        Debug.Writeln("Stop playback called", Debug.INFO);
        
        MediaPlayer.Stop(uicontext);
        
        if(this.returnMenu != null)
        {
            Debug.Writeln("Return menu was registered. Lanuching Menu: " + this.returnMenu, Debug.INFO);
            this.returnMenu.LaunchMenu();
        }
        else
        {
            Debug.Writeln("No return menu was specified", Debug.INFO);
        }
    }
    
    public void Pause() throws SageCallApiException
    {
        Debug.Writeln("Pause playback called", Debug.INFO);
        MediaPlayer.Pause(uicontext);
    }
    
    public void Play() throws SageCallApiException
    {
        Debug.Writeln("Play called", Debug.INFO);
        
        if(MediaPlayer.IsMediaPlayerLoaded(uicontext))
        {
            Debug.Writeln("Media player is loaded. Calling MediaPlayer.Play", Debug.INFO);
            
            if(this.GetCurrentAiring().IsWatched())
            {
                this.GetCurrentAiring().SetWatchedStatus(false);
            }
            
            MediaPlayer.Play(uicontext);
        }
        else
        {
            Debug.Writeln("Media player is not loaded. Calling MediaPlayer.Watch", Debug.INFO);
            
            this.PlayCurrentFile();
        }
        
        
    }
    
    /**
     * Plays the file at the current index.  If it is a Multi playback option it
     * will clear the watched status.
     * 
     * @throws SageCallApiException 
     */
    public void PlayCurrentFile() throws SageCallApiException
    {
        /*
        if(this.GetPlaybackOption() == PlaybackOptions.MULTIPLE_RANDOM
            || this.GetPlaybackOption() == PlaybackOptions.MULTIPLE
            || this.GetPlaybackOption() == PlaybackOptions.MULTIPLE_UNWATCHED)
        {
            this.GetCurrentAiring().SetWatchedStatus(false);
        }
        */
        
        if(this.GetCurrentAiring().IsWatched())
        {
            this.GetCurrentAiring().SetWatchedStatus(false);
        }
        
        MediaPlayer.Watch(uicontext, this.GetCurrentAiring());
    }
    
    /**
     * Attempts to play the next file now.
     * @throws SageCallApiException 
     */
    public void PlayNextFile() throws SageCallApiException
    {
        //In case the play next thread is running
        this.CancelPlayNextThread();
        
        if(this.GetCurrentAiring().IsWatched())
        {
            this.GetCurrentAiring().SetWatchedStatus(false);
        }
        
        switch (this.playbackOptions) 
        {
        
            case LIVE_TV:
                
                Debug.Writeln("PlaybackOption.LIVE_TV, calling Watch on current airing.", Debug.INFO);
                
                //Call GetCurrent????
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
        
            case SINGLE:
                
                Debug.Writeln("PlaybackOption.SINGLE, calling watch on " + this.airings.get(index).GetShow().GetTitle(), Debug.INFO);
                
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
                    
            default:
                
                Debug.Writeln("PlaybackOption.MULTIPLE_*", Debug.INFO);
                
                if(this.HasMoreMediaFiles())
                {
                    MediaFile mediaFile = this.NextMediaFile();
                    mediaFile.GetAiring().SetWatchedStatus(false);
                    
                    Debug.Writeln("calling watch on " + this.airings.get(index).GetShow().GetTitle(), Debug.WARNING);
                    MediaPlayer.Watch(uicontext, mediaFile.GetAiring());
                }   
                else
                {
                    Debug.Writeln("There are no more media files...  Not playing anything.", Debug.WARNING);
                }
                
                break;
        }        
    }
    
    public void PlayPreviousFile() throws SageCallApiException
    {
        //In case the play next thread is running
        this.CancelPlayNextThread();
        
        if(this.GetCurrentAiring().IsWatched())
        {
            this.GetCurrentAiring().SetWatchedStatus(false);
        }
        
        switch (this.playbackOptions) 
        {
        
            case LIVE_TV:
                
                Debug.Writeln("PlaybackOption.LIVE_TV, calling Watch on current airing.", Debug.INFO);
                
                //Call GetCurrent????
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
        
            case SINGLE:
                
                Debug.Writeln("PlaybackOption.SINGLE, calling watch on " + this.airings.get(index).GetShow().GetTitle(), Debug.INFO);
                
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
                    
            default:
                
                Debug.Writeln("PlaybackOption.MULTIPLE_*", Debug.INFO);
                
                if(this.index > 0)
                {
                    MediaFile mediaFile = this.PreviousMediaFile();
                    mediaFile.GetAiring().SetWatchedStatus(false);
                    
                    Debug.Writeln("calling watch on " + this.airings.get(index).GetShow().GetTitle(), Debug.WARNING);
                    MediaPlayer.Watch(uicontext, mediaFile.GetAiring());
                }   
                else
                {
                    Debug.Writeln("Currently playing the first file...  Not playing anything.", Debug.WARNING);
                }
                
                break;
        }        
    }
    
    /**
     * Gets the amount of time before the next media file plays back
     * @return Time in seconds
     */
    public int GetCurrentPlayNextTime()
    {
        return currentPlayNextTime;
    }
    
    /**
     * Executes a timer thread that counts down PlayNextTime and then
     * attemps to play next media file if it has an additional media file,
     * and is supposed to play the next file.
     */
    public void StartPlayNextThread()
    {
        if(this.playNextThread != null && this.playNextThread.isAlive())
        {
            //Thread is still running.  I think it is best to do nothing....
            Debug.Writeln("PlayNextThread is already running.  Not starting another.", Debug.WARNING);
        }
        else
        {
            Debug.Writeln("PlayNextThread is starting.", Debug.WARNING);
            this.playNextThread = new PlayNextThread(this);
            currentPlayNextTime = 0;
            cancelPlayNext = false;
            this.playNextThread.start();
        }
        
    }
    
    /**
     * Tells the timer thread to stop.  If it is still in the wait look it
     * will exit the wait loop and not play next file
     */
    public void CancelPlayNextThread()
    {
        Debug.Writeln("Stopping play next thread.", Debug.WARNING);
        currentPlayNextTime = 0;
        cancelPlayNext = true;
    }
    
    private class PlayNextThread extends Thread
    {
        private Playback playback;
        
        public PlayNextThread(Playback playback)
        {
            this.playback = playback;
        }
        
        @Override
        public void run()
        {
            //Set CurrentPlayNext time
            this.playback.currentPlayNextTime = this.playback.playnextTime;

            while(currentPlayNextTime > 0 && !cancelPlayNext)
            {
                try { Thread.sleep(1000); } catch (InterruptedException ex) { }
                this.playback.currentPlayNextTime = this.playback.currentPlayNextTime - 1;
            }

            try 
            {
                if(!cancelPlayNext)
                {
                    this.playback.PlayNextFile();
                }
            } 
            catch (SageCallApiException ex) 
            {
                Debug.Writeln("Error attempting to play next file from the thread", Debug.ERROR);
            }

            currentPlayNextTime = 0;
        }
    }
}

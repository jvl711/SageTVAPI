package jvl.playback;

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
    private int playnextTime;
    private Airings airings;
    private int index;
    private PlaybackOptions playbackOptions;
    private UIContext uicontext;
    private boolean cancelPlayNext;
    private PlayNextThread playNextThread;
    private Widget returnMenu;
    
    private int currentPlayNextTime;
    
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
        this.index = index;
        this.uicontext = new UIContext(context);
        this.playbackOptions = playbackOptions;
        this.playnextTime = Playback.DEFAULT_PLAYNEXT_TIME_SECONDS;
        
        if(media instanceof MediaFile)
        {
            System.out.println("JVL Playback - Constructor creating from jvl.sage.MediaFile");
            
            airings = new Airings();
            airings.add(((MediaFile)media).GetAiring());
            
            index = 0;
        }
        else if(media instanceof Airing)
        {
            System.out.println("JVL Playback - Constructor creating from jvl.sage.Airing");
            
            airings = new Airings();
            airings.add((Airing)media);
            
            index = 0;
        }
        else if(media instanceof MediaFiles)
        {
            System.out.println("JVL Playback - Constructor creating from jvl.sage.MediaFiles");
            
            MediaFiles mediaFiles = (MediaFiles)media;
            airings = mediaFiles.GetAirings();
        }
        else if(media instanceof Airings)
        {
            System.out.println("JVL Playback - Constructor creating from jvl.sage.Airings");
            
            airings = ((Airings)media);
        }
        else
        {
            if(MediaFile.IsMediaFileObject(media))
            {
                System.out.println("JVL Playback - Constructor creating from Sage MediaFile");
                
                airings = new Airings();
                MediaFile mediaFile = new MediaFile(media);
                airings.add(mediaFile.GetAiring());
                index = 0;
            }
            else if(Airing.IsAiringObject(media))
            {
                System.out.println("JVL Playback - Constructor creating from Sage Airing");
                
                airings = new Airings();
                airings.add(new Airing(media));
                index = 0;
            }
            else
            {
                System.out.println("JVL Playback - Constructor unknown object type passed!");
                throw new RuntimeException("JVL Playback - Unknown media type passed to constructor");
            }
        }
        
        /*
         * Preload with all unwatched
        */
        if(PlaybackOptions.MULTIPLE_UNWATCHED == playbackOptions)
        {
            airings = airings.GetUnwatchedAirings();
            index = 0;
        }
        /*
         * Preload with random order of airings
        */
        if(PlaybackOptions.MULTIPLE_RANDOM == playbackOptions)
        {
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
        System.out.println("JVL - Playback.AddRetunMenu");
        
        if(widget.GetType().equalsIgnoreCase("menu"))
        {
            System.out.println("JVL - Widget is of type menu.  Adding widget");
            this.returnMenu = widget;
        }
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
        return new Timebar(uicontext.GetName(), this.GetCurrentAiring());
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
    
    public void Stop() throws SageCallApiException
    {
        System.out.println("JVL - Playback.Stop called");
        
        MediaPlayer.Stop(uicontext);
        
        
        if(this.returnMenu != null)
        {
            System.out.println("JVL - Return menu registered.  Launching menu");
            this.returnMenu.LaunchMenu();
        }
        else
        {
            System.out.println("JVL - No retrun menu registered");
        }
    }
    
    public void Pause() throws SageCallApiException
    {
        MediaPlayer.Pause(uicontext);
    }
    
    public void Play() throws SageCallApiException
    {
        MediaPlayer.Play(uicontext);
    }
    
    /**
     * Plays the file at the current index.  If it is a Multi playback option it
     * will clear the watched status.
     * 
     * @throws SageCallApiException 
     */
    public void PlayCurrentFile() throws SageCallApiException
    {
        if(this.GetPlaybackOption() == PlaybackOptions.MULTIPLE_RANDOM
            || this.GetPlaybackOption() == PlaybackOptions.MULTIPLE
            || this.GetPlaybackOption() == PlaybackOptions.MULTIPLE_UNWATCHED)
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
        
        switch (this.playbackOptions) 
        {
        
            case LIVE_TV:
                
                System.out.println("JVL Playback - PlayNextFile LIVE_TV: " + this.airings.get(index).GetShow().GetTitle());
                
                //Call GetCurrent????
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
        
            case SINGLE:
                
                System.out.println("JVL Playback - PlayNextFile SINGLE: " + this.airings.get(index).GetShow().GetTitle());
                
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
                    
            default:
                
                System.out.println("JVL Playback - PlayNextFile OTHER");
                
                if(this.HasMoreMediaFiles())
                {
                    MediaFile mediaFile = this.NextMediaFile();
                    mediaFile.GetAiring().SetWatchedStatus(false);
                    
                    MediaPlayer.Watch(uicontext, mediaFile.GetAiring());
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
        }
        else
        {
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
                System.out.println("JVL Playback - Error attempting to playnext: " +  ex.getMessage());
            }

            currentPlayNextTime = 0;
        }
    }
}
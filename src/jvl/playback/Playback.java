package jvl.playback;

import jvl.sage.SageCallApiException;
import jvl.sage.api.Airing;
import jvl.sage.api.Airings;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.MediaPlayer;
import jvl.sage.api.UIContext;

/**
 * This is used to describe playback options. What to do when playback finishes.
 * Play another media file, go back to another screen.  Show a show as potential
 * next playback
 * 
 * @author jvl711
 */
public class Playback extends Thread
{
    private static final int DEFAULT_PLAYNEXT_TIME_SECONDS = 15;
    private int playnextTime;
    private Airings airings;
    private int index;
    private PlaybackOptions playbackOptions;
    private UIContext uicontext;
    private boolean cancelPlayNext;
    
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
        
        if(PlaybackOptions.MULTIPLE_UNWATCHED == playbackOptions)
        {
            airings = airings.GetUnwatchedAirings();
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
        //if(PlaybackOptions.LIVE_TV == this.playbackOptions)
        //{
            //return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));
            
        //}
        //else
        //{
        System.out.println("JVL Playback - GetCurrnetMediaFile()");
        
        return this.airings.get(index).GetMediaFile();
        //}
    }
    
     /**
     * Returns the current Airing that the playback is pointing to.
     * 
     * @return Airing object
     * @throws SageCallApiException 
     */
    public Airing GetCurrentAiring() throws SageCallApiException
    {
        System.out.println("JVL Playback - GetCurrnetAiring()");
        
        return this.airings.get(index);
        
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
            //TODO: Enhance this to get the next airing in the EPG
            //if(MediaPlayer.HasMediaFile(uicontext) )
            //{
                return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));
            //}
            //else
            //{
                //return this.airings.get(index).GetMediaFile();
            //}
        }
        else if(PlaybackOptions.MULTIPLE_RANDOM == this.playbackOptions)
        {
            if(index >= (this.airings.size() - 1))
            {
                throw new IndexOutOfBoundsException();
            }
            
            index++;
            return this.airings.GetRandomAiring().GetMediaFile();
        }
        else
        {
            if(index >= (this.airings.size() - 1))
            {
                throw new IndexOutOfBoundsException();
            }
            
            return this.airings.get(index++).GetMediaFile();
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
                
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
        
            case SINGLE:
                
                MediaPlayer.Watch(uicontext, this.airings.get(index));
                break;
                
            default:
                
                if(this.HasMoreMediaFiles())
                {
                    MediaFile mediaFile = this.NextMediaFile();
                    MediaPlayer.Watch(uicontext, mediaFile);
                }   break;
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
        cancelPlayNext = false;
        this.start();
    }
    
    /**
     * Tells the timer thread to stop.  If it is still in the wait look it
     * will exit the wait loop and not play next file
     */
    public void CancelPlayNextThread()
    {
        cancelPlayNext = true;
    }
    
    @Override
    public void run()
    {
        //Set CurrentPlayNext time
        this.currentPlayNextTime = this.playnextTime;
        
        while(currentPlayNextTime > 0 && !cancelPlayNext)
        {
            try { Thread.sleep(1000); } catch (InterruptedException ex) { }
            this.currentPlayNextTime = this.currentPlayNextTime - 1000;
        }
        
        try 
        {
            if(!cancelPlayNext)
            {
                this.PlayNextFile();
            }
        } 
        catch (SageCallApiException ex) 
        {
            System.out.println("JVL Playback - Error attempting to playnext: " +  ex.getMessage());
        }
    }
}

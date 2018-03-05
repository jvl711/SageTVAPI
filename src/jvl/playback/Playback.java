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
public class Playback 
{
    private static final int DEFAULT_PLAYNEXT_TIME_SECONDS = 15;
    private int playnextTime;
    private Airings airings;
    private int index;
    private PlaybackOptions playbackOptions;
    private UIContext uicontext;
    
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
            airings = new Airings();
            airings.add(((MediaFile)media).GetAiring());
            
            index = 0;
        }
        else if(media instanceof Airing)
        {
            airings = new Airings();
            airings.add((Airing)media);
            
            index = 0;
        }
        else if(media instanceof MediaFiles)
        {
            MediaFiles mediaFiles = (MediaFiles)media;
            airings = mediaFiles.GetAirings();
        }
        else if(media instanceof Airings)
        {
            airings = ((Airings)media);
        }
        else
        {
            if(MediaFile.IsMediaFileObject(media))
            {
                airings = new Airings();
                MediaFile mediaFile = new MediaFile(media);
                airings.add(mediaFile.GetAiring());
                index = 0;
            }
            else if(Airing.IsAiringObject(media))
            {
                airings = new Airings();
                airings.add(new Airing(media));
                index = 0;
            }
        }
        
        if(PlaybackOptions.MULTIPLE_UNWATCHED == playbackOptions)
        {
            airings = airings.GetUnwatchedAirings();
        }
        
    }
    
    /**
     * Returns the current MediaFile that the playback is pointing to, or 
     * if playing live tv it pulls back the current media file from the
     * MediaPlayer, and does not increment the index
     * 
     * @return MediaFile object
     * @throws SageCallApiException 
     */
    public MediaFile GetCurrnetMediaFile() throws SageCallApiException
    {
        if(PlaybackOptions.LIVE_TV == this.playbackOptions)
        {
            return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));
        }
        else
        {
            return this.airings.get(index).GetMediaFile();
        }
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
            return new MediaFile(MediaPlayer.GetCurrentMediaFile(uicontext));
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
}

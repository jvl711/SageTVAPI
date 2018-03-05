package jvl.sage;

import jvl.playback.Playback;
import jvl.sage.api.*;

/**
 * Holds a list of airings and manages iterating through the list
 * Keeps track of the current position
 * 
 * Keeps track of auto playback properties
 * 
 */
public class Playlist 
{
    private static final int DEFAULT_PLAYNEXT_WAIT_TIME_SECONDS = 15;
    
    private Airings airings;
    private int currentIndex;
    
    private boolean autoPlayNext;
    private int autoPlayWaitTime = DEFAULT_PLAYNEXT_WAIT_TIME_SECONDS;

    public Playlist(Shows shows) throws SageCallApiException
    {   
        airings = new Airings();
        
        Integer [] seasons = shows.GetSeasons();
        
        for(int i = 0; i < seasons.length; i++)
        {
            Shows temp = shows.GetShows(seasons[i]);
            
            for(int j = 0; j < temp.size(); j++)
            {
                airings.add(temp.get(j).GetAiring());
            }
        }
        
        this.autoPlayNext = false;
        this.currentIndex = 0;
    }
    
    public Playlist(Airings airings) throws SageCallApiException
    {
        this.airings = new Airings();
        Shows shows = airings.GetShows();
        
        Integer [] seasons = shows.GetSeasons();
        
        for(int i = 0; i < seasons.length; i++)
        {
            Shows temp = shows.GetShows(seasons[i]);
            
            for(int j = 0; j < temp.size(); j++)
            {
                this.airings.add(temp.get(j).GetAiring());
            }
        }
        
        this.autoPlayNext = false;
        this.currentIndex = 0;
    }
            
    /**
     * Returns true if the next call to GetNextAiring would succeed
     * @return 
     */
    public boolean HasMoreAirings()
    {
        return (currentIndex) < airings.size();
    }
    
    /**
     * Increments the index on the play list, and returns the airing at the
     * new index;
     * @return 
     */
    public Object GetNextAiringUnwrapped()
    {        
        return GetNextAiring().UnwrapObject();
    }
    
    public Airing GetNextAiring()
    {
        //Check to see if incrementing would go beyond
        if(!this.HasMoreAirings())
        {
            throw new RuntimeException("Index is outside of the bounds");
        }
        
        currentIndex++;
        Airing airing = airings.get(currentIndex);
        
        
        return airing;
    }
    
    public Airing PeekNextAiring()
    {
        //Check to see if incrementing would go beyond
        if(!this.HasMoreAirings())
        {
            throw new RuntimeException("Index is outside of the bounds");
        }
        
        Airing airing = airings.get(currentIndex + 1);
        //currentIndex++;
        
        return airing;
    }
    
    public Object GetCurrenttAiringUnwrapped()
    {
        return airings.get(currentIndex).UnwrapObject();
    }
    
    public Object GetCurrenttAiring()
    {
        return airings.get(currentIndex);
    }
    
    public void ResetIndex()
    {
        currentIndex = 0;
    }

    /**
     * Returns if auto playback is on or off
     * 
     * @return True if auto playback is on
     */
    public boolean IsAutoPlayNext() 
    {
        return autoPlayNext;
    }

    /**
     * Set if auto playback of the next item in the playlist is on
     * or off for the playlist
     * 
     * @param autoPlayNext turn auto playback on or off
     */
    public void SetAutoPlayNext(boolean autoPlayNext) 
    {
        this.autoPlayNext = autoPlayNext;
    }

    /**
     * Returns the amount of time to wait before playing the next
     * video
     * 
     * @return Amount of time in seonds
     */
    public int GetAutoPlayWaitTime() 
    {
        return autoPlayWaitTime;
    }

    /**
     * Set the amount of time to wait before playing the next
     * video in the playlist
     * 
     * @param autoPlayWaitTime time in seconds
     */
    public void SetAutoPlayWaitTime(int autoPlayWaitTime) 
    {
        this.autoPlayWaitTime = autoPlayWaitTime;
    }
}

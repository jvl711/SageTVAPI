
package jvl.sage;

import jvl.sage.api.*;

/**
 * Holds a list of airings and manages iterating through the list
 * Keeps track of the current position
 * 
 * @author jolewis
 */
public class Playlist 
{
    private Airings airings;
    private int currentIndex;

    public Playlist(Shows shows)
    {
        this.airings = shows.GetAirings();
        currentIndex = 0;
    }
    
    public Playlist(Airings airings)
    {
        this.airings = airings;
        currentIndex = 0;
    }
    
    /**
     * Returns true if the next call to GetNextAiring would succeed
     * @return 
     */
    public boolean HasMoreAirings()
    {
        return (currentIndex + 1) < airings.Size();
    }
    
    /**
     * Increments the index on the play list, and returns the airing at the
     * new index;
     * @return 
     */
    public Object GetNextAiringUnwrapped()
    {
        //Check to see if incrementing would go beyond
        if(!this.HasMoreAirings())
        {
            throw new RuntimeException("Index is outside of the bounds");
        }
        
        return airings.Get(++currentIndex).UnwrapObject();
    }
    
    public Airing GetNextAiring()
    {
        //Check to see if incrementing would go beyond
        if(!this.HasMoreAirings())
        {
            throw new RuntimeException("Index is outside of the bounds");
        }
        
        return airings.Get(++currentIndex);
    }
    

    public Object GetCurrenttAiringUnwrapped()
    {
        return airings.Get(currentIndex).UnwrapObject();
    }
    
    public Object GetCurrenttAiring()
    {
        return airings.Get(currentIndex);
    }
    
    public void ResetIndex()
    {
        currentIndex = 0;
    }
}

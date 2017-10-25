
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
        
        currentIndex = 0;
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
        
        currentIndex = 0;
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
        
        Airing airing = airings.get(currentIndex);
        currentIndex++;
        
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
}

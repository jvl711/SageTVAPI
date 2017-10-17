package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import jvl.sage.Debug;
import jvl.sage.SageCallApiException;
import java.util.Random;



public class Airings extends SageArrayObject<Airing>
{
    ArrayList<Airing> airings;
    
    ArrayList<Airing> randomAirings;
    
    

    /*
    public Airings(String MediaMask)
    {
        try
        {
            airings = new ArrayList();
            Object [] temp = callApiArray("GetMediaFiles", MediaMask);
     
            for(int i = 0; i < temp.length; i++)
            {
                airings.add(new Airing(temp[0]));
            }
            
        }
        catch(Exception ex)
        {
            System.out.println("Error loading airings: " + ex.getMessage());
        }
    }
    */
    
    public Airings()
    {
        airings = new ArrayList();
        randomAirings = new ArrayList();
    }
    
    public Airings(Object object)
    {
        Object [] objects = null;
        
        if (object instanceof Collection)
        {
              objects = ((Collection)object).toArray();
        }
        else if(object instanceof Object[])
        {
            objects = (Object [])object;
        }
        else
        {
            Debug.Writeln("Airings Constructor: The passed object type was not expected.  Unable to initialize airings.", Debug.ERROR);
            Debug.Writeln("\tObject info: " + object.toString(), Debug.ERROR);
        }

        airings = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                airings.add(new Airing(objects[i]));
            }
        }
    }
    
    public Shows GetShows() throws SageCallApiException
    {
        Shows shows = new Shows();
       
        for(int i = 0; i < this.airings.size(); i++)
        {
            shows.Add(this.airings.get(i).GetShow());
        }
        
        return shows;
    }
    
    public MediaFiles GetMediaFiles() throws SageCallApiException
    {
        MediaFiles mediafiles = new MediaFiles();
        
        for(int i = 0; i < this.airings.size(); i++)
        {
            mediafiles.Add(this.airings.get(i).GetMediaFile());
        }
        
        return mediafiles;
    }
    
    public Airings GetUnwatchedAirings() throws SageCallApiException
    {
        Airings unwatchedAirings = new Airings();
        
        for(int i = 0; i < airings.size(); i++)
        {
            if(!airings.get(i).IsWatched())
            {
                unwatchedAirings.Add(airings.get(i));
            }
        }
        
        System.out.println("Airings.GetUnwatchedAirings: " + unwatchedAirings.Size());
        
        return unwatchedAirings;
    }

    /**
     * Set the watched status on all of the airings
     * 
     * @param watched True sets the status to watched
     *                False sets it to not watched
     */
    public void SetWatchedStatus(boolean watched) throws SageCallApiException
    {
        for(int i = 0; i < airings.size(); i++)
        {
            airings.get(i).SetWatchedStatus(watched);
        }
    }
    
    /**
     * Looks through the list of airings to determine if they are all watched
     * 
     * @return True if all airings are watched
     * @throws jvl.sage.SageCallApiException
     */
    public boolean IsAllWatched() throws SageCallApiException
    {
        for(int i = 0; i < this.airings.size(); i++)
        {
            if(!airings.get(i).IsWatched())
            {
                return false;
            }   
        }
        
        return true;
    }
    
    /**
     * Uses the random class to pick a random airing in the set of airings.
     * The class keeps track of the random airings picked, and will not pick
     * the same airing twice until the set of airings is empty.  Then it will
     * rebuild the list and start over again.
     * 
     * @return A random airing from the set
     */
    public Airing GetRandomAiring()
    {
        Random random = new Random();
     
        //Fill an array of airings.  Makes sure we do not pull the same episode
        //over and over again.
        if (randomAirings == null || randomAirings.size() == 0)
        {
            randomAirings = new ArrayList<Airing>();
            
            for(int i = 0; i < airings.size(); i++)
            {
                randomAirings.add(airings.get(i));
            }
        }
        else if (randomAirings.size() == 1)
        {
            return randomAirings.remove(0);
        }
        
        return airings.get(random.nextInt(this.airings.size()));
    }
    
    /**
     * Looks through all of the airings for the oldest unwatched airing.
     * If all airings are unwatched it returns the oldest airing. If
     * there are no qualifying airings it will return null.
     * 
     * Excludes all items that do not have a season or episode number attached
     * 
     * @return An airing or null if we were unable to find a qualifying airing
     * @throws jvl.sage.SageCallApiException
     */
    public Airing GetNextAiring() throws SageCallApiException
    {
        Integer[] seasons = this.GetShows().GetSeasons();
        Show nextShow = null;
        Show oldestShow = null;
        
        //Look for first unwatched airing
        for(int i = 0; i < seasons.length && nextShow == null; i++)
        {
            
            //Ignore 0 season
            if(seasons[i] > 0)
            {
                Shows shows = this.GetShows().GetShows(seasons[i]);
                
                for(int j = 0; j < shows.Size() && nextShow == null; j++)
                {
                    //Has an episode number, and is not watched
                    if(shows.Get(j).GetEpisodeNumber() > 0 && !shows.Get(j).GetAiring().IsWatched())
                    {
                        nextShow = shows.Get(j);   
                    }
                    
                    //The first show we come in contact with is the oldest
                    if(oldestShow == null)
                    {
                        oldestShow = shows.Get(j);
                    }
                }
            }
        }
        
        //If all of the shows are watched set to the oldest show
        if(nextShow == null)
        {
            nextShow = oldestShow;
        }
        
        //If we were unable to find an episode return null
        if(nextShow == null)
        {
            return null;
        }
        
        return nextShow.GetAiring();
    }
    
    
    /**
     * Check to see if there is a gap in the episode numbers.  It does not expect
     * that all of the episodes are present, only that if in a series of episodes
     * there is a number that is missing
     * 
     * Excludes Seasons 0
     * 
     * @return Returns true if there is a gap in episode numbers.  False if there is not
     */
    public boolean IsGapInAirings() throws SageCallApiException
    {
        Integer[] seasons = this.GetShows().GetSeasons();
        int lastEpisodeNumber;
        
        for(int i = 0; i < seasons.length; i++)
        {    
            //Ignore 0 season
            if(seasons[i] > 0)
            {
                Shows shows = this.GetShows().GetShows(seasons[i]);
                
                lastEpisodeNumber = shows.Get(0).GetEpisodeNumber();
                
                for(int j = 1; j < shows.Size(); j++)
                {
                    //If there is a gap in the series (Ep 2, Ep 4, Ep 5) vs (Ep 2, Ep 3, Ep 4, Ep 5)
                    //Allows for duplicate episodes
                    if(shows.Get(j).GetEpisodeNumber() > lastEpisodeNumber + 1)
                    {
                        return true;
                    }
                    
                    lastEpisodeNumber = shows.Get(j).GetEpisodeNumber();
                }
            }
        }
        
        return false;
    }
    
    @Override
    public Object[] UnwrapObject() 
    {
        Object [] unwrapped = new Object[airings.size()];
        
        for(int i = 0; i < airings.size(); i++)
        {
            unwrapped[i] = airings.get(i).UnwrapObject();
        }
        
        return unwrapped;

    }
    
    /**
     * Will delete all of the airings that are watched.  Will stop deleting
     * if one of the files errors when attempting to delete.  Will remove the
     * items from the collection if they are successfully deleted
     * 
     * @return true if all of the files are deleted successfully.  Returns false
     * and stops processing if any of them return an error
     * @throws jvl.sage.SageCallApiException Throws an exception if the call fails
     */
    public boolean DeleteAllWatched() throws SageCallApiException 
    {
        for(int i = airings.size() - 1; i >= 0; i--)
        {
        
                if(airings.get(i).IsWatched())
                {
                    if(airings.get(i).GetMediaFile().DeleteFile())
                    {
                        airings.remove(i);
                    }
                    else
                    {
                        Debug.Writeln("Airings.Verify - Item does not exist on disk and is being removed. Index = " + i, Debug.ERROR);
                        return false;
                    }
                }
        }
        
        return true;
    }
    
    /**
     * Will delete all of the airings that are not currently recording.  Will stop deleting
     * if one of the files errors when attempting to delete.  Will remove the
     * items from the collection if they are successfully deleted
     * 
     * @return true if all of the files are deleted successfully.  Returns false
     * and stops processing if any of them return an error
     * @throws jvl.sage.SageCallApiException Throws an exception if the call fails
     */
    public boolean DeleteAll() throws SageCallApiException 
    {
        for(int i = airings.size() - 1; i >= 0; i--)
        {
            if(!airings.get(i).GetMediaFile().IsFileCurrentlyRecording())
            {
                if(airings.get(i).GetMediaFile().DeleteFile())
                {
                    airings.remove(i);
                }
                else
                {
                    Debug.Writeln("Airings.Verify - Item does not exist on disk and is being removed. Index = " + i, Debug.ERROR);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * Goes through each object in the list and tries to determine if they are
     * still valid files on disk for each.  If not the airings are removed from
     * the list
     */
    public void Verify()
    {
        for(int i = airings.size() - 1; i >= 0; i--)
        {
            if(!airings.get(i).ExistsOnDisk())
            {
                Airing deletedAiring = airings.remove(i);
                Debug.Writeln("Airings.Verify - Item does not exist on disk and is being removed. Index = " + i, Debug.INFO);
                
                randomAirings.remove(deletedAiring);
            }
        }
    }

    @Override
    public Airing Remove(int index) 
    {
        Airing deletedAiring = this.airings.remove(index);
        randomAirings.remove(deletedAiring);
        
        return deletedAiring;
    }

    @Override
    public void Add(Airing d) 
    {
        randomAirings.add(d);
        this.airings.add(d);
    }

    @Override
    public int Size() 
    {
        return this.airings.size();
    }

    @Override
    public Airing Get(int index) 
    {
        return this.airings.get(index);
    }

    @Override
    public void Set(int index, Airing d) 
    {
        this.randomAirings.set(index, d);
        this.airings.set(index, d);
    }
    
}

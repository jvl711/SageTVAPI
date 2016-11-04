package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import jvl.sage.Debug;
import jvl.sage.SageCallApiException;



public class Airings extends SageArrayObject<Airing>
{
    ArrayList<Airing> airings;

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
    
    
    public Shows GetShows()
    {
        Shows shows = new Shows();
       
        for(int i = 0; i < this.airings.size(); i++)
        {
            Object temp = airings.get(i).UnwrapObject();

            Show show = new Show(temp);

            shows.Add(show);
        }
        
        return shows;
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
                airings.remove(i);
                Debug.Writeln("Airings.Verify - Item does not exist on disk and is being removed. Index = " + i, Debug.INFO);
            }
        }
    }

    @Override
    public Airing Remove(int index) 
    {
        return this.airings.remove(index);
    }

    @Override
    public void Add(Airing d) 
    {
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
        this.airings.set(index, d);
    }
    
}

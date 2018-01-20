package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import jvl.sage.Debug;
import jvl.sage.SageCallApiException;
import java.util.Random;



public class Airings extends SageArrayObject<Airing>
{
    //ArrayList<Airing> airings;
    
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
        //airings = new ArrayList();
        randomAirings = new ArrayList();
        //this.baseList = new ArrayList<Airing>();
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

        //this.baseList = new ArrayList<Airing>();
        randomAirings = new ArrayList<Airing>();
        //airings = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                this.add(new Airing(objects[i]));
            }
        }
    }
    
    public Shows GetShows() throws SageCallApiException
    {
        Shows shows = new Shows();
       
        for(int i = 0; i < this.size(); i++)
        {
            shows.add(this.get(i).GetShow());
        }
        
        return shows;
    }
    
    public MediaFiles GetMediaFiles() throws SageCallApiException
    {
        MediaFiles mediafiles = new MediaFiles();
        
        for(int i = 0; i < this.size(); i++)
        {
            mediafiles.add(this.get(i).GetMediaFile());
        }
        
        return mediafiles;
    }
    
    public Airings GetUnwatchedAirings() throws SageCallApiException
    {
        Airings unwatchedAirings = new Airings();
        
        for(int i = 0; i < size(); i++)
        {
            if(!this.get(i).IsWatched())
            {
                unwatchedAirings.add(this.get(i));
            }
        }
        
        System.out.println("Airings.GetUnwatchedAirings: " + unwatchedAirings.size());
        
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
        for(int i = 0; i < this.size(); i++)
        {
            this.get(i).SetWatchedStatus(watched);
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
        for(int i = 0; i < this.size(); i++)
        {
            if(!this.get(i).IsWatched())
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
        if (randomAirings == null || randomAirings.isEmpty())
        {
            randomAirings = new ArrayList<Airing>();
            
            for(int i = 0; i < this.size(); i++)
            {
                randomAirings.add(this.get(i));
            }
        }
        else if (randomAirings.size() == 1)
        {
            return randomAirings.remove(0);
        }
        
        return this.get(random.nextInt(this.size()));
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
                
                for(int j = 0; j < shows.size() && nextShow == null; j++)
                {
                    //Has an episode number, and is not watched
                    if(shows.get(j).GetEpisodeNumber() > 0 && !shows.get(j).GetAiring().IsWatched())
                    {
                        nextShow = shows.get(j);   
                    }
                    
                    //The first show we come in contact with is the oldest
                    if(oldestShow == null)
                    {
                        oldestShow = shows.get(j);
                    }
                }
            }
        }
        
        //If both of these conditions are null than pull the newest unwatched episode
        //Ignoring Season and Episode info
        if(nextShow == null && oldestShow == null)
        {
            
            Shows shows = this.GetShows();

            oldestShow = shows.get(0);
            nextShow = shows.get(0);
            
            for(int j = 0; j < shows.size() && nextShow == null; j++)
            {
                //Has an episode number, and is not watched
                if(!shows.get(j).GetAiring().IsWatched() && nextShow.GetAiring().GetAiringStartTime() < shows.get(j).GetAiring().GetAiringStartTime())
                {
                    nextShow = shows.get(j);   
                }

                //The first show we come in contact with is the oldest
                if(oldestShow.GetAiring().GetAiringStartTime() > shows.get(j).GetAiring().GetAiringStartTime())
                {
                    oldestShow = shows.get(j);
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
                
                lastEpisodeNumber = shows.get(0).GetEpisodeNumber();
                
                for(int j = 1; j < shows.size(); j++)
                {
                    //If there is a gap in the series (Ep 2, Ep 4, Ep 5) vs (Ep 2, Ep 3, Ep 4, Ep 5)
                    //Allows for duplicate episodes
                    if(shows.get(j).GetEpisodeNumber() > lastEpisodeNumber + 1)
                    {
                        return true;
                    }
                    
                    lastEpisodeNumber = shows.get(j).GetEpisodeNumber();
                }
            }
        }
        
        return false;
    }
    
    @Override
    public Object[] UnwrapObject() 
    {
        Object [] unwrapped = new Object[this.size()];
        
        for(int i = 0; i < this.size(); i++)
        {
            unwrapped[i] = this.get(i).UnwrapObject();
        }
        
        return unwrapped;

    }
    
    public boolean DeleteMissingFromDisk() throws SageCallApiException
    {
        for(int i = this.size() - 1; i >= 0; i--)
        {
        
            if(!this.get(i).ExistsOnDisk())
            {
                if(this.get(i).GetMediaFile().DeleteFile())
                {
                    this.remove(i);
                }
                else
                {
                    Debug.Writeln("Airings.DeleteMissingFromDisk - Error Deleting Item. Index = " + i, Debug.ERROR);
                    return false;
                }
            }
        }
        
        return true;
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
        for(int i = this.size() - 1; i >= 0; i--)
        {
        
            if(this.get(i).IsWatched())
            {
                if(this.get(i).GetMediaFile().DeleteFile())
                {
                    this.remove(i);
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
        for(int i = this.size() - 1; i >= 0; i--)
        {
            if(!this.get(i).GetMediaFile().IsFileCurrentlyRecording())
            {
                if(this.get(i).GetMediaFile().DeleteFile())
                {
                    this.remove(i);
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
     * Modifying this to not worry about if the file is on disk, instead check
     * if it still has a media file in the database
     */
    public void Verify()
    {
        for(int i = this.size() - 1; i >= 0; i--)
        {
            boolean exists = false;
            
            try
            {
                Object airing = this.get(i).UnwrapObject();
                Object mediaFile = Airing.GetMediaFileForAiring(airing);
                exists = MediaFile.IsMediaFileObject(mediaFile);
            }
            catch(Exception ex)
            {
                exists = false;
            }
            
            if(!exists)
            {
                Airing deletedAiring = this.remove(i);
                Debug.Writeln("Airings.Verify - Item does not exist in DB removed. Index = " + i, Debug.INFO);
                
                randomAirings.remove(deletedAiring);
            }
        }
    }

    /*
    @Override
    public Airing remove(int i)
    {
        
    }
    
    @Override
    public Airing set(int i, Airing e) 
    {
        this.randomAirings.set(i, e);
        return this.baseList.set(i, e);
    }
    
    

    @Override
    public boolean add(Airing e) 
    {
        randomAirings.add(e);
        return baseList.add(e);        
    }

    @Override
    public void add(int i, Airing e) 
    {
        randomAirings.add(1, e);
        this.baseList.add(i, e);
    }
    */
    
    /**
     * @deprecated 
     * @param index
     * @return 
     */
    //@Override
    //public Airing Remove(int index) 
    //{
        //this.remove(this)
        //randomAirings.remove(deletedAiring);
    //    System.out.println("JVL - Deprecated called (Airings.Remove)");
    //    return this.remove(index);
    //}
    
    /**
     * @deprecated 
     * @return 
     */
    //@Override
    //public int Size() 
    //{
    //    return this.size();
    //}

    /**
     * @deprecated 
     * @param index
     * @return 
     */
    //@Override
    //public Airing Get(int index) 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Get)");
    //    return this.get(index);
    //}

    /**
     * @deprecated 
     * @param d 
     */
    //@Override
    //public void Add(Airing d) 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Add)");
    //    this.add(d);
    //}
    
    
    
    /**
     * @deprecated 
     * @param index
     * @param d 
     */
    //@Override
    //public void Set(int index, Airing d) 
    //{
    //    System.out.println("JVL - Deprecated called (Airings.Set)");
    //    this.set(index, d);
    //}
    
}

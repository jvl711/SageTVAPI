
package jvl.sage.api;

import java.util.ArrayList;
import jvl.sage.SageArrayObject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import jvl.sage.Debug;
import jvl.sage.JobStatus;
import jvl.sage.SageCallApiException;

public class Shows extends SageArrayObject<Show>
{
    //ArrayList<Show> shows;

    public Shows()
    {
        //this.shows = new ArrayList();
    }
    
    public Shows(Object object) throws SageCallApiException
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
            System.out.println("Shows - Passed type = Unknow");
            System.out.println(object.toString());
        }
        
        
        //shows = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                this.add(new Show(objects[i]));
            }
        }
    }
    
    public Airings GetAirings()
    {
        Airings airings = new Airings();
       
        for(int i = 0; i < this.size(); i++)
        {
            airings.add(this.get(i).GetAiring());
        }
        
        return airings;
    }
    
    public MediaFiles GetMediaFiles()
    {
        MediaFiles mediafiles = new MediaFiles();
        
        for(int i = 0; i < this.size(); i++)
        {
            mediafiles.add(this.get(i).GetMediaFile());
        }
                
        return mediafiles;
    }
    
    public boolean MetadataLookup(boolean forceRefresh, boolean blocking)
    {
        boolean result = true;
        
        for(int i = 0 ; i < this.size(); i++)
        {
            try
            {
                if(!this.get(i).MetadataLookup(forceRefresh, blocking))
                {
                    result = false;
                }
            }
            catch(Exception ex)
            {
                System.out.println("JVL - Error looking up metadata: " + ex.getMessage());
            }
        }
        
        return result;
    }
    
    public boolean MetadataLookup()
    {
        boolean result = true;
        
        for(int i = 0 ; i < this.size(); i++)
        {
            try
            {
                if(!this.get(i).MetadataLookup())
                {
                    result = false;
                }
            }
            catch(Exception ex)
            {
                System.out.println("JVL - Error looking up metadata: " + ex.getMessage());
            }
        }
        
        return result;
    }
    
    public ArrayList<String> GetCategroies() throws SageCallApiException
    {
        ArrayList<String> categories = new ArrayList<String>();
        
        for(int i = 0; i < this.size(); i++)
        {
            String [] temp = this.get(i).GetCategories();
            
            for(int j = 0; j < temp.length; j++)
            {
                if(!categories.contains(temp[j]))
                {
                    categories.add(temp[j]);
                }
            }
        }
        
        
        //categories.sort(String.CASE_INSENSITIVE_ORDER);

        return categories;
    }
    
    public void FilterByCategory(String Category) throws SageCallApiException
    {
        ArrayList<Show> shows = new ArrayList<Show>();
        
        for(int i = 0; i < this.size(); i++)
        {
            String [] categories = this.get(i).GetCategories();
            
            for(int j = 0; j < categories.length; j++)
            {
                if(categories[j].equalsIgnoreCase(Category))
                {
                    shows.add(this.get(i));
                    break;
                }
            }
        }
        
        this.baseList = shows;
        
        //return shows;
    }
    
    public int GetSeasonCount() throws SageCallApiException
    {
        return this.GetSeasons().length;
    }
    
    public Integer [] GetSeasons() throws SageCallApiException
    {
        Integer [] ret;
        LinkedList list = new LinkedList();
        
        for(int i = 0; i < this.size(); i++)
        {
            Integer temp = this.get(i).GetSeasonNumber();
            
            if(!list.contains(temp))
            {
                list.add(temp);
            }
        }
        
        ret = new Integer[list.size()];
        
        for(int i = 0; i < list.size(); i++)
        {
            ret[i] = (Integer)list.get(i);
        }
        
        java.util.Arrays.sort(ret);
        
        return ret;
    }
    
    public Shows GetShows(int season) throws SageCallApiException
    {
        Shows retTemp = new Shows();
        Shows retSorted = new Shows();
        
        for(int i = 0; i < this.size(); i++)
        {
            if(this.get(i).GetSeasonNumber() == season)
            {
                retTemp.add(this.get(i));
            }
        }
        
        for(int i = 0; i < retTemp.size(); i++)
        {
            //Get next compare in the list
            Show next = retTemp.get(i);
            
            for(int j = (i + 1); j < retTemp.size(); j++)
            {
                Show compare = retTemp.get(j);
                
                //Swap the items
                if(compare.GetEpisodeNumber() < next.GetEpisodeNumber())
                {
                    retTemp.set(j, next);
                    next = compare;
                }
            }
            
            retSorted.add(next);
        }

        return retSorted;
    }
    
    public void SortByTitle()
    {
        this.SortByTitle(false);
    }
    
    public void SortByTitle(boolean desc)
    {
        Collections.sort(this, new SortableShowTitleCompaator());
        
        if(desc)
        {
            Collections.reverse(this);
        }
    }
    
    /**
     * Sorts the shows ascending by the Airing Date Start
     */
    public void SortByDateAdded()
    {
        this.SortByTitle(false);
    }
    
    /**
     * Sorts the shows ascending by the Airing Date Start
     * @param desc Switches sort to descending if true
     */
    public void SortByDateAdded(boolean desc)
    {
        Collections.sort(this, new SortableShowDateAddedComparator());
        
        if(desc)
        {
            Collections.reverse(this);
        }
    }
    
    /**
     * Sorts the shows ascending by the by the year released as the primary
     * sort, than the title
     */
    public void SortByYearReleased()
    {
        this.SortByTitle(false);
    }
    
    /**
     * Sorts the shows ascending by the by the year released as the primary
     * sort, than the title
     * @param desc Switches sort to descending if true
     */
    public void SortByYearReleased(boolean desc)
    {
        Collections.sort(this, new SortableShowYearReleasedCompaator());
        
        if(desc)
        {
            Collections.reverse(this);
        }
    }
    
    /*
    public JobStatus CleanPosters(int width)
    {
        JobStatus jobStatus = new JobStatus();
        
        CleanPostersThread cleanPostersThread = new CleanPostersThread(this, width, jobStatus);
        Thread thread = new Thread(cleanPostersThread);
        thread.start();
        
        return jobStatus;
    }
    */
    
    /*
    public JobStatus ScalePosters(int width)
    {
        JobStatus jobStatus = new JobStatus();
        
        ScalePostersThread scalePosterThread = new ScalePostersThread(this, width, jobStatus);
        Thread thread = new Thread(scalePosterThread);
        thread.start();
        
        
        return jobStatus;
    }
    */
    
    public Show GetShowBySearchChar(String searchChar) throws SageCallApiException
    {
        return this.GetShowBySearchChar(searchChar.toUpperCase().charAt(0));
    }
    
    public Show GetShowBySearchChar(char searchChar) throws SageCallApiException
    {
        Show lastShow = this.get(0);
        
        for(int i = 1; i < this.size(); i++)
        {
            if(this.get(i).GetSortableTitle().toUpperCase().charAt(0) < searchChar)
            {
                lastShow = this.get(i);
            }
            else if(this.get(i).GetSortableTitle().toUpperCase().charAt(0) == searchChar)
            {
                lastShow = this.get(i);
                break;
            }
            else
            {
                break;
            }
        }
        
        return lastShow;
    }
    
    public int GetIndex(Show show)
    {
        return this.indexOf(show);
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

    
    /*
    private class ScalePostersThread implements Runnable
    {
        private JobStatus jobStatus;
        private Shows shows;
        private int width;
        
        public ScalePostersThread(Shows shows, int width, JobStatus jobStatus)
        {
           this.jobStatus = jobStatus;
           this.shows = shows;
           this.width = width;
        }
        
        @Override
        public void run() 
        {
            HashMap<String, Show> map = new HashMap<String, Show>();
            
            Debug.Writeln("Scale posters thread is starting", Debug.INFO);
            this.jobStatus.SetRunning();
            
            //Build a hashmap of Show Title + Season, So that we only process them once...
            //This should be fine for the movies as well.
            try
            {
                for(int i = 0; i < this.shows.size(); i++)
                {
                    String key = shows.get(i).GetTitle() + shows.get(i).GetSeasonNumberString();
                    
                    if(!map.containsKey(key))
                    {
                        map.put(key, shows.get(i));
                    }
                }
            }
            catch(Exception ex)
            {
                Debug.Writeln("Error building the hashmap for poster scaling thread.", Debug.ERROR);
                Debug.WriteStackTrace(ex, Debug.ERROR);
                //Exit the thread.  We are in an unknown state...
                return;
            }
            
            
            try
            {
                String [] keys = map.keySet().toArray(new String[map.size()]);
                
                for(int i = 0; i < keys.length; i++)
                {
                    Show show = map.get(keys[i]);
                    show.ScalePosters(width);
                    
                    this.jobStatus.SetStatusMessage("Processing " + (i + 1) + " of " + keys.length + " - " + show.GetTitle());
                }
                
                this.jobStatus.SetComplete();
            }
            catch(Exception ex)
            {
                Debug.Writeln("Error scaling posters", Debug.ERROR);
                Debug.WriteStackTrace(ex, Debug.ERROR);
                this.jobStatus.SetError("Error scaling posters: " + ex.getMessage());
            }
            
            Debug.Writeln("Scale posters thread has completed", Debug.INFO);
        }
        
    }
    */

    
    /*
    private class CleanPostersThread implements Runnable
    {
        private JobStatus jobStatus;
        private Shows shows;
        private int width;
        
        public CleanPostersThread(Shows shows, int width, JobStatus jobStatus)
        {
           this.jobStatus = jobStatus;
           this.shows = shows;
           this.width = width;
        }
        
        @Override
        public void run() 
        {
            this.jobStatus.SetRunning();
            
            try
            {
                for(int i = 0; i < this.shows.size(); i++)
                {
                    shows.get(i).CleanPosters(width);
                    
                    this.jobStatus.SetStatusMessage("Processing " + (i + 1) + " of " + this.shows.size() + " - " + shows.get(i).GetTitle());
                }
                
                this.jobStatus.SetComplete();
            }
            catch(Exception ex)
            {
                this.jobStatus.SetError("Error cleaning posters: " + ex.getMessage());
            }
        }
        
    }
    */
}

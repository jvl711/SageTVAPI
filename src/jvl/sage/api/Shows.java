
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
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
    
    public JobStatus CleanPosters(int width)
    {
        JobStatus jobStatus = new JobStatus();
        
        CleanPostersThread cleanPostersThread = new CleanPostersThread(this, width, jobStatus);
        Thread thread = new Thread(cleanPostersThread);
        thread.start();
        
        return jobStatus;
    }
    
    public JobStatus ScalePosters(int width)
    {
        JobStatus jobStatus = new JobStatus();
        
        ScalePostersThread scalePosterThread = new ScalePostersThread(this, width, jobStatus);
        Thread thread = new Thread(scalePosterThread);
        thread.start();
        
        
        return jobStatus;
    }
    
    public Show GetShowBySearchChar(String searchChar) throws SageCallApiException
    {
        return this.GetShowBySearchChar(searchChar.toUpperCase().charAt(0));
    }
    
    public Show GetShowBySearchChar(char searchChar) throws SageCallApiException
    {
        Show lastShow = this.get(0);
        
        for(int i = 1; i < this.size(); i++)
        {
            if(this.get(i).GetSortableShowTitle().toUpperCase().charAt(0) < searchChar)
            {
                lastShow = this.get(i);
            }
            else if(this.get(i).GetSortableShowTitle().toUpperCase().charAt(0) == searchChar)
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
    
    /**
     * @deprecated 
     */
    //@Override
    //public Show Remove(int index) 
    //{
    //    System.out.println("JVL - Deprecated called (Shows.Remove)");
    //    return this.remove(index);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public Show Get(int index) 
    //{
    //    System.out.println("JVL - Deprecated called (Shows.Get)");
    //    return this.get(index);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public void Add(Show d) 
    //{
    //    System.out.println("JVL - Deprecated called (Shows.Add)");
    //    this.add(d);
    //}

    /**
     * @deprecated 
     */
    //@Override
    //public int Size() 
    //{
    //    System.out.println("JVL - Deprecated called (Shows.Size)");
    //    return this.size();
    //}
    
    /**
     * @deprecated 
     */
    //@Override
    //public void Set(int index, Show d) 
    //{
    //    System.out.println("JVL - Deprecated called (Shows.Set)");
    //    this.set(index, d);
    //}
    
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
            this.jobStatus.SetRunning();
            
            //Build a hashmap of Show Title + Season, and only process them once.
            
            
            
            try
            {
                for(int i = 0; i < this.shows.size(); i++)
                {
                    shows.get(i).ScalePosters(width);
                    
                    this.jobStatus.SetStatusMessage("Processing " + (i + 1) + " of " + this.shows.size() + " - " + shows.get(i).GetShowTitle());
                }
                
                this.jobStatus.SetComplete();
            }
            catch(Exception ex)
            {
                this.jobStatus.SetError("Error scaling posters: " + ex.getMessage());
            }
        }
        
    }
    
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
                    
                    this.jobStatus.SetStatusMessage("Processing " + (i + 1) + " of " + this.shows.size() + " - " + shows.get(i).GetShowTitle());
                }
                
                this.jobStatus.SetComplete();
            }
            catch(Exception ex)
            {
                this.jobStatus.SetError("Error cleaning posters: " + ex.getMessage());
            }
        }
        
    }
}

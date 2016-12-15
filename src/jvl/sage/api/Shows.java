
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import jvl.sage.SageCallApiException;

public class Shows extends SageArrayObject<Show>
{
    ArrayList<Show> shows;

    public Shows()
    {
        this.shows = new ArrayList();
    }
    
    public Shows(Object object)
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
        
        
        shows = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                shows.add(new Show(objects[i]));
            }
        }
    }
    
    public int GetSeasonCount() throws SageCallApiException
    {
        return this.GetSeasons().length;
    }
    
    public Integer [] GetSeasons() throws SageCallApiException
    {
        Integer [] ret;
        LinkedList list = new LinkedList();
        
        for(int i = 0; i < this.shows.size(); i++)
        {
            Integer temp = this.shows.get(i).GetSeasonNumber();
            
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
        
        for(int i = 0; i < shows.size(); i++)
        {
            if(shows.get(i).GetSeasonNumber() == season)
            {
                retTemp.Add(shows.get(i));
            }
        }
        
        for(int i = 0; i < retTemp.Size(); i++)
        {
            //Get next compare in the list
            Show next = retTemp.Get(i);
            
            for(int j = (i + 1); j < retTemp.Size(); j++)
            {
                Show compare = retTemp.Get(j);
                
                //Swap the items
                if(compare.GetEpisodeNumber() < next.GetEpisodeNumber())
                {
                    retTemp.Set(j, next);
                    next = compare;
                }
            }
            
            retSorted.Add(next);
        }

        return retSorted;
    }
    
    public Airings GetAirings()
    {
        Airings airings = new Airings();
       
        for(int i = 0; i < this.shows.size(); i++)
        {
            Object temp = shows.get(i).UnwrapObject();

            Airing airing = new Airing(temp);

            airings.Add(airing);
        }
        
        return airings;
    }
    
    public void SortByTitle()
    {
        this.SortByTitle(false);
    }
    
    public void SortByTitle(boolean desc)
    {
        Collections.sort(shows, new SortableShowTitleCompaator());
        
        if(desc)
        {
            Collections.reverse(shows);
        }
    }
    
    @Override
    public Show Remove(int index) 
    {
        return this.shows.remove(index);
    }

    @Override
    public Show Get(int index) 
    {
        return this.shows.get(index);
    }

    @Override
    public void Add(Show d) 
    {
        this.shows.add(d);
    }

    @Override
    public int Size() 
    {
        return this.shows.size();
    }
    
        @Override
    public Object[] UnwrapObject() 
    {
        Object [] unwrapped = new Object[shows.size()];
        
        for(int i = 0; i < shows.size(); i++)
        {
            unwrapped[i] = shows.get(i).UnwrapObject();
        }
        
        return unwrapped;
    }

    @Override
    public void Set(int index, Show d) 
    {
        this.shows.set(index, d);
    }
    
}

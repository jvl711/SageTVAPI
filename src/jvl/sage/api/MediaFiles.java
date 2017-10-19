
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import jvl.sage.SageCallApiException;


public class MediaFiles extends SageArrayObject<MediaFile>
{
    ArrayList<MediaFile> mediafiles;
    
    public MediaFiles()
    {
        mediafiles = new ArrayList<MediaFile>();
    }
    
    public MediaFiles(Object object)
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
            System.out.println("MediaFiles - Passed type = Unknown");
            System.out.println(object.toString());
        }

        mediafiles = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                mediafiles.add(new MediaFile(objects[i]));
            }
        }
    }

    public Shows GetShows() throws SageCallApiException
    {
        Shows shows = new Shows();
        
        for(int i = 0; i < this.mediafiles.size(); i++)
        {
            shows.Add(mediafiles.get(i).GetShow());
        }
        
        return shows;
    }
    
    public Airings GetAirings()
    {
        Airings airings = new Airings();
        
        for(int i = 0; i < this.mediafiles.size(); i++)
        {
            airings.Add(mediafiles.get(i).GetAiring());
        }
        
        return airings;
    }
    
    public void FilterByMetadata(String field, String value)
    {
        ArrayList<MediaFile> tempmediafiles = new ArrayList<MediaFile>();
        
        for(int i = 0; i < this.mediafiles.size(); i++)
        {
            try
            {
                if(this.mediafiles.get(i).GetMetadata(field).equalsIgnoreCase(value))
                {
                    tempmediafiles.add(this.mediafiles.get(i));
                }
            }
            catch(Exception ex)
            {
                //Assume that if there is an error there was no meta data on the file
            }
        }
        
        this.mediafiles = tempmediafiles;
    }
    
    @Override
    public MediaFile Remove(int index) 
    {
        return mediafiles.remove(index);
    }

    @Override
    public MediaFile Get(int index) 
    {
        return mediafiles.get(index);
    }

    @Override
    public void Set(int index, MediaFile d) 
    {
        mediafiles.set(index, d);
    }

    @Override
    public void Add(MediaFile d) 
    {
        mediafiles.add(d);
    }

    @Override
    public int Size() 
    {
        return mediafiles.size();
    }
    
    @Override
    public Object[] UnwrapObject() 
    {
        Object[] temp = new Object[mediafiles.size()];
        
        for(int i = 0; i < mediafiles.size(); i++)
        {
            temp[i] = mediafiles.get(i).UnwrapObject();
        }
        
        return temp;
    }
}


package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;


public class MediaFiles extends SageArrayObject<MediaFile>
{
    ArrayList<MediaFile> mediafiles;
    
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
            temp[i] = mediafiles.get(i);
        }
        
        return temp;
    }
}

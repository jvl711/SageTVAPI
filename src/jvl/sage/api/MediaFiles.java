
package jvl.sage.api;

import jvl.sage.SageArrayObject;
import java.util.ArrayList;
import java.util.Collection;
import jvl.sage.SageCallApiException;


public class MediaFiles extends SageArrayObject<MediaFile>
{
    //ArrayList<MediaFile> mediafiles;
    
    public MediaFiles()
    {
        //mediafiles = new ArrayList<MediaFile>();
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

        //mediafiles = new ArrayList();
        
        if(objects != null)
        {
            for(int i = 0; i < objects.length; i++)
            {
                this.add(new MediaFile(objects[i]));
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
    
    public Airings GetAirings()
    {
        Airings airings = new Airings();
        
        for(int i = 0; i < this.size(); i++)
        {
            airings.add(this.get(i).GetAiring());
        }
        
        return airings;
    }
    
    public void FilterByMetadata(String field, String value)
    {
        ArrayList<MediaFile> tempmediafiles = new ArrayList<MediaFile>();
        
        for(int i = 0; i < this.size(); i++)
        {
            try
            {
                if(this.get(i).GetMetadata(field).equalsIgnoreCase(value))
                {
                    tempmediafiles.add(this.get(i));
                }
            }
            catch(Exception ex)
            {
                //Assume that if there is an error there was no meta data on the file
            }
        }
        
        this.baseList = tempmediafiles;
    }
    
    public boolean IsFileCurrentlyRecording() throws SageCallApiException
    {
        for(int i = 0; i < this.size(); i++)
        {
            if(this.get(i).IsFileCurrentlyRecording())
            {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Object[] UnwrapObject() 
    {
        Object[] temp = new Object[this.size()];
        
        for(int i = 0; i < this.size(); i++)
        {
            temp[i] = this.get(i).UnwrapObject();
        }
        
        return temp;
    }
}

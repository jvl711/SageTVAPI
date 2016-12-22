package jvl.sage.api;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;
import phoenix.fanart;
import jvl.AdvancedImage;
import jvl.sage.Debug;


public class Show extends SageObject
{
    private Object show;
    
    public Show(Object show)
    {
        this.show = show;
    }
    
    public MediaFile GetMediaFile()
    {
        return new MediaFile(this.UnwrapObject());
    }
    
    public Airing GetAiring()
    {
        return new Airing(this.UnwrapObject());
    }
    
    public int GetSeasonNumber() throws SageCallApiException 
    {
        int response = 0;
        
        response = callApiInt("GetShowSeasonNumber", this.show);
        
        return response;
    }
    
    public int GetEpisodeNumber() throws SageCallApiException
    {
        int response = 0;
        
        response = callApiInt("GetShowEpisodeNumber", this.show);
        
        return response;
    }

    public String GetShowTitle() throws SageCallApiException
    {
        return callApiString("GetShowTitle", this.show);
    }
 
    public char GetShowTitleSearchChar() throws SageCallApiException
    {
        String title = this.GetSortableShowTitle();
        
        if(title.length() > 0)
        {
            return this.GetSortableShowTitle().charAt(0);
        }
        else
        {
            return ' ';
        }
    }
    
    public String GetSortableShowTitle() throws SageCallApiException
    {
        String title = this.GetShowTitle();
        String ret = title;
        
        if(title.startsWith("The "))
        {
            ret = title.replaceFirst("The ", "");
        }
        else if(title.startsWith("A "))
        {
            ret = title.replaceFirst("A ", "");
        }
        else if(title.startsWith("\"") && title.endsWith("\""))
        {
            //Remove the beginig and end Quote
            ret = title.substring(1, title.length() - 1);
        }
        else if(title.startsWith("'") && title.endsWith("'"))
        {
            //Remove the beginig and end Quote
            ret = title.substring(1, title.length() - 1);
        }
        
        return ret;
    }

    public String [] GetPosters()
    {
        return fanart.GetFanartPosters(this.show);
    }
    
    /**
     * Delete all posters that are not as large as specified width
     * Also deletes any poster that does not fall into specified 
     * ratio of width / height between .68 and .64
     * @param Width Any poster with a width less than this will be deleted
     */
    public void CleanPosters(int Width)
    {
        String [] posters = fanart.GetFanartPosters(this.show);
        
        if(posters != null)
        {
            for(int i = 0; i < posters.length; i++)
            {
                try
                {
                    AdvancedImage image = new AdvancedImage(posters[i]);

                    Double ratio = (image.getWidth() * 1.0) / (image.getHeight() * 1.0);
                    
                    //Delete the poster if it is not in the standard format
                    if(ratio < 0.64 || ratio > 0.68 || image.getWidth() < Width)
                    {
                        File file = new File(posters[i]);
                        file.delete();
                    }
                }
                catch(IOException ex2)
                {
                    //TODO: Add error logging
                }
            }
        }
    }
    
    /***
     * Scales all posters for this show to the given width maintaining
     * aspect ratio
     * @param Width 
     */
    public void ScalePosters(int width)
    {
        String [] posters = fanart.GetFanartPosters(this.show);
        
        if(posters != null)
        {
            for(int i = 0; i < posters.length; i++)
            {
                try
                {
                    //TODO: Only resize if the poster is > than the specified width

                    
                    AdvancedImage image = new AdvancedImage(posters[i]);
                    
                    if(image.getWidth() > width)
                    {
                        image.ResizeImageByWidth(width, true);
                        image.SaveImageToFile(posters[i]);
                    }
                }
                catch(IllegalArgumentException ex1)
                {
                    Debug.Writeln("jvl.sage.api.Show.ScalePoser:  Error scaling poster - " + posters[i], Debug.ERROR);
                    Debug.WriteStackTrace(ex1, Debug.ERROR);
                }
                catch(IOException ex2)
                {
                    Debug.Writeln("jvl.sage.api.Show.ScalePoser:  Error scaling poster - " + posters[i], Debug.ERROR);
                    Debug.WriteStackTrace(ex2, Debug.ERROR);
                }
            }
        }
        
    }
    
    @Override
    public Object UnwrapObject() 
    {
        return this.show;
    }
    
}

class SortableShowTitleCompaator implements Comparator<Show>
{

    @Override
    public int compare(Show t, Show t1) 
    {
        try 
        {   
            return t.GetSortableShowTitle().compareTo(t1.GetSortableShowTitle());
            
        } 
        catch (SageCallApiException ex) 
        {
            
        }
        
        return 0;
        
    }
    
}
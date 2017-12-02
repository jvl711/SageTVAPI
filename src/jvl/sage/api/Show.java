package jvl.sage.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;
import phoenix.fanart;
import jvl.AdvancedImage;
import jvl.sage.Debug;


public class Show extends SageObject
{
    public static final double MINIMUM_POSTER_RATIO = 0.64;
    public static final double MAXIMUM_POSTER_RATIO = 0.68;

    //private Object show;
    private Object mediafile;
    private Object airing;
    private Object lookupObject;
    
    /**
     * This constructor will only construct if it is given a
     * MediaFile or Airing. This is to make sure that a conversion
     * between Airing, Show and MediaFile is properly maintained
     * 
     * @param input 
     * @throws jvl.sage.SageCallApiException 
     */
    public Show(Object input) throws SageCallApiException
    {
        if(MediaFile.IsMediaFileObject(input))
        {
            this.mediafile = input;
            this.airing = MediaFile.GetMediaFileAiring(mediafile);
            //this.show = Airing.GetShowForAiring(airing);
            
        }
        else if(Airing.IsAiringObject(input))
        {
            this.airing = input;
            this.mediafile = Airing.GetMediaFileForAiring(airing);
            //this.show = Airing.GetShowForAiring(airing);
        }
        else
        {
            throw new RuntimeException("JVL - The input is not an Airing or MediaFile.");
        }
        
        if(mediafile == null)
        {
            this.lookupObject = airing;
        }
        else
        {
            this.lookupObject = mediafile;
        }
    }
    
    /**
     * Returns true if the argument is an Show object. Automatic type conversion is NOT done in this call.
     * 
     * @param testObject the object to test
     * @return true if the argument is an Show object
     * @throws SageCallApiException 
     */
    public static boolean IsShowObject(Object testObject) throws SageCallApiException
    {
        return Airing.callAPIBoolean("IsShowObject", testObject);
    }
    
    public MediaFile GetMediaFile()
    {
        return new MediaFile(this.mediafile);
    }
    
    public Airing GetAiring()
    {
        return new Airing(this.airing);
    }
    
    public int GetSeasonNumber() throws SageCallApiException 
    {
        int response = 0;
        
        response = callApiInt("GetShowSeasonNumber", this.lookupObject);
        
        return response;
    }
    
    public int GetEpisodeNumber() throws SageCallApiException
    {
        int response = 0;
        
        response = callApiInt("GetShowEpisodeNumber", this.lookupObject);
        
        return response;
    }

    public String GetShowTitle() throws SageCallApiException
    {
        return callApiString("GetShowTitle", this.lookupObject);
    }
    
    public String GetShowYear() throws SageCallApiException
    {
        return callApiString("GetShowYear", this.lookupObject);
    }
 
    public String [] GetShowCategories() throws SageCallApiException
    {
        return (String []) Show.callApiArray("GetShowCategoriesList", this.lookupObject);
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

    public String GetPoster()
    {
        String poster = fanart.GetFanartPoster(this.lookupObject);
        
        File file = null;

        //If it returns null sttempt to clear cache and pickup new poster
        if(poster == null)
        {
            //System.out.println("JVL Debug - Getting poster returned null...");
            //System.out.println("JVL Debug - Fanart direcotry: " + fanart.GetFanartCentralFolder());
            //System.out.println("JVL Debug - Clearing cache to attemp to get poster.");
            fanart.ClearMemoryCaches();
            poster = fanart.GetFanartPoster(this.lookupObject);
            //System.out.println("JVL Debug - Second poster call attemp: " + poster);
        }
        
        if(poster != null)
        {
            file = new File(poster);
        }
                
        if(file == null || !file.exists())
        {
            String [] posters = null;
            
            
            try
            {
                //System.out.println("JVL Debug - Attempting to get all posters... ");
                posters = this.GetPosters();
            }
            catch(Exception ex)
            {
                //System.out.println("JVL Debug - Failed with error: " + ex.getMessage());
            }

            if(posters != null && posters.length > 0)
            {
                poster = posters[0];
            }
            
            //TODO:  Investigate further.  Not sure why, but when I set a new poster
            // on one client, it is not respected on the other.
            //fanart.SetFanartPoster(show, poster);
        }
        
        return poster;
    }
    
    public String GetBackground()
    {
        String background = fanart.GetFanartBackground(this.lookupObject);
        File file = null;
        
        if(background != null)
        {
            file = new File(background);
        }
                
        if(file == null || !file.exists())
        {
            String [] backgrounds = this.GetBackgrounds();
            
            if(backgrounds.length > 0)
            {
                background = backgrounds[0];
            }
            
            //TODO:  Investigate further.  Not sure why, but when I set a new poster
            // on one client, it is not respected on the other.
            //fanart.SetFanartPoster(show, background);
        }
        
        return background;
    }
    
    public void SetPoster(String poster)
    {
        fanart.SetFanartPoster(this.lookupObject, poster);
    }
    
    public void SetBackground(String background)
    {
        fanart.SetFanartBackground(this.lookupObject, background);
    }
    /**
     * Returns all of the posters for the show.  Verifies that the poster
     * exists.
     * @return String [] of paths to posters that have been verified to exist
     */
    public String [] GetPosters()
    {
        String [] posters = fanart.GetFanartPosters(this.lookupObject);
        ArrayList temp = new ArrayList();
        
        
        for(int i = 0; i < posters.length; i++)
        {
            File file = new File(posters[i]);
            
            if(file.exists())
            {
                temp.add(posters[i]);
            }
        }
        
        if(temp.size() > 0)
        {
            posters = new String[temp.size()];
            
            for(int i = 0; i < temp.size(); i++)
            {
                posters[i] = (String)temp.get(i);
            }
        }
        else
        {
            posters = null;
        }
        
        return posters;
    }
    
    public String [] GetBackgrounds()
    {
        String [] backgrounds = fanart.GetFanartBackgrounds(this.lookupObject);
        ArrayList temp = new ArrayList();
        
        for(int i = 0; i < backgrounds.length; i++)
        {
            File file = new File(backgrounds[i]);
            
            if(file.exists())
            {
                temp.add(backgrounds[i]);
            }
        }
        
        if(temp.size() > 0)
        {
            backgrounds = new String[temp.size()];
            
            for(int i = 0; i < temp.size(); i++)
            {
                backgrounds[i] = (String)temp.get(i);
            }
        }
        else
        {
            backgrounds = null;
        }
        
        return backgrounds;
    }
    
    /**
     * Delete all posters that are not as large as specified width
     * Also deletes any poster that does not fall into specified 
     * ratio of width / height between .68 and .64
     * @param Width Any poster with a width less than this will be deleted
     */
    public void CleanPosters(int Width)
    {
        String [] posters = fanart.GetFanartPosters(this.lookupObject);
        
        if(posters != null)
        {
            for(int i = 0; i < posters.length; i++)
            {
                try
                {
                    AdvancedImage image = new AdvancedImage(posters[i]);

                    Double ratio = (image.getWidth() * 1.0) / (image.getHeight() * 1.0);
                    
                    //Delete the poster if it is not in the standard format
                    if(ratio < Show.MINIMUM_POSTER_RATIO || ratio > Show.MAXIMUM_POSTER_RATIO || image.getWidth() < Width)
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
        String [] posters = fanart.GetFanartPosters(this.lookupObject);
        
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
                    Debug.Writeln("ScalePoser:  Error scaling poster - " + posters[i], Debug.ERROR);
                    Debug.WriteStackTrace(ex1, Debug.ERROR);
                }
                catch(IOException ex2)
                {
                    Debug.Writeln("ScalePoser:  Error scaling poster - " + posters[i], Debug.ERROR);
                    Debug.WriteStackTrace(ex2, Debug.ERROR);
                }
            }
        }
        
    }
    
    @Override
    public Object UnwrapObject() 
    {
        if(mediafile == null)
        {
            return this.airing;
        }
        
        return this.mediafile;
    }
    
}

class SortableShowTitleCompaator implements Comparator<Show>
{

    @Override
    public int compare(Show t, Show t1) 
    {
        try 
        {   
            return t.GetSortableShowTitle().toUpperCase().compareTo(t1.GetSortableShowTitle().toUpperCase());
            
        } 
        catch (SageCallApiException ex) 
        {
            
        }
        
        return 0;
        
    }
    
}

class SortableShowDateAddedComparator implements Comparator<Show>
{

    @Override
    public int compare(Show t, Show t1) 
    {
        try 
        {   
            Long first = t.GetAiring().GetAiringStartTime();
            Long second = t1.GetAiring().GetAiringStartTime();
            
            return first.compareTo(second);
        } 
        catch (SageCallApiException ex) 
        {
            
        }
        
        return 0;
        
    }
    
}


/**
 * First checks for year, than checks sortable title. If there is no year
 * or the year is able to be parsed than it defaults to title.
 */
class SortableShowYearReleasedCompaator implements Comparator<Show>
{

    @Override
    public int compare(Show t, Show t1) 
    {
        try 
        {   
            int first = 0;
            int second = 0;
            
            try{ first = Integer.parseInt(t.GetShowYear()); } catch(Exception ex) { }
            try{ second = Integer.parseInt(t1.GetShowYear()); } catch(Exception ex) { }
            
            
            //if they are the same than use title
            if(first == second)
            {
                return t.GetSortableShowTitle().toUpperCase().compareTo(t1.GetSortableShowTitle().toUpperCase());
            }
            else if(first > second)
            {
                return 1;
            }
            else
            {
                return -1;
            }
        } 
        catch (SageCallApiException ex) 
        {
            
        }
        
        return 0;
        
    }
    
}
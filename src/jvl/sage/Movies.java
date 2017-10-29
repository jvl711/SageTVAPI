
package jvl.sage;

import jvl.sage.api.Configuration;
import jvl.sage.api.Shows;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.UIContext;


/*
 * Helper class for displaying movies
*/
public class Movies 
{
    public static final String PROPERTY_PREFIX = "jvl.movies";
    public static final String PROPERTY_SORT_DIR = PROPERTY_PREFIX + ".sort.direction";
    
    private UIContext context;
    
    public Movies(String context)
    {
        System.out.println("JVL - Movies Constructor Called: " + context);
        this.context = new UIContext(context);
    }
    
    public void SetSortDirection(SortDirection sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_DIR, sort.GetName());
    }
    
    public SortDirection GetSortDirection()
    {
        return SortDirection.Parse(Configuration.GetProperty(context, PROPERTY_SORT_DIR, SortDirection.GetDefault().GetName()));
    }
    
    public Shows GetMovies() throws SageCallApiException
    {
        System.out.println("JVL - Movies GetMovies Called: " + this.context);
        
        try
        {
            MediaFiles mediaFiles = MediaFile.GetVideoFiles();
            System.out.println("JVL - Movies Called GetVideoFiles: " + mediaFiles.size());

            SortDirection sortDir = this.GetSortDirection();

            //Filter to just Movie type media files
            mediaFiles.FilterByMetadata("MediaType", "Movie");
            System.out.println("JVL - Movies Filterred: " + mediaFiles.size());
            Shows shows = mediaFiles.GetShows();
            System.out.println("JVL - Converted to shows: " + shows.size());


            if(sortDir == SortDirection.DESC)
            {
                shows.SortByTitle(true);
            }
            else
            {
                shows.SortByTitle(false);
            }

            return shows;
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Movies ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
}



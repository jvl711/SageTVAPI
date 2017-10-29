
package jvl.sage;

import jvl.sage.api.Configuration;
import jvl.sage.api.Shows;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;


/*
 * Helper class for displaying movies
*/
public class Movies 
{
    public static final String PROPERTY_PREFIX = "jvl.movies";
    public static final String PROPERTY_SORT_DIR = PROPERTY_PREFIX + ".sort.direction";
    private final String context;
    
    public Movies(String context)
    {
        this.context = context;
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
        MediaFiles mediaFiles = MediaFile.GetVideoFiles();
        SortDirection sortDir = this.GetSortDirection();
        
        //Filter to just Movie type media files
        mediaFiles.FilterByMetadata("MediaType", "Movie");
        Shows shows = mediaFiles.GetShows();
        
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
}



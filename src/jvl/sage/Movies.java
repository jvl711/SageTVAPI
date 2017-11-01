
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
    public static final String PROPERTY_SORT_COL = PROPERTY_PREFIX + ".sort.column";
    
    private UIContext context;
    
    public Movies(String context)
    {
        System.out.println("JVL - Movies Constructor Called: " + context);
        this.context = new UIContext(context);
    }
    
    public void SetSortDirection(String sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_DIR, SortDirection.Parse(sort).GetName());
    }
    
    public void SetSortDirection(SortDirection sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_DIR, sort.GetName());
    }
    
    public SortDirection GetSortDirection()
    {
        return SortDirection.Parse(Configuration.GetProperty(context, PROPERTY_SORT_DIR, SortDirection.GetDefault().GetName()));
    }
    
    public void SetSortColumn(String sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_COL, MoviesSortColumn.Parse(sort).GetName());
    }
    
    public void SetSortColumn(MoviesSortColumn sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_COL, sort.GetName());
    }
    
    public MoviesSortColumn GetSortColumn()
    {
        return MoviesSortColumn.Parse(Configuration.GetProperty(context, PROPERTY_SORT_COL, MoviesSortColumn.GetDefault().GetName()));
    }
    
    public Shows GetMovies() throws SageCallApiException
    {
        //System.out.println("JVL - Movies GetMovies Called: " + this.context);
        
        try
        {
            MediaFiles mediaFiles = MediaFile.GetVideoFiles();
            
            //System.out.println("JVL - Movies Called GetVideoFiles: " + mediaFiles.size());

            SortDirection sortDir = this.GetSortDirection();
            MoviesSortColumn sortCol = this.GetSortColumn();
            boolean sortDesc = false;

            //Filter to just Movie type media files
            mediaFiles.FilterByMetadata("MediaType", "Movie");            
            
            Shows shows = mediaFiles.GetShows();

            sortDesc = (sortDir == SortDirection.DESC);
            

            switch (sortCol) 
            {
                case TITLE:
                    shows.SortByTitle(sortDesc);
                    break;
                    
                case DATE_ADDED:
                    shows.SortByDateAdded(sortDesc);
                    break;
                    
                case YEAR_RELEASED:
                    shows.SortByYearReleased(sortDesc);
                    break;
                    
                default:
                    //Just in case
                    shows.SortByTitle();
                    break;
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



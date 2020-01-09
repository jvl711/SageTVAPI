package jvl.sage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvl.logging.Logging;
import jvl.sage.api.Airings;
import jvl.sage.api.Configuration;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.Shows;
import jvl.sage.api.UIContext;

public class Recordings 
{
    public static final String PROPERTY_PREFIX = "jvl.recordings";
    public static final String PROPERTY_SORT_DIR = PROPERTY_PREFIX + ".sort.direction";
    public static final String PROPERTY_SORT_COL = PROPERTY_PREFIX + ".sort.column";
    public static final String PROPERTY_FILTER_CAT = PROPERTY_PREFIX + ".filter.category";
    
    
    private ArrayList<String> categories;
    private HashMap<String, Shows> showsByTitle;
    private UIContext context;
    private Shows shows;
    
    private static final Logger LOG = Logging.getLogger(Recordings.class.getName());
   
    public Recordings(String context)
    {
        System.out.println("JVL - Recordings Constructor Called: " + context);
        LOG.log(Level.INFO, "Recordings Constructor Called");
        this.context = new UIContext(context);
        this.categories = new ArrayList<String>();
        
        try
        {
            this.shows = this.RefreshRecordingsList();
            this.showsByTitle = this.shows.SegmentByTitle();
        }
        catch(Exception ex)
        {
            LOG.log(Level.SEVERE, "There was an error loading the recordings");
        }
    }
    
    
    
    public void SetFilterCategory(String cat)
    {
        Configuration.SetProperty(context, PROPERTY_FILTER_CAT, cat);
    }
    
    public String GetFilterCategory()
    {
        return Configuration.GetProperty(context, PROPERTY_FILTER_CAT, "All");
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
    
    public void SetSortColumn(RecordingsSortColumn sort)
    {
        Configuration.SetProperty(context, PROPERTY_SORT_COL, sort.GetName());
    }
    
    public RecordingsSortColumn GetSortColumn()
    {
        return RecordingsSortColumn.Parse(Configuration.GetProperty(context, PROPERTY_SORT_COL, RecordingsSortColumn.GetDefault().GetName()));
    }
        
    public ArrayList<String> GetCategories()
    {
        return this.categories;
    }
    
    public Shows GetRecordings()
    {
        return shows;
    }
    
    public ArrayList<String> GetShowTitles() throws SageCallApiException
    {
        return shows.GetShowTitles();
    }
    
    public int GetShowTitlesCount() throws SageCallApiException
    {
        return shows.GetShowTitleCount();
    }
    
    public Airings GetAirings(String title) throws SageCallApiException
    {
        return this.showsByTitle.get(title).GetAirings();
    }
    
    public MediaFiles GetMediaFiles(String title) throws SageCallApiException
    {
        return this.showsByTitle.get(title).GetMediaFiles();
    }
    
    public Shows GetShows(String title) throws SageCallApiException
    {
        return this.showsByTitle.get(title);
    }
    
    private Shows RefreshRecordingsList() throws SageCallApiException
    {
        try
        {
            LOG.log(Level.INFO, "GetRecordings called");
            
            LOG.log(Level.FINE, "MediaFile.GetTVFiles() Start");
            MediaFiles mediaFiles = MediaFile.GetTVFiles();
            LOG.log(Level.FINE, "MediaFile.GetTVFiles() End");
            
            SortDirection sortDir = this.GetSortDirection();
            RecordingsSortColumn sortCol = this.GetSortColumn();
            boolean sortDesc = false;

            Shows shows = mediaFiles.GetShows();

            //Fill and cache the categories
            if(categories.size() == 0)
            {
                this.categories = shows.GetCategroies();
                //Hard coded all category
                this.categories.add(0, "All");
            }
            
            if(!this.GetFilterCategory().equalsIgnoreCase("all"))
            {
                LOG.log(Level.FINE, "shows.FilterByCategory Start");
                shows.FilterByCategory(this.GetFilterCategory());
                LOG.log(Level.FINE, "shows.FilterByCategory End");
            }
            
            if(sortDir == SortDirection.DESC)
            {
                sortDesc = true;
            }
            else
            {
                sortDesc = false;
            }
            
            
            switch (sortCol) 
            {
                case TITLE:
                    LOG.log(Level.FINE, "shows.SortByTitle Start");
                    shows.SortByTitle(sortDesc);
                    break;
                    
                case DATE_AIRED:
                    LOG.log(Level.FINE, "shows.SortByDateAdded Start");
                    shows.SortByDateAdded(sortDesc);
                    break;
                    
                default:
                    //Just in case
                    LOG.log(Level.FINE, "shows.SortByTitle Start");
                    shows.SortByTitle();
                    break;
            }
            
            LOG.log(Level.FINE, "shows.SortX End");
            
            LOG.log(Level.INFO, "GetRecordings completed");
            
            return shows;
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Recordings ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
}

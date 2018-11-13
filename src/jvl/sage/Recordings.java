package jvl.sage;

import java.util.ArrayList;
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
    private UIContext context;
   
    public Recordings(String context)
    {
        System.out.println("JVL - Recordings Constructor Called: " + context);
        this.context = new UIContext(context);
        this.categories = new ArrayList<String>();
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
    
    public Shows GetRecordings() throws SageCallApiException
    {
        try
        {
            MediaFiles mediaFiles = MediaFile.GetTVFiles();

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
                shows.FilterByCategory(this.GetFilterCategory());
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
                    shows.SortByTitle(sortDesc);
                    break;
                    
                case DATE_AIRED:
                    shows.SortByDateAdded(sortDesc);
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
            System.out.println("JVL - Recordings ERROR: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        return null;
    }
}

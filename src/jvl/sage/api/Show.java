package jvl.sage.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;
import jvl.logging.Logging;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;
import jvl.metadata.Metadata;
import jvl.metadata.Watched;
import jvl.tmdb.RateLimitException;
import jvl.tmdb.model.Cast;
import jvl.tmdb.model.Crew;
import jvl.tmdb.model.Image;


public class Show extends SageObject
{
    public static final double MINIMUM_POSTER_RATIO = 0.64;
    public static final double MAXIMUM_POSTER_RATIO = 0.68;

    //private Object show;
    private Object mediafile;
    private Object airing;
    private Object lookupObject;
    
    private Metadata meta;
    
    
    
    private static final Logger LOG = Logging.getLogger(Show.class.getName());
    
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
        String apikey = "";
        
        if(MediaFile.IsMediaFileObject(input))
        {
            this.mediafile = input;
            this.airing = MediaFile.GetAiring(mediafile);
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
        
        try
        {
            apikey = Configuration.GetServerProperty("jvl.metadataplugin.apikey", "");
        }
        catch(Exception ex)
        {
            LOG.severe("Error getting the APIKey for TheMovieDB.  Metadata lookups will fail without a valid key.");
        }
        
        meta = new Metadata(this, apikey);
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
    
    public String GetExternalID() throws SageCallApiException
    {
        return callApiString("GetShowExternalID", Airing.GetShowForAiring(airing));
    }
    
    public void SaveTVMetadata(int tmbdID, int seasonNumber, int episodeNumber, boolean blocking) throws IOException, SageCallApiException, RateLimitException
    {
        meta.SaveTVMetadata(tmbdID, seasonNumber, episodeNumber, blocking, blocking);
    }
    
    public void SaveMovieMetadata(int tmdbID, boolean blocking) throws SageCallApiException, IOException, FileNotFoundException, RateLimitException
    {
        meta.SaveMovieMetadata(tmdbID, blocking, blocking);
    }
    
    public boolean MetadataLookup(boolean forceRefresh, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.meta.LookupMetadata(forceRefresh, blocking);
    }
    
    public boolean MetadataLookup()
    {
        System.out.println("JVL - Show.LookupMetaData called");
        
        try 
        {
            return this.meta.LookupMetadata(true, false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL - Show.MetadataLookup exception thrown: " + ex.getMessage());
            return false;
        } 
    }
    
    public String GetCastString()
    {
        ArrayList<Cast> value;
        String cast = "";
        
        LOG.log(Level.INFO, "GetCastString called");
        
        try
        {
            if(this.meta.HasMetadata() && this.IsMovie())
            {
                value = this.meta.GetMovieCredits().getCast();   
            }
            else
            {
               value = this.meta.GetShowCredits().getCast();
            }
            
            for(int i = 0; i < 3 && i < value.size(); i++)
            {
                cast += value.get(i).getName() + " (" + value.get(i).getCharacter() + "), ";
            }

            if(cast.length() > 0)
            {
                cast = cast.substring(0, cast.length() -2);
            }
        }
        catch(Exception ex)
        {
            LOG.log(Level.WARNING, "GetCastString exception: " + ex.getMessage(), ex);
        }
        
        return cast;
    }
    
    public String GetDirectorString()
    {
        ArrayList<Crew> value;
        String directors = "";
        
        try
        {
            if(this.meta.HasMetadata() && this.IsMovie())
            {
                value = this.meta.GetMovieCredits().getCrew();
                
                for(int i = 0; i < value.size(); i++)
                {
                    if(value.get(i).getJob().equalsIgnoreCase("director"))
                    {
                        directors = value.get(i).getName() + ", ";
                    }
                }
                
                if(directors.length() > 0)
                {
                    directors = directors.substring(0, directors.length() -2);
                }
                
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
        return directors;
    }
    
    public String GetParentalRating()
    {
        String value = "";
        
        try
        {
            if(this.meta.HasMetadata() && this.IsMovie())
            {
                value = this.meta.GetMovieReleases().getParentalRating();
                
                System.out.println("JVL - getParentalRating: " + value);
            }
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Error GetParentalRating");
            ex.printStackTrace();
        }
        
        return value;
    }
    
    public String GetTagline()
    {
        String value = "";
        
        try
        {
            if(this.meta.HasMetadata() && this.IsMovie())
            {
                value = this.meta.GetMovieDetails().getTagline();
            }
        }
        catch(Exception ex){ }
        
        return value;
    }
    
    public double GetShowVoteScore()
    {
        double value = 0;
        
        try
        {
            if(this.meta.HasMetadata() && !this.IsMovie())
            {
                value = this.meta.GetShowDetails().getVoteAverage();
            }
        }
        catch(Exception ex){ }
        
        return value;
    }
    
    /***
     * Returns the Watched object which stores watched data in metadata folder.
     * This allows the data to persist outside of SageTV wiz.bin.
     * @return Watched object or null if the object does not have Metadata
     * @throws SageCallApiException 
     */
    public Watched GetWatchedDetails() throws SageCallApiException, IOException
    {
        if(this.meta.HasMetadata())
        {
            return meta.GetWatchedDetails();
        }
        else
        {
            return null;
        }
    }
    
    public double GetVoteScore()
    {
        double value = 0;
        try
        {
            if(this.meta.HasMetadata())
            {
                if(this.IsMovie())
                {
                    meta.GetMovieDetails().getVoteAverage();
                }
                else
                {
                    meta.getEpisodeDetails().getVoteAverage();
                }
            }
        }
        catch(Exception ex) { }
        
        return value;
    }
    
    public int GetSeasonNumber() throws SageCallApiException 
    {
        int response = 0;
        
        response = callApiInt("GetShowSeasonNumber", this.lookupObject);
        
        return response;
    }
    
    public void SetSeasonNumber(int value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("SeasonNumber", value + "");
    }
    
    public int GetEpisodeNumber() throws SageCallApiException
    {
        int response = 0;
        
        response = callApiInt("GetShowEpisodeNumber", this.lookupObject);
        
        return response;
    }
    
    public void SetEpisodeNumber(int value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("EpisodeNumber", value + "");
    }
    
    public String GetEpisodeNumberString() throws SageCallApiException
    {
        int episodeNumber = this.GetEpisodeNumber();
        
        return "Episode " + episodeNumber;
    }
    
    public String GetSeasonNumberString() throws SageCallApiException
    {
        int seasonNumber = this.GetSeasonNumber();
        
        return "Season " + seasonNumber;
    }

    public String GetSeasonEpisodeString() throws SageCallApiException
    {
       return GetSeasonEpisodeString(0);
    }
    
    /**
     * Formats the Season Episode info into a formated string
     * 
     * Format 0: Season 1 Episode 1
     * Format 1: S01E01
     * 
     * @param format a number of the format
     * @return A formated string with Season Episode information
     * @throws SageCallApiException 
     */
    public String GetSeasonEpisodeString(int format) throws SageCallApiException
    {
       switch(format)
       {
           case 0:
               return this.GetSeasonNumberString() + " " + this.GetEpisodeNumberString();

           case 1:
               int season = this.GetSeasonNumber();
               int episode = this.GetEpisodeNumber();
               String seasonString;
               String episodeString;
               
               if(season < 10)
               {
                   seasonString = "0" + season;
               }
               else
               {
                   seasonString = season + "";
               }
               
               if(episode < 10)
               {
                   episodeString = "0" + episode;
               }
               else
               {
                    episodeString = episode + "";
               }
               
               return "S" + seasonString + "E" + episodeString;
               
           default:
               return this.GetSeasonNumberString() + " " + this.GetEpisodeNumberString();
               
               
       }
    }
    
    public String GetEpisodeName() throws SageCallApiException
    {
        return callApiString("GetShowEpisode", this.lookupObject).trim();
    }
    
    public void SetEpisodeName(String value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("EpisodeName", value);
    }
    
    public String GetTitle() throws SageCallApiException
    {
        return callApiString("GetShowTitle", this.lookupObject).trim();
    }
    
    public void SetTitle(String value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("Title", value);
    }
    
    public String GetDescription() throws SageCallApiException
    {
        return callApiString("GetShowDescription", this.lookupObject).trim();
    }
    
    public void SetDescription(String value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("Description", value);
    }
    
    public String GetYear() throws SageCallApiException
    {
        return callApiString("GetShowYear", this.lookupObject);
    }
 
    public void SetYear(String value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("Year", value);
    }
    
    public String GetCategoriesString() throws SageCallApiException
    {
        return this.GetCategoriesString(" / ");
    }
    
    public String GetCategoriesString(String Delimiter) throws SageCallApiException
    {
        return Show.callApiString("GetShowCategoriesString", this.lookupObject, Delimiter);
    }
    
    public void SetCategories(String [] values) throws SageCallApiException
    {
        String value = "";
        
        for(int i = 0; i < values.length; i++)
        {
            value += values[i] + ";";
        }
        
        if(value.length() > 0)
        {
            value = value.substring(0, value.length() - 1);
        }
        
        this.GetMediaFile().SetMetadata("Genre", value);
    }
    
    public String [] GetCategories() throws SageCallApiException
    {
        return (String []) Show.callApiArray("GetShowCategoriesList", this.lookupObject);
    }
    
    public boolean IsMovie() throws SageCallApiException
    {
        LOG.log(Level.INFO, "Called IsMovie");
        
        
        if(this.meta.HasMetadata())
        {
            return this.GetMediaType().equalsIgnoreCase("movie");
        }
        else
        {
            return Show.callAPIBoolean("IsMovie", Airing.GetShowForAiring(airing));
            
            //return this.callAPIBoolean("IsMovie");
        }
    }
    
    /**
     * Attempts to use metadata to determine if this is a tv show.  If not
     * it tries to see if Sage thinks it is not a movie.
     * @return True if it is marked as a TV show
     * @throws SageCallApiException 
     */
    public boolean IsTV() throws SageCallApiException
    {
        if(this.GetMediaFile() != null && this.meta.HasMetadata())
        {
            return this.GetMediaType().equalsIgnoreCase("tv");
        }
        else
        {
            return !Show.callAPIBoolean("IsMovie", Airing.GetShowForAiring(airing));
        }
    }
                
    public char GetTitleSearchChar() throws SageCallApiException
    {
        String title = this.GetSortableTitle();
        
        if(title.length() > 0)
        {
            return this.GetSortableTitle().charAt(0);
        }
        else
        {
            return ' ';
        }
    }
    
    public String GetSortableTitle() throws SageCallApiException
    {
        String title = this.GetTitle();
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

    public String GetEpisodeStill() throws SageCallApiException, IOException
    {
        return this.meta.GetEpisodeStill();
    }
    
    public String GetSeasonPoster() throws SageCallApiException, IOException, RateLimitException
    {
        if(this.mediafile == null)
        {
            return this.meta.GetPosterRealtime();
        }
        else if(this.meta.HasMetadata())
        {
            String seasonPoster = this.meta.GetSeasonPoster();
            
            if(seasonPoster.equalsIgnoreCase(""))
            {
                //If there is no poster, than attempt to get show poster
                return this.GetPoster();
            }
            
            return seasonPoster;
        }

        return "";
    }
    
    public String GetPoster() throws SageCallApiException, IOException, RateLimitException
    {
        LOG.log(Level.INFO, "GetPoster Called");
        
        if(this.mediafile == null)
        {
            LOG.log(Level.FINE, "MediaFile null, calling Metasata.GetPosterRealtime");
            return this.meta.GetPosterRealtime();
        }
        else if(this.meta.HasMetadata())
        {
            LOG.log(Level.FINE, "MediaFile is not null, calling Metadata.GetPoster");
            return this.meta.GetPoster();
        }
        
        return "";
    }
    
    public String GetPoster(int Size) throws SageCallApiException, IOException, RateLimitException
    {
        if(this.mediafile == null)
        {
            return this.meta.GetPosterRealtime(Size, true);
        }
        else if(this.meta.HasMetadata())
        {
            return this.meta.GetPoster(Size, true);
        }
        
        return "";
    }
    
    public String GetBackground() throws SageCallApiException, IOException
    {
        
        if(this.mediafile == null)
        {
            return this.meta.GetBackdropRealtime();
        }
        else if(this.meta.HasMetadata())
        {
            return this.meta.GetBackdrop();
        }
        
        return "";
    }
    
    public void SetPoster(Image poster)
    {
        meta.SetPoster(poster);
    }
    
    public void SetSeasonPoster(Image poster) throws SageCallApiException
    {
        meta.SetSeasonPoster(poster);
    }
    
    public void SetBackground(Image background)
    {
        meta.SetBackdrop(background);
    }
    
    public ArrayList<Image> GetPosterImages()
    {
        return this.meta.GetPosterImages();
    }
    
    public ArrayList<Image> GetSeasonPosterImages()
    {
        return this.meta.GetSeasonPosterImages();
    }
    
    public ArrayList<Image> GetBackgroundImages()
    {
        return this.meta.GetBackdropImages();
    }
    
    
    /**
     * The type of media file this show object represents.
     * 
     * TV - TV Episode
     * Movie - Movie
     * Music - File without video
     * Unknown - Unable to identify.  These may need manual assignment
     * 
     * @return The type of media this object represents
     * @throws SageCallApiException 
     */
    public String GetMediaType() throws SageCallApiException
    {
        return this.GetMediaFile().GetMetadata("MediaType");
    }
    
    /**
     * Sets the type of media this object represents
     * 
     * TV - TV Episode
     * Movie - Movie
     * Music - File without video
     * Unknown - Unable to identify.  These may need manual assignment
     * 
     * @param value MediaType from one of the valid options above
     * @throws SageCallApiException 
     */
    public void SetMediaType(String value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("MediaType", value);
    }
    
    
    
    /**
     * Gets the TMDB ID if it is set in the database.  If it is not set, or there
     * is an error parsing the value it will return -1
     * @return TMDB ID
     */
    public int GetTheMovieDBID() throws SageCallApiException
    {
        LOG.log(Level.INFO, "Called GetTheMovieDBID");
        
        if(this.mediafile == null)
        {
            LOG.log(Level.INFO, "MediaFile is null.  Exiting GetTheMovieDBID");
            return -1;
        }
        
        try
        {
            String temp_id = this.GetMediaFile().GetMetadata("metadata.tmdb.id");
            
            return Integer.parseInt(temp_id);
        }
        catch(Exception ex)
        {
            if(this.GetMediaFile().IsTVFile())
            {
                return -2;
            }
            
            return -1;
        }
    }
    
    public void SetTheMovieDBID(int value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("metadata.tmdb.id", value + "");
    }
    
    public long GetMetadataUpdateDate() throws SageCallApiException
    {
        String temp;
        long value = 0;
        
        temp = this.GetMediaFile().GetMetadata("metadata.lastupdated");
        
        try { value = Long.parseLong(temp); } catch(Exception ex) {}
        
        return value;
    }
    
    public void SetMetadataUpdateDate(long value) throws SageCallApiException
    {
        this.GetMediaFile().SetMetadata("metadata.lastupdated", value + "");
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
            return t.GetSortableTitle().toUpperCase().compareTo(t1.GetSortableTitle().toUpperCase());
            
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
            
            try{ first = Integer.parseInt(t.GetYear()); } catch(Exception ex) { }
            try{ second = Integer.parseInt(t1.GetYear()); } catch(Exception ex) { }
            
            
            //if they are the same than use title
            if(first == second)
            {
                return t.GetSortableTitle().toUpperCase().compareTo(t1.GetSortableTitle().toUpperCase());
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
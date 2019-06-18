
package jvl.metadata;

import java.io.IOException;
import jvl.sage.SageCallApiException;
import jvl.sage.api.Show;
import jvl.tmdb.SearchAPI;
import jvl.tmdb.TMDBRequest;
import jvl.tmdb.model.Movie;
import jvl.tmdb.model.SearchResults;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import jvl.tmdb.ConfigAPI;
import jvl.tmdb.MovieAPI;
import jvl.tmdb.RateLimitException;
import jvl.tmdb.TVAPI;
import jvl.tmdb.model.Episode;
import jvl.tmdb.model.Image;
import jvl.tmdb.model.Images;
import jvl.tmdb.model.Season;
import jvl.tmdb.model.TV;

/*
 *  Directory Structure
 *      Root ->
 *              movies ->
 *                      [TMDB ID] ->
 *                                  details.json
 *                                  images.json
 *                                  posters ->
 *                                              size ->
 *                                                      name.jpg
 *                                  backdrops ->
 *                                              size ->
 *                                                      name.jpg
 *
 *      Root ->
 *              TV ->
 *                      {TMDB ID} ->
 *                                  details.json
 *
 *                                  images.json
 *                                  posters (Show) ->
 *                                                  size ->
                                                            name.jpg
 *                                  backdrops (Show) ->
 *                                                  size ->
                                                            name.jpg
 *                                  season_{n} ->
 *                                                  season.json
 *                                                  posters ->
 *                                                              size ->
                                                                        name.jpg
 *                                                  episode_{n} ->
                                                                    stills
            *                                                              size ->
 *                                                                                  name.jpg
 *
*/



public class Metadata 
{
    //private String cacheFolder;
    private File cacheFolder;
    private Show show;
    private TMDBRequest request;
    
    /*Basic cache for lookup on title*/
    private static Map<String, Integer> TMDBIDCacheMovie = new ConcurrentHashMap<String, Integer>();
    private static Map<String, Integer> TMDBIDCacheTV = new ConcurrentHashMap<String, Integer>();
    
    private static final int DEFAULT_POSTER_SIZE_WIDTH = 600;
    private static final int DEFAULT_STILL_SIZE_WIDTH = 600;
    private static final int DEFAULT_BACKDROP_SIZE_WIDTH = 3840;
    
    
    public Metadata(Show show)
    {
        this("metacache", show);
    }
    
    public Metadata(String cacheFolder, Show show)
    {
        this.cacheFolder = new File(cacheFolder);
        this.show = show;
        this.request = new TMDBRequest();
        
        try
        {
            if(!this.cacheFolder.exists())
            {
                this.cacheFolder.mkdir();
            }
        }
        catch(Exception ex) 
        { 
            throw new RuntimeException("Unable to create cache directory for metadata");
        }
        
        if(!this.cacheFolder.canRead())
        {
            throw new RuntimeException("Unable to read from the cache directory for metadata");
        }
        
        if(!this.cacheFolder.canWrite())
        {
            throw new RuntimeException("Unable to write from cache directory for metadata");
        }
    }
    
    
    public boolean LookupMetadata(boolean forceRefresh, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        //TODO: Handle force refresh
        System.out.println("JVL Metadata - LookupMetaData was called");
        
        if(!this.HasMetadata() || forceRefresh)
        {
            if(this.show.GetMediaFile().IsTVFile() && !this.show.IsMovie()) //Recorded TV
            {
                System.out.println("JVL - This is recorded content that is not a movie");
                int tmdb_id = -1;
                
                if(this.HasCachedTVTMDB_ID(show.GetTitle()))
                {
                    System.out.println("JVL - Metadata cache hit on title for TV Show");
                    tmdb_id = this.GetCachedTVTMDB_ID(show.GetTitle());
                }
                else
                {
                    SearchResults results = SearchAPI.searchTV(this.request, show.GetTitle(), blocking);
                    
                    if(results != null && results.getShows().size() > 0)
                    {
                        tmdb_id = results.getShows().get(0).getTmdbID();
                        this.PutCacheTVTMDB_ID(show.GetTitle(), tmdb_id);
                    }
                }

                if(tmdb_id > 0)
                {
                    if(show.GetEpisodeNumber() > 0)
                    {
                        this.SaveTVMetadata(tmdb_id, this.show.GetSeasonNumber(), this.show.GetEpisodeNumber(), forceRefresh, blocking);
                    }
                    else
                    {
                        //This is for shows that do not have season episode numbers
                        this.SaveTVMetadata(tmdb_id, blocking, forceRefresh);
                    }
                }
                else
                {
                    //TODO:  Think about logging this to the system message interface
                    System.out.println("JVL - TMDB there was no hit finding this show.");
                    System.out.println("Show title: " + this.show.GetTitle());
                    System.out.println("Going to set MediaType so it is at least available");
                    this.show.SetMediaType("TV");
                    this.show.SetTheMovieDBID(-2);

                    return false;
                }

                return false;
            }
            else if(this.show.GetMediaFile().IsTVFile() && this.show.IsMovie()) //Recored Movie
            {
                SearchResults results;
                int year = 0;
                int tmdb_id = -1;
                try{ year = Integer.parseInt(this.show.GetYear()); } catch(Exception ex) { }                
                
                if(year > 0)
                {
                    if(this.HasCachedMovieTMDB_ID(this.show.GetTitle(), year))
                    {
                        System.out.println("JVL - Metadata cache hit getting movie id");
                        tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle(), year);
                    }
                    else
                    {
                        results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);
                        
                        if(results != null && results.getMovies().size() > 0)
                        {
                            tmdb_id = results.getMovies().get(0).getTmdbID();
                            this.PutCacheMovieTMDB_ID(this.show.GetTitle(), year, tmdb_id);
                        }
                    }
                }
                else
                {
                    if(this.HasCachedMovieTMDB_ID(this.show.GetTitle()))
                    {
                        System.out.println("JVL - Metadata cache hit getting movie id");
                        tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle());
                    }
                    else
                    {
                        results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);
                        
                        if(results != null && results.getMovies().size() > 0)
                        {
                            tmdb_id = results.getMovies().get(0).getTmdbID();
                            this.PutCacheMovieTMDB_ID(this.show.GetTitle(), tmdb_id);
                        }
                    }
                }
                
                if(tmdb_id > 0)
                {
                    this.SaveMovieMetadata(tmdb_id, forceRefresh, blocking);
                }
                else
                {
                    //TODO:  Think about logging this to the system message interface
                    System.out.println("JVL - TMDB there was no hit finding this movie.");
                    System.out.println("Movie title: " + this.show.GetTitle());
                    System.out.println("Going to set MediaType so it is at least available");
                    this.show.SetMediaType("Movie");
                    this.show.SetTheMovieDBID(-2);
                    
                    return false;
                }

            }
            else //This is non-recorded content
            {
                FileNameParser parser = new FileNameParser(this.show.GetMediaFile().GetFileName());
                System.out.println("JVL - Filename: " + this.show.GetMediaFile().GetFileName());
                
                SearchResults results;
                int tmdb_id = -1;
                
                
                if(parser.IsMovie())
                {
                    System.out.println("JVL - Parser IsMovie true");
                    
                    if(parser.GetReleaseYear() > 0)
                    {
                        //System.out.println("JVL - Parser Title: " + parser.GetTitle());
                        //System.out.println("JVL - Parser Release Year: " + parser.GetReleaseYear());
                        
                        if(this.HasCachedMovieTMDB_ID(parser.GetTitle(), parser.GetReleaseYear()))
                        {
                            System.out.println("JVL - Metadata cache hit getting movie id");
                            tmdb_id = this.GetCachedMovieTMDB_ID(parser.GetTitle(), parser.GetReleaseYear());
                        }
                        else
                        {
                            results = SearchAPI.searchMovies(this.request, parser.GetTitle(), parser.GetReleaseYear(), blocking);

                            if(results != null && results.getMovies().size() > 0)
                            {
                                tmdb_id = results.getMovies().get(0).getTmdbID();
                                this.PutCacheMovieTMDB_ID(parser.GetTitle(), parser.GetReleaseYear(), tmdb_id);
                            }
                        }
                    }
                    else
                    {
                        if(this.HasCachedMovieTMDB_ID(parser.GetTitle()))
                        {
                            System.out.println("JVL - Metadata cache hit getting movie id");
                            tmdb_id = this.GetCachedMovieTMDB_ID(parser.GetTitle());
                        }
                        else
                        {
                            results = SearchAPI.searchMovies(this.request, parser.GetTitle(), blocking);

                            if(results != null && results.getMovies().size() > 0)
                            {
                                tmdb_id = results.getMovies().get(0).getTmdbID();
                                this.PutCacheMovieTMDB_ID(parser.GetTitle(), tmdb_id);
                            }
                        }
                    }
                    
                    if(tmdb_id > 0)
                    {
                        this.SaveMovieMetadata(tmdb_id, forceRefresh, blocking);
                    }
                    else
                    {
                        //TODO:  Think about logging this to the system message interface
                        System.out.println("JVL - TMDB there was no hit finding this movie.");
                        System.out.println("Movie title: " + parser.GetTitle());
                        
                        return false;
                    }
                }
                else
                {
                    System.out.println("JVL - Parser found IsMovie is false");
                    
                    if(this.HasCachedTVTMDB_ID(parser.GetTitle()))
                    {
                        tmdb_id = this.GetCachedTVTMDB_ID(parser.GetTitle());
                        System.out.println("JVL - Metadata cache hit getting tv id");
                    }
                    else
                    {
                        results = SearchAPI.searchTV(request, parser.GetTitle(), blocking);
                        
                        if(results != null && results.getShows().size() > 0)
                        {
                            tmdb_id = results.getShows().get(0).getTmdbID();
                            this.PutCacheTVTMDB_ID(parser.GetTitle(), tmdb_id);
                        }
                    }
                    
                    if(tmdb_id > 0)
                    {
                        this.SaveTVMetadata(tmdb_id, parser.GetSeason(), parser.GetEpisode(), forceRefresh, blocking);
                    }
                    else
                    {
                        //TODO:  Think about logging this to the system message interface
                        System.out.println("JVL - TMDB there was no hit finding this show.");
                        System.out.println("Show title: " + parser.GetTitle());
                        
                        return false;
                    }
                }
            }  
        }
        
        return true;
    }
    
    private void PutCacheMovieTMDB_ID(String title, int year, int tmdb_id)
    {
        if(!Metadata.TMDBIDCacheMovie.containsKey(title + "_" + year))
        {
            Metadata.TMDBIDCacheMovie.put(title + "_" + year, tmdb_id);
        }
    }
    
    private void PutCacheMovieTMDB_ID(String title, int tmdb_id)
    {
        if(!Metadata.TMDBIDCacheMovie.containsKey(title))
        {
            Metadata.TMDBIDCacheMovie.put(title, tmdb_id);
        }
    }
    
    private boolean HasCachedMovieTMDB_ID(String title)
    {
        return Metadata.TMDBIDCacheMovie.containsKey(title);
    }
    
    private boolean HasCachedMovieTMDB_ID(String title, int year)
    {
        return Metadata.TMDBIDCacheMovie.containsKey(title + "_" + year);
    }
    private int GetCachedMovieTMDB_ID(String title, int year)
    {
        return Metadata.TMDBIDCacheMovie.get(title + "_" + year);
    }
    
    private int GetCachedMovieTMDB_ID(String title)
    {
        return Metadata.TMDBIDCacheMovie.get(title);
    }
    
    private void PutCacheTVTMDB_ID(String title, int tmdb_id)
    {
        if(!Metadata.TMDBIDCacheTV.containsKey(title))
        {
            Metadata.TMDBIDCacheTV.put(title, tmdb_id);
        }
    }
    
    private boolean HasCachedTVTMDB_ID(String title)
    {
        return Metadata.TMDBIDCacheTV.containsKey(title);
    }
    
    private int GetCachedTVTMDB_ID(String title)
    {
        return Metadata.TMDBIDCacheTV.get(title);
    }
    
    public void SaveTVMetadata(int tmdb_id, int seasonNumber, int episodeNumber, boolean forceRefresh, boolean blocking) throws IOException, SageCallApiException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/images.json");
        File seasonFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/season_" + seasonNumber + "/season.json");
        File seasonImagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/season_" + seasonNumber + "/images.json");
        File episodeImagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/season_" + seasonNumber + "/episode_" + episodeNumber + "/images.json");
        
        boolean updateCache = forceRefresh;
        
        //Check to see if we need to update files.  If we already have the season episode info
        //then skip.  If we are missing anything than update it all.
        if(!detailsFile.exists()) { updateCache = true; }
        
        if(!imagesFile.exists()) { updateCache = true; }
    
        if(!seasonFile.exists())
        {
            updateCache = true;
        }
        else
        {
            TV tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request, blocking));
            
            if(!tv.hasSeason(seasonNumber))
            {
                updateCache = true;
            }
            else
            {
                Season season = Season.parseFile(seasonFile, ConfigAPI.getConfig(request, blocking));

                if(!season.hasEpisode(episodeNumber))
                {
                    updateCache = true;
                }
            }
        }
                
        Date now = new Date();

        TV tv = null;
        Season season = null;
        Episode episode = null;
            
        if(updateCache)
        {
            tv = TVAPI.getDetails(this.request, tmdb_id, blocking);
            
            if(tv == null)
            {
                return;
            }
            
            try
            {
                season = TVAPI.getSeasonDetails(this.request, tmdb_id, seasonNumber, blocking);

                if(season != null)
                {
                    episode = season.getEpisode(episodeNumber);
                }
            }
            catch(Exception ex)
            {
                //Season info is not always up to date.  Allow to fail and still continue to update
            }
            
        }
        else
        {
            tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request, blocking));
            season = Season.parseFile(seasonFile, ConfigAPI.getConfig(request, blocking));
            episode = season.getEpisode(episodeNumber);
        }

        
        
        show.SetTheMovieDBID(tmdb_id);
        show.SetEpisodeNumber(episodeNumber);
        show.SetSeasonNumber(seasonNumber);
        show.SetMetadataUpdateDate(now.getTime());
        show.SetMediaType("TV");
        
        if(tv != null)
        {    
            show.SetTitle(tv.getName());
            show.SetCategories(tv.getGenres());
        }
        
        if(episode != null)
        {
            show.SetEpisodeName(episode.getName());
            show.SetDescription(episode.getOverview());
        }

        

        if(updateCache)
        {
            Images images = TVAPI.getImages(this.request, tmdb_id, blocking);
            Images seasonImages = TVAPI.getSeasonImages(request, tmdb_id, seasonNumber, blocking);
            Images episodeImages = TVAPI.getEpisodeImages(request, tmdb_id, seasonNumber, episodeNumber, blocking);

            if(tv != null)
            {
                tv.save(detailsFile);
            }
            
            if(season != null)
            {
                season.save(seasonFile);
            }

            if(images.getPosters().size() > 0 || images.getBackdrops().size() > 0)
            {
                images.save(imagesFile);
            }
            
            if(episodeImages.getPosters().size() > 0)
            {
                episodeImages.save(episodeImagesFile);
            }
            
            if(seasonImages.getPosters().size() > 0)
            {
                seasonImages.save(seasonImagesFile);
            }
            
        }
        
        
        this.GetPoster(blocking);
        this.GetBackdrop(blocking);
        this.GetSeasonPoster(blocking);
        this.GetEpisodeStill(blocking);
    }
    
    /**
     * This downloads data associated with the show into the cache directory.  It stores the TheMovieDBID
     * and some of the metadata back to the Sage object.  This call is for episodes that 
     * do not have a season episode number.
     * 
     * @param result Search result hit from TheMovieDB
     * @throws IOException
     * @throws SageCallApiException 
     */
    private void SaveTVMetadata(int tmdb_id, boolean forceRefresh, boolean blocking) throws IOException, SageCallApiException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/images.json");
        
        boolean updateCache = forceRefresh;
        
        //Check to see if we need to update files. If we are missing anything than update it all.
        if(!detailsFile.exists()) { updateCache = true; }
        
        if(!imagesFile.exists()) { updateCache = true; }
    
        Date now = new Date();
        TV tv;
            
        if(updateCache)
        {
            tv = TVAPI.getDetails(this.request, tmdb_id, blocking);
        }
        else
        {
            tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request, blocking));
        }

        show.SetTheMovieDBID(tmdb_id);
        show.SetMetadataUpdateDate(now.getTime());
        show.SetMediaType("TV");
        
        if(tv != null)
        {
            show.SetTitle(tv.getName());
            show.SetCategories(tv.getGenres());
        }

        if(updateCache)
        {
            Images images = TVAPI.getImages(this.request, tmdb_id, blocking);

            if(tv != null)
            {
                tv.save(detailsFile);
            }
            
            if(images.getPosters().size() > 0 || images.getBackdrops().size() > 0)
            {
                images.save(imagesFile);
            }
            
        }
  
        this.GetPoster(blocking);
        this.GetBackdrop(blocking);
    }
    
    public void SaveMovieMetadata(int tmdb_id, boolean forceRefresh, boolean blocking) throws SageCallApiException, FileNotFoundException, IOException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + tmdb_id + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + tmdb_id + "/images.json");
        
        Date now = new Date();
        Movie movie = MovieAPI.getDetails(request, tmdb_id, blocking);
        Images images = MovieAPI.getImages(request, tmdb_id, blocking);
        
        //TODO: Implement selective save
        
        show.SetTheMovieDBID(tmdb_id);
        show.SetMediaType("Movie");
        
        if(movie != null)
        {
            show.SetTitle(movie.getTitle());
            show.SetDescription(movie.getOverview());
            show.SetCategories(movie.getGenres());
            show.SetYear(movie.getReleaseYear());
            show.SetMetadataUpdateDate(now.getTime());
        }
        
        if(movie != null)
        {
            movie.save(detailsFile);
        }
        
        if(images.getPosters().size() > 0 || images.getBackdrops().size() > 0)
        {
            images.save(imagesFile);
        }
        
        this.GetPoster(blocking);
        this.GetBackdrop(blocking);
    }

    public Properties GetMetadataOverrides()
    {
        Properties overrides = new Properties();
        File overridesFile;
        
        InputStream input = null;
        
        try
        {
            if(this.HasMetadata())
            {
                if(this.show.GetMediaType().equalsIgnoreCase("TV"))
                {
                    overridesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/overrides.properties");

                    if(overridesFile.exists())
                    {
                        input = new FileInputStream(overridesFile);
                        overrides.load(input);
                    }
                }
                else if(this.show.GetMediaType().equalsIgnoreCase("Movie"))
                {
                    overridesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/overrides.properties");
                    
                    if(overridesFile.exists())
                    {
                        input = new FileInputStream(overridesFile);
                        overrides.load(input);
                    }
                }
            }
        }
        catch(Exception ex)
        {
            
        }
        
        return overrides;
    }
    
    public void SaveMetadataOverrides(Properties overrides)
    {
        
        File overridesFile;
        OutputStream output = null;
        
        try
        {
            if(this.HasMetadata())
            {
                if(this.show.GetMediaType().equalsIgnoreCase("TV"))
                {
                    System.out.println("JVL Metadata - Override Path: " + this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/overrides.properties");
                    overridesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/overrides.properties");
                                                
                    output = new FileOutputStream(overridesFile);
                    overrides.store(output, "");
                }
                else if(this.show.GetMediaType().equalsIgnoreCase("Movie"))
                {
                    
                    System.out.println("JVL Metadata - Override Path: " + this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/overrides.properties");
                    overridesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/overrides.properties");
        
                    output = new FileOutputStream(overridesFile);
                    overrides.store(output, "");
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("JVL Metadata - Exception writing overrides.properties: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Determines if there was a successful metadata lookup, and there is and MovieDB ID set 
     * @return True if lookup was successful
     * @throws SageCallApiException 
     */
    public boolean HasMetadata() throws SageCallApiException
    {
        return this.show.GetTheMovieDBID() > -1;
    }
    
    public Movie GetMovieDetails() throws IOException, RateLimitException, SageCallApiException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/detials.json");
        Movie movie = null;
        
        if(!detailsFile.exists())
        {
            movie = MovieAPI.getDetails(this.request, this.show.GetTheMovieDBID(), false);
        }
        
        return movie;
    }
    
    public String GetPosterRealtime()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetPosterRealtime(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetPosterRealtime: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    public String GetPosterRealtime(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetPosterRealtime(DEFAULT_POSTER_SIZE_WIDTH, blocking);
    }
    
    public String GetPosterRealtime(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        int tmdb_id = -1;
        String MediaType;
        SearchResults results;
        File file = null;
        
        System.out.println("JVL - Realtime poster lookup called");
        
        if(this.show.IsMovie())
        {
            System.out.println("JVL - Looking up movie");
            int year = -1;    
            MediaType = "Movie";
        
            try { year = Integer.parseInt(this.show.GetYear()); } catch (Exception ex) { } 
            
            if(year > 0)
            {
                if(this.HasCachedMovieTMDB_ID(this.show.GetTitle(), year))
                {
                    System.out.println("JVL - Metadata cache hit getting movie id");
                    tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle(), year);
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);

                    if(results != null && results.getMovies().size() > 0)
                    {
                        tmdb_id = results.getMovies().get(0).getTmdbID();
                        this.PutCacheMovieTMDB_ID(this.show.GetTitle(), year, tmdb_id);
                    }
                }
            }
            else
            {
                if(this.HasCachedMovieTMDB_ID(this.show.GetTitle()))
                {
                    System.out.println("JVL - Metadata cache hit getting movie id");
                    tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle());
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);

                    if(results != null && results.getMovies().size() > 0)
                    {
                        tmdb_id = results.getMovies().get(0).getTmdbID();
                        this.PutCacheMovieTMDB_ID(this.show.GetTitle(), tmdb_id);
                    }
                }
            }

        }
        else
        {
            System.out.println("JVL - Looking up TV");
            MediaType = "TV";
            
            if(this.HasCachedTVTMDB_ID(show.GetTitle()))
            {
                System.out.println("JVL - Metadata cache hit on title for TV Show");
                tmdb_id = this.GetCachedTVTMDB_ID(show.GetTitle());
            }
            else
            {
                System.out.println("JVL - Metadata cache miss on TV Show: " + show.GetTitle());
                results = SearchAPI.searchTV(this.request, show.GetTitle(), blocking);

                if(results != null && results.getShows().size() > 0)
                {
                    tmdb_id = results.getShows().get(0).getTmdbID();
                    this.PutCacheTVTMDB_ID(show.GetTitle(), tmdb_id);
                    System.out.println("JVL - Metadata adding tp cache TMDB: " + tmdb_id);
                }
            }
        }
        
        if(tmdb_id > 0)
        {
            Images images = this.GetImages(tmdb_id, MediaType, blocking);
            
            if(images.getPosters().size() > 0)
            {
                System.out.println("JVL - Image found");
                String poster_width = images.getPoster().getValidSize(preferredSize);

                if(MediaType.equalsIgnoreCase("TV"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/posters/" + poster_width + images.getPoster().getFileName());
                }
                else if(MediaType.equalsIgnoreCase("MOVIE"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + tmdb_id + "/posters/" + poster_width + images.getPoster().getFileName());
                }

                if(file != null && !file.exists())
                {
                    images.getPoster().saveImage(file, preferredSize);
                }
            }
        }

        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
        
    }
    
    public String GetBackdropRealtime()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetBackdropRealtime(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetBackdropRealtime: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    public String GetBackdropRealtime(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetBackdropRealtime(DEFAULT_BACKDROP_SIZE_WIDTH, blocking);
    }
    
    public String GetBackdropRealtime(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        int tmdb_id = -1;
        String MediaType;
        SearchResults results;
        File file = null;
        
        System.out.println("JVL - Realtime backdrop lookup called");
        
        if(this.show.IsMovie())
        {
            System.out.println("JVL - Looking up movie");
            int year = -1;    
            MediaType = "Movie";
        
            try { year = Integer.parseInt(this.show.GetYear()); } catch (Exception ex) { } 
            
            if(year > 0)
            {
                if(this.HasCachedMovieTMDB_ID(this.show.GetTitle(), year))
                {
                    System.out.println("JVL - Metadata cache hit getting movie id");
                    tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle(), year);
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);

                    if(results != null && results.getMovies().size() > 0)
                    {
                        tmdb_id = results.getMovies().get(0).getTmdbID();
                        this.PutCacheMovieTMDB_ID(this.show.GetTitle(), year, tmdb_id);
                    }
                }
            }
            else
            {
                if(this.HasCachedMovieTMDB_ID(this.show.GetTitle()))
                {
                    System.out.println("JVL - Metadata cache hit getting movie id");
                    tmdb_id = this.GetCachedMovieTMDB_ID(this.show.GetTitle());
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);

                    if(results != null && results.getMovies().size() > 0)
                    {
                        tmdb_id = results.getMovies().get(0).getTmdbID();
                        this.PutCacheMovieTMDB_ID(this.show.GetTitle(), tmdb_id);
                    }
                }
            }

        }
        else
        {
            System.out.println("JVL - Looking up TV");
            MediaType = "TV";
            
            if(this.HasCachedTVTMDB_ID(show.GetTitle()))
            {
                System.out.println("JVL - Metadata cache hit on title for TV Show");
                tmdb_id = this.GetCachedTVTMDB_ID(show.GetTitle());
            }
            else
            {
                results = SearchAPI.searchTV(this.request, show.GetTitle(), blocking);

                if(results != null && results.getShows().size() > 0)
                {
                    tmdb_id = results.getShows().get(0).getTmdbID();
                    this.PutCacheTVTMDB_ID(show.GetTitle(), tmdb_id);
                }
            }
        }
        
        if(tmdb_id > 0)
        {
            Images images = this.GetImages(tmdb_id, MediaType, blocking);
            
            if(images.getBackdrops().size() > 0)
            {
                System.out.println("JVL - Image found");
                String backdrops_width = images.getBackdrop().getValidSize(preferredSize);

                if(MediaType.equalsIgnoreCase("TV"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + tmdb_id + "/backdrops/" + backdrops_width + images.getBackdrop().getFileName());
                }
                else if(MediaType.equalsIgnoreCase("MOVIE"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + tmdb_id + "/backdrops/" + backdrops_width + images.getBackdrop().getFileName());
                }

                if(file != null && !file.exists())
                {
                    images.getBackdrop().saveImage(file, preferredSize);
                }
            }
        }

        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
        
    }
    
    public void SetPoster(Image image)
    {
        Properties overrides = this.GetMetadataOverrides();
        
        overrides.setProperty("show.poster", image.getFileName());
        this.SaveMetadataOverrides(overrides);
    }
    
    public void SetBackdrop(Image image)
    {
        Properties overrides = this.GetMetadataOverrides();
        
        overrides.setProperty("show.backdrop", image.getFileName());
        this.SaveMetadataOverrides(overrides);
    }
    
    public void SetSeasonPoster(Image image) throws SageCallApiException
    {
        Properties overrides = this.GetMetadataOverrides();
        
        overrides.setProperty("show.season_" + this.show.GetSeasonNumber() + ".poster", image.getFileName());
        this.SaveMetadataOverrides(overrides);
    }
    
    public String GetPoster()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetPoster(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetPoster: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    /**
     * Gets the default poster for movie/show with the default size. Will download
     * and save the image into the local cache folder
     * 
     * @return Path to the locally cached poster in the default size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetPoster(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetPoster(Metadata.DEFAULT_POSTER_SIZE_WIDTH, blocking);
    }
    
    /**
     * Gets the default poster for movie/show with the closest match to the preferred size.
     * Will download and save the image into the local cache folder
     * 
     * @return Path to the locally cached file in the closest matched preferred size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetPoster(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        File file = null;
        Images images = null;
        Properties overrides = this.GetMetadataOverrides();
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), blocking);
        }
        
        if(this.HasMetadata() && images != null && images.getPosters().size() > 0)
        {
            Image image = images.getPoster(overrides.getProperty("show.poster", images.getPoster().getFileName()));
            
            if(image == null)
            {
                image = images.getPoster();
            }
            
            String poster_width = image.getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/posters/" + poster_width + image.getFileName());
            }
            else if(this.show.GetMediaType().equalsIgnoreCase("MOVIE"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/posters/" + poster_width + image.getFileName());
            }
            
            if(file != null && !file.exists())
            {
                image.saveImage(file, preferredSize);
            }
        }
     
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    public ArrayList<Image> GetPosterImages()
    {
        ArrayList<Image> images = null;
        
        try 
        {
            if(this.HasMetadata())
            {
                images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), true).getPosters();
            }
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Error getting poster images");
        }
        
        return images;
    }
    
    public String [] GetPosters()
    {
        String [] ret = new String[0];
        
        try 
        {
            ret = this.GetPosters(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetPosterRealtime: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    public String [] GetPosters(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        Images images = null;
        String [] urls = null;
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), blocking);
        }
        
        if(images != null && images.getPosters().size() > 0)
        {
            urls = new String [images.getPosters().size()];
            
            for(int i = 0; i < images.getPosters().size(); i++)
            {
                urls[i] = images.getPosters().get(i).getURL(DEFAULT_POSTER_SIZE_WIDTH);
            }
        }
        
        return urls;
    }
    
    public ArrayList<Image> GetSeasonPosterImages()
    {
        ArrayList<Image> images = null;
        
        try 
        {
            if(this.HasMetadata())
            {
                images = this.GetSeasonImages(this.show.GetTheMovieDBID(),this.show.GetMediaType(), this.show.GetSeasonNumber(), true).getPosters();
            }
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Error getting poster images");
        }
        
        return images;
    }
    
    public String GetSeasonPoster()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetSeasonPoster(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetSeasonPoster: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    public String GetSeasonPoster(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetSeasonPoster(DEFAULT_POSTER_SIZE_WIDTH, blocking);
    }
    
    public String GetSeasonPoster(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        Properties overrides = this.GetMetadataOverrides();
        File file = null;
        Images images = null;
        
        if(this.HasMetadata())
        {
            images = this.GetSeasonImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), this.show.GetSeasonNumber(), blocking);
        }
        
        if(this.HasMetadata() && images != null && images.getPosters().size() > 0)
        {
            Image image = images.getPoster(overrides.getProperty("show.season_" + this.show.GetSeasonNumber() + ".poster", images.getPoster().getFileName()));
            
            if(image == null)
            {
                image = images.getPoster();
            }
            
            String poster_width = image.getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + this.show.GetTheMovieDBID() + "/season_" + this.show.GetSeasonNumber() + "/posters/" + poster_width + image.getFileName());
            }
            
            if(file != null && !file.exists())
            {
                image.saveImage(file, preferredSize);
            }
        }
        
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    public String GetEpisodeStill()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetEpisodeStill(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetEpisodeStill: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    public String GetEpisodeStill(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetEpisodeStill(DEFAULT_STILL_SIZE_WIDTH, blocking);
    }
    
    public String GetEpisodeStill(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        File file = null;
        Images images = null;
        
        if(this.HasMetadata() && show.GetEpisodeNumber() > 0)
        {
            images = this.GetEpisodeImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), this.show.GetSeasonNumber(), this.show.GetEpisodeNumber(), blocking);
        }
        
        if(this.HasMetadata() && images != null && images.getStills().size() > 0)
        {
            String still_width = images.getStill().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + this.show.GetTheMovieDBID() + "/season_" + this.show.GetSeasonNumber() + "/episode_" + this.show.GetEpisodeNumber() + "/stills/" + still_width + images.getStill().getFileName());
            }
            
            if(file != null && !file.exists())
            {
                images.getStill().saveImage(file, preferredSize);
            }
        }
        
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    public ArrayList<Image> GetBackdropImages()
    {
        ArrayList<Image> images = null;
        
        try 
        {
            if(this.HasMetadata())
            {
                images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), true).getBackdrops();
            }
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Error getting poster images");
        }
        
        return images;
    }
    
    public String GetBackdrop()
    {
        String ret = "";
        
        try 
        {
            ret = this.GetBackdrop(false);
        } 
        catch (Exception ex) 
        {
            System.out.println("JVL Metadata - Exception calling GetBackdrop: " + ex.getMessage());
        } 
        
        return ret;
    }
    
    /**
     * Gets the default backdrop for movie/show with the default size. Will download
     * and save the image into the local cache folder
     * 
     * @return Path to the locally cached poster in the default size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetBackdrop(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetBackdrop(DEFAULT_BACKDROP_SIZE_WIDTH, blocking);
    }
    
     /**
     * Gets the default backdrop for movie/show with the closest match to the preferred size.
     * Will download and save the image into the local cache folder
     * 
     * @return Path to the locally cached file in the closest matched preferred size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetBackdrop(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        File file = null;
        Images images = null;
        Properties overrides = this.GetMetadataOverrides();
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), blocking);
        }
        
        if(this.HasMetadata() && images != null && images.getBackdrops().size() > 0)
        {
            Image image = images.getBackdrop(overrides.getProperty("show.backdrop", images.getBackdrop().getFileName()));
            
            if(image == null)
            {
                image = images.getBackdrop();
            }
            
            String backdrop_width = image.getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/backdrops/" + backdrop_width + image.getFileName());
            }
            else if(this.show.GetMediaType().equalsIgnoreCase("MOVIE"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/backdrops/" + backdrop_width + image.getFileName());
            }
            
            if(file != null && !file.exists())
            {
                image.saveImage(file, preferredSize);
            }
        }
        
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    private Images GetSeasonImages(int TheMovieDBID, String MediaType, int seasonNumber, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        Images images = null;
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/season_" + seasonNumber + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request, blocking));
                
                if(images.getPosters().isEmpty())
                {
                    images = TVAPI.getSeasonImages(request, TheMovieDBID, seasonNumber, blocking);
                    
                    if(images != null)
                    {
                        images.save(imagesFile);
                    }
                }
            }
            else
            {
                images = TVAPI.getSeasonImages(request, TheMovieDBID, seasonNumber, blocking);
                
                if(images != null)
                {
                    images.save(imagesFile);
                }
            }
        }
        
        return images;
    }
    
    private Images GetEpisodeImages(int TheMovieDBID, String MediaType, int seasonNumber, int episodeNumber, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        Images images = null;
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/season_" + seasonNumber + "/episode_" + episodeNumber + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request, blocking));
                
                if(images.getStills().isEmpty())
                {
                    images = TVAPI.getEpisodeImages(this.request, TheMovieDBID, seasonNumber, episodeNumber, blocking);
                    
                    if(images != null)
                    {
                        images.save(imagesFile);
                    }
                }
            }
            else
            {
                images = TVAPI.getEpisodeImages(this.request, TheMovieDBID, seasonNumber, episodeNumber, blocking);
                
                if(images != null)
                {
                    images.save(imagesFile);
                }
            }
        }
        
        return images;
    }
    
    private Images GetImages(int TheMovieDBID, String MediaType, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        Images images = null;
        
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request, blocking));
                
                if(images.getBackdrops().isEmpty() || images.getPosters().isEmpty())
                {
                    images = TVAPI.getImages(request, TheMovieDBID, blocking);
                    
                    if(images != null)
                    {
                        images.save(imagesFile);
                    }
                }
            }
            else
            {
                images = TVAPI.getImages(request, TheMovieDBID, blocking);
                
                if(images != null)
                {
                    images.save(imagesFile);
                }
            }
        }
        else if(MediaType.equalsIgnoreCase("MOVIE"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + TheMovieDBID + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request, blocking));
                
                if(images.getBackdrops().isEmpty() || images.getPosters().isEmpty())
                {
                    images = MovieAPI.getImages(request, TheMovieDBID, blocking);
                   
                    if(images != null)
                    {
                        images.save(imagesFile);
                    }
                }

            }
            else
            {
                images = MovieAPI.getImages(request, TheMovieDBID, blocking);
                
                if(images != null)
                {
                    images.save(imagesFile);
                }
            }
        }
        
        
        return images;
    }
    
}
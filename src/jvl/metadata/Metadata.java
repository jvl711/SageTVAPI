
package jvl.metadata;

import java.io.IOException;
import jvl.sage.SageCallApiException;
import jvl.sage.api.Show;
import jvl.tmdb.SearchAPI;
import jvl.tmdb.TMDBRequest;
import jvl.tmdb.model.Movie;
import jvl.tmdb.model.SearchResultMovie;
import jvl.tmdb.model.SearchResults;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import jvl.tmdb.ConfigAPI;
import jvl.tmdb.MovieAPI;
import jvl.tmdb.ShowAPI;
import jvl.tmdb.model.Images;
import jvl.tmdb.model.SearchResultShow;
import jvl.tmdb.model.Season;

/*
 *  Directory Structure
 *      Root ->
 *              movies ->
 *                      [TMDB ID] ->
 *                                  details.json
 *                                  images.json
 *                                  posters ->
 *                                          size_name.jpg
 *                                  backdrops ->
 *                                          size_name.jpg
 *
 *      Root ->
 *              shows ->
 *                      {TMDB ID} ->
 *                                  details.json
 
 *                                  images.json
 *                                  posters (Show) ->
 *                                                  size_name.jpg
 *                                  backdrops (Show) ->
 *                                                  size_name.jpg
 *                                  season_{n} ->
 *                                                  season.json
 *                                                  posters ->
 *                                                              size_name.jpg
 *                                                  episode_{n} ->
 *                                                              size_name.jpg
 *
 *  
*/



public class Metadata 
{
    //private String cacheFolder;
    private File cacheFolder;
    private Show show;
    private TMDBRequest request;
    
    private static final int DEFAULT_POSTER_SIZE_WIDTH = 600;
    private static final int DEFAULT_BACKDROP_SIZE_WIDTH = 1920;
    
    
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
    
    
    public boolean LookupMetadata(boolean forceRefresh) throws SageCallApiException, IOException
    {
        System.out.println("JVL - Working directory: " + this.cacheFolder.getAbsolutePath());
        System.out.println("JVL - LookupMetaData was called");
        
        if(!this.HasMetadata() || forceRefresh)
        {
            if(this.show.GetMediaFile().IsTVFile() && !this.show.IsMovie()) //Recorded TV
            {
                System.out.println("JVL - This is recorded content that is not a movie");
                
                if(this.show.GetEpisodeNumber() > 0)
                {
                    SearchResults results = SearchAPI.searchTV(this.request, show.GetTitle());
                    
                    if(results.getShows().size() > 0)
                    {
                        this.SaveTVMetadata(results.getShows().get(0), this.show.GetSeasonNumber(), this.show.GetEpisodeNumber());
                    }
                    else
                    {
                        //TODO:  Think about logging this to the system message interface
                        System.out.println("JVL - TMDB there was no hit finding this show.");
                        System.out.println("Show title: " + this.show.GetTitle());
                        
                        return false;
                    }
                }
                else
                {
                    System.out.println("JVL - This show does not appear to have an episode number.  I am going to skip it.");
                }
                
                return false;
            }
            else if(this.show.GetMediaFile().IsTVFile() && this.show.IsMovie()) //Recored Movie
            {
                SearchResults results;
                int year = 0;
                
                try{ year = Integer.parseInt(this.show.GetYear()); } catch(Exception ex) { }
                
                if(year > 0)
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year);
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle());
                }
                
                if(results.getMovies().size() > 0)
                {
                    this.SaveMovieMetadata(results.getMovies().get(0), year);
                }
                else
                {
                    //TODO:  Think about logging this to the system message interface
                    System.out.println("JVL - TMDB there was no hit finding this movie.");
                    System.out.println("Movie title: " + this.show.GetTitle());
                    
                    return false;
                }

            }
            else //This is non-recorded content
            {
                FileNameParser parser = new FileNameParser(this.show.GetMediaFile().GetFileName());
                System.out.println("JVL - Filename: " + this.show.GetMediaFile().GetFileName());
                
                SearchResults results;
                
                if(parser.IsMovie())
                {
                    System.out.println("JVL - Parser IsMovie true");
                    
                    if(parser.GetReleaseYear() > 0)
                    {
                        System.out.println("JVL - Parser Title: " + parser.GetTitle());
                        System.out.println("JVL - Parser Release Year: " + parser.GetReleaseYear());
                        results = SearchAPI.searchMovies(request, parser.GetTitle(), parser.GetReleaseYear());
                    }
                    else
                    {
                        System.out.println("JVL - Parser Title: " + parser.GetTitle());
                        results = SearchAPI.searchMovies(request, parser.GetTitle());
                    }
                    
                    if(results.getMovies().size() > 0)
                    {
                        this.SaveMovieMetadata(results.getMovies().get(0), parser.GetReleaseYear());
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
                    results = SearchAPI.searchTV(request, parser.GetTitle());
                    
                    if(results.getShows().size() > 0)
                    {
                        this.SaveTVMetadata(results.getShows().get(0), parser.GetSeason(), parser.GetEpisode());
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
    
    private void SaveTVMetadata(SearchResultShow result, int seasonNumber, int episodeNumber) throws IOException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/shows/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/shows/" + result.getTmdb_ID() + "/images.json");
        File seasonsFile = new File(this.cacheFolder.getAbsolutePath() + "/shows/" + result.getTmdb_ID() + "season_" + seasonNumber + "/season.json");
        
        boolean updateCache = false;
        
        //Check to see if we need to update files.  If we already have the season episode info
        //then skip.  If we are missing anything than update it all.
        if(!detailsFile.exists()) { updateCache = true; }
        
        if(!imagesFile.exists()) { updateCache = true; }
    
        if(!seasonsFile.exists())
        {
            updateCache = true;
        }
        else
        {
            Season season = Season.parseFile(seasonsFile, ConfigAPI.getConfig(request));
            
            if(season.getEpisodeCount() < episodeNumber)
            {
                { updateCache = true; }
            }
        }
        
        Date now = new Date();
        //Show show = ShowAPI.getDetails(this.request, result.getTmdb_ID());
        
        
    }
    
    private void SaveMovieMetadata(SearchResultMovie result, int year) throws SageCallApiException, FileNotFoundException, IOException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/images.json");
        
        Date now = new Date();
        Movie movie = MovieAPI.getDetails(request, result.getTmdb_ID());
        Images images = MovieAPI.getImages(request, result.getTmdb_ID());
        
        show.SetTheMovieDBID(result.getTmdb_ID());
        show.SetTitle(movie.getTitle());
        show.SetDescription(movie.getOverview());
        show.SetCategories(movie.getGenres());
        show.SetYear(movie.getReleaseYear());
        show.SetMetadataUpdateDate(now.getTime());
        show.SetMediaType("Movie");
        
        movie.save(detailsFile);
        images.save(imagesFile);
        
        //Cache the default poster and backdrop in the default size
        this.GetPoster();
        this.GetBackdrop();
    }
    
    private boolean HasMetadata()
    {
        if(this.show.GetTheMovieDBID() == -1)
        {
            return false;
        }
        
        return true;
    }
    
    public Movie GetMovieDetails() throws IOException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/detials.json");
        Movie movie = null;
        
        if(!detailsFile.exists())
        {
            movie = MovieAPI.getDetails(this.request, this.show.GetTheMovieDBID());
        }
        
        return movie;
    }
    
    
    /**
     * Gets the default poster for movie/show with the default size. Will download
     * and save the image into the local cache folder
     * 
     * @return Path to the locally cached poster in the default size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetPoster() throws SageCallApiException, IOException
    {
        return this.GetPoster(Metadata.DEFAULT_POSTER_SIZE_WIDTH);
    }
    
    /**
     * Gets the default poster for movie/show with the closest match to the preferred size.
     * Will download and save the image into the local cache folder
     * 
     * @return Path to the locally cached file in the closest matched preferred size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetPoster(int preferredSize) throws SageCallApiException, IOException
    {
        File file = null;
        Images images = this.GetImages();
        
        if(this.HasMetadata() && images.getPosters().size() > 0)
        {
            String poster_width = images.getPoster().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/show/" + show.GetTheMovieDBID() + "/posters/" + poster_width + images.getPoster().getFileName());
            }
            else if(this.show.GetMediaType().equalsIgnoreCase("MOVIE"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/posters/" + poster_width + images.getPoster().getFileName());
            }
            
            if(file != null && !file.exists())
            {
                images.getPoster().saveImage(file, preferredSize);
            }
        }
        
        return file.getAbsolutePath();
    }
    
    /**
     * Gets the default backdrop for movie/show with the default size. Will download
     * and save the image into the local cache folder
     * 
     * @return Path to the locally cached poster in the default size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetBackdrop() throws SageCallApiException, IOException
    {
        return this.GetBackdrop(DEFAULT_BACKDROP_SIZE_WIDTH);
    }
    
     /**
     * Gets the default backdrop for movie/show with the closest match to the preferred size.
     * Will download and save the image into the local cache folder
     * 
     * @return Path to the locally cached file in the closest matched preferred size
     * @throws SageCallApiException
     * @throws IOException 
     */
    public String GetBackdrop(int preferredSize) throws SageCallApiException, IOException
    {
        File file = null;
        Images images = this.GetImages();
        
        if(this.HasMetadata() && images.getBackdrops().size() > 0)
        {
            String backdrop_width = images.getBackdrop().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/show/" + show.GetTheMovieDBID() + "/backdrops/" + backdrop_width + images.getBackdrop().getFileName());
            }
            else if(this.show.GetMediaType().equalsIgnoreCase("MOVIE"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/backdrops/" + backdrop_width + images.getBackdrop().getFileName());
            }
            
            if(file != null && !file.exists())
            {
                images.getBackdrop().saveImage(file, preferredSize);
            }
        }
        
        return file.getAbsolutePath();
    }
    
    private Images GetImages() throws SageCallApiException, IOException
    {
        Images images = null;
        
        if(this.HasMetadata())
        {
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/shows/" + this.show.GetTheMovieDBID() + "/images.json");
                
                if(imagesFile.exists())
                {
                    images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
                }
                else
                {
                    images = ShowAPI.getImages(request, this.show.GetTheMovieDBID());
                    images.save(imagesFile);
                }
            }
            else if(this.show.GetMediaType().equalsIgnoreCase("MOVIE"))
            {
                File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + this.show.GetTheMovieDBID() + "/images.json");
                
                if(imagesFile.exists())
                {
                    images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
                }
                else
                {
                    images = MovieAPI.getImages(request, this.show.GetTheMovieDBID());
                    images.save(imagesFile);
                }
            }
        }
        
        return images;
    }
    
    private Movie GetMovie()
    {
        //This assumes we already know the TMDB ID
        int id = this.show.GetTheMovieDBID();
        
        
        if(id != -1)
        {
            File json = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + id + "movie.json");

        }
        
        return null;
    }
    
}
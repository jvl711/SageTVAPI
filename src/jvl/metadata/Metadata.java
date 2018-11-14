
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
import jvl.tmdb.RateLimitException;
import jvl.tmdb.TVAPI;
import jvl.tmdb.model.Episode;
import jvl.tmdb.model.Images;
import jvl.tmdb.model.SearchResultShow;
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
        System.out.println("JVL - Working directory: " + this.cacheFolder.getAbsolutePath());
        System.out.println("JVL - LookupMetaData was called");
        
        if(!this.HasMetadata() || forceRefresh)
        {
            if(this.show.GetMediaFile().IsTVFile() && !this.show.IsMovie()) //Recorded TV
            {
                System.out.println("JVL - This is recorded content that is not a movie");
                

                SearchResults results = SearchAPI.searchTV(this.request, show.GetTitle(), blocking);

                if(results != null && results.getShows().size() > 0)
                {
                    if(show.GetEpisodeNumber() > 0)
                    {
                        this.SaveTVMetadata(results.getShows().get(0), this.show.GetSeasonNumber(), this.show.GetEpisodeNumber(), blocking);
                    }
                    else
                    {
                        //This is for shows that do not have season episode numbers
                        this.SaveTVMetadata(results.getShows().get(0), blocking);
                    }
                }
                else
                {
                    //TODO:  Think about logging this to the system message interface
                    System.out.println("JVL - TMDB there was no hit finding this show.");
                    System.out.println("Show title: " + this.show.GetTitle());

                    return false;
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
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);
                }
                else
                {
                    results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);
                }
                
                if(results != null && results.getMovies().size() > 0)
                {
                    this.SaveMovieMetadata(results.getMovies().get(0), year, blocking);
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
                        results = SearchAPI.searchMovies(request, parser.GetTitle(), parser.GetReleaseYear(), blocking);
                    }
                    else
                    {
                        System.out.println("JVL - Parser Title: " + parser.GetTitle());
                        results = SearchAPI.searchMovies(request, parser.GetTitle(), blocking);
                    }
                    
                    if(results != null && results.getMovies().size() > 0)
                    {
                        this.SaveMovieMetadata(results.getMovies().get(0), parser.GetReleaseYear(), blocking);
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
                    results = SearchAPI.searchTV(request, parser.GetTitle(), blocking);
                    
                    if(results != null && results.getShows().size() > 0)
                    {
                        this.SaveTVMetadata(results.getShows().get(0), parser.GetSeason(), parser.GetEpisode(), blocking);
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
    
    private void SaveTVMetadata(SearchResultShow result, int seasonNumber, int episodeNumber, boolean blocking) throws IOException, SageCallApiException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/images.json");
        File seasonFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/season_" + seasonNumber + "/season.json");
        File seasonImagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/season_" + seasonNumber + "/images.json");
        File episodeImagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/season_" + seasonNumber + "/episode_" + episodeNumber + "/images.json");
        
        boolean updateCache = false;
        
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
            tv = TVAPI.getDetails(this.request, result.getTmdb_ID(), blocking);
            
            if(tv == null)
            {
                return;
            }
            
            season = TVAPI.getSeasonDetails(this.request, result.getTmdb_ID(), seasonNumber, blocking);
            
            if(season != null)
            {
                episode = season.getEpisode(episodeNumber);
            }
            
        }
        else
        {
            tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request, blocking));
            season = Season.parseFile(seasonFile, ConfigAPI.getConfig(request, blocking));
            episode = season.getEpisode(episodeNumber);
        }

        
        
        show.SetTheMovieDBID(result.getTmdb_ID());
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
            Images images = TVAPI.getImages(this.request, result.getTmdb_ID(), blocking);
            Images seasonImages = TVAPI.getSeasonImages(request, result.getTmdb_ID(), seasonNumber, blocking);
            Images episodeImages = TVAPI.getEpisodeImages(request, result.getTmdb_ID(), seasonNumber, episodeNumber, blocking);

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
        
        
        this.GetPoster();
        this.GetBackdrop();
        this.GetSeasonPoster();
        this.GetEpisodeStill();
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
    private void SaveTVMetadata(SearchResultShow result, boolean blocking) throws IOException, SageCallApiException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + result.getTmdb_ID() + "/images.json");
        
        boolean updateCache = false;
        
        //Check to see if we need to update files. If we are missing anything than update it all.
        if(!detailsFile.exists()) { updateCache = true; }
        
        if(!imagesFile.exists()) { updateCache = true; }
    
        Date now = new Date();
        TV tv;
            
        if(updateCache)
        {
            tv = TVAPI.getDetails(this.request, result.getTmdb_ID(), blocking);
        }
        else
        {
            tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request, blocking));
        }

        show.SetTheMovieDBID(result.getTmdb_ID());
        show.SetMetadataUpdateDate(now.getTime());
        show.SetMediaType("TV");
        
        if(tv != null)
        {
            show.SetTitle(tv.getName());
            show.SetCategories(tv.getGenres());
        }

        if(updateCache)
        {
            Images images = TVAPI.getImages(this.request, result.getTmdb_ID(), blocking);

            if(tv != null)
            {
                tv.save(detailsFile);
            }
            
            if(images.getPosters().size() > 0 || images.getBackdrops().size() > 0)
            {
                images.save(imagesFile);
            }
            
        }
  
        this.GetPoster();
        this.GetBackdrop();
    }
    
    private void SaveMovieMetadata(SearchResultMovie result, int year, boolean blocking) throws SageCallApiException, FileNotFoundException, IOException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/images.json");
        
        Date now = new Date();
        Movie movie = MovieAPI.getDetails(request, result.getTmdb_ID(), blocking);
        Images images = MovieAPI.getImages(request, result.getTmdb_ID(), blocking);
        
        show.SetTheMovieDBID(result.getTmdb_ID());
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
        
        this.GetPoster();
        this.GetBackdrop();
    }
    
    public boolean HasMetadata()
    {
        return this.show.GetTheMovieDBID() != -1;
    }
    
    public Movie GetMovieDetails() throws IOException, RateLimitException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + show.GetTheMovieDBID() + "/detials.json");
        Movie movie = null;
        
        if(!detailsFile.exists())
        {
            movie = MovieAPI.getDetails(this.request, this.show.GetTheMovieDBID(), false);
        }
        
        return movie;
    }
    
    public String GetPosterRealtime(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetPosterRealtime(DEFAULT_POSTER_SIZE_WIDTH, blocking);
    }
    
    public String GetPosterRealtime(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        int TheMovieDBID = -1;
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
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);                
            }
            else
            {
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);    
            }
            
            if(results != null && results.getMovies().size() > 0)
            {
                System.out.println("JVL - Found movie");
                TheMovieDBID = results.getMovies().get(0).getTmdb_ID();
            }
        }
        else
        {
            System.out.println("JVL - Looking up TV");
            MediaType = "TV";
            results = SearchAPI.searchTV(this.request, this.show.GetTitle(), blocking);
            
            if(results.getShows().size() > 0)
            {
                System.out.println("JVL - Found show");
                TheMovieDBID = results.getShows().get(0).getTmdb_ID();
            }
        }
        
        if(TheMovieDBID > 0)
        {
            Images images = this.GetImages(TheMovieDBID, MediaType);
            
            if(images.getPosters().size() > 0)
            {
                System.out.println("JVL - Image found");
                String poster_width = images.getPoster().getValidSize(preferredSize);

                if(MediaType.equalsIgnoreCase("TV"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/posters/" + poster_width + images.getPoster().getFileName());
                }
                else if(MediaType.equalsIgnoreCase("MOVIE"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + TheMovieDBID + "/posters/" + poster_width + images.getPoster().getFileName());
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
    
    public String GetBackdropRealtime(boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        return this.GetBackdropRealtime(DEFAULT_BACKDROP_SIZE_WIDTH, blocking);
    }
    
    public String GetBackdropRealtime(int preferredSize, boolean blocking) throws SageCallApiException, IOException, RateLimitException
    {
        int TheMovieDBID = -1;
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
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year, blocking);                
            }
            else
            {
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), blocking);    
            }
            
            if(results != null && results.getMovies().size() > 0)
            {
                System.out.println("JVL - Found movie");
                TheMovieDBID = results.getMovies().get(0).getTmdb_ID();
            }
        }
        else
        {
            System.out.println("JVL - Looking up TV");
            MediaType = "TV";
            results = SearchAPI.searchTV(this.request, this.show.GetTitle(), blocking);
            
            if(results != null && results.getShows().size() > 0)
            {
                System.out.println("JVL - Found show");
                TheMovieDBID = results.getShows().get(0).getTmdb_ID();
            }
        }
        
        if(TheMovieDBID > 0)
        {
            Images images = this.GetImages(TheMovieDBID, MediaType);
            
            if(images.getBackdrops().size() > 0)
            {
                System.out.println("JVL - Image found");
                String backdrops_width = images.getBackdrop().getValidSize(preferredSize);

                if(MediaType.equalsIgnoreCase("TV"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/backdrops/" + backdrops_width + images.getBackdrop().getFileName());
                }
                else if(MediaType.equalsIgnoreCase("MOVIE"))
                {
                    file = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + TheMovieDBID + "/backdrops/" + backdrops_width + images.getBackdrop().getFileName());
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
        Images images = null;
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType());
        }
        
        if(this.HasMetadata() && images != null && images.getPosters().size() > 0)
        {
            String poster_width = images.getPoster().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/posters/" + poster_width + images.getPoster().getFileName());
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
     
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    public String [] GetPosters() throws SageCallApiException, IOException
    {
        Images images = null;
        String [] urls = null;
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType());
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
    
    public String GetSeasonPoster() throws SageCallApiException, IOException
    {
        return this.GetSeasonPoster(DEFAULT_POSTER_SIZE_WIDTH);
    }
    
    public String GetSeasonPoster(int preferredSize) throws SageCallApiException, IOException
    {
        File file = null;
        Images images = null;
        
        if(this.HasMetadata())
        {
            images = this.GetSeasonImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), this.show.GetSeasonNumber());
        }
        
        if(this.HasMetadata() && images != null && images.getPosters().size() > 0)
        {
            String poster_width = images.getPoster().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + this.show.GetTheMovieDBID() + "/season_" + this.show.GetSeasonNumber() + "/posters/" + poster_width + images.getPoster().getFileName());
            }
            
            if(file != null && !file.exists())
            {
                images.getPoster().saveImage(file, preferredSize);
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
    
    public String GetEpisodeStill() throws SageCallApiException, IOException
    {
        return this.GetEpisodeStill(DEFAULT_STILL_SIZE_WIDTH);
    }
    
    public String GetEpisodeStill(int preferredSize) throws SageCallApiException, IOException
    {
        File file = null;
        Images images = null;
        
        if(this.HasMetadata() && show.GetEpisodeNumber() > 0)
        {
            images = this.GetEpisodeImages(this.show.GetTheMovieDBID(), this.show.GetMediaType(), this.show.GetSeasonNumber(), this.show.GetEpisodeNumber());
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
        Images images = null;
        
        if(this.HasMetadata())
        {
            images = this.GetImages(this.show.GetTheMovieDBID(), this.show.GetMediaType());
        }
        
        if(this.HasMetadata() && images != null && images.getBackdrops().size() > 0)
        {
            String backdrop_width = images.getBackdrop().getValidSize(preferredSize);
            
            if(this.show.GetMediaType().equalsIgnoreCase("TV"))
            {
                file = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + show.GetTheMovieDBID() + "/backdrops/" + backdrop_width + images.getBackdrop().getFileName());
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
        
        if(file != null)
        {
            return file.getAbsolutePath();
        }
        else
        {
            return "";
        }
    }
    
    private Images GetSeasonImages(int TheMovieDBID, String MediaType, int seasonNumber) throws SageCallApiException, IOException
    {
        Images images = null;
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/season_" + seasonNumber + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
            }
            else
            {
                images = TVAPI.getSeasonImages(request, TheMovieDBID, seasonNumber);
                images.save(imagesFile);
            }
        }
        
        return images;
    }
    
    private Images GetEpisodeImages(int TheMovieDBID, String MediaType, int seasonNumber, int episodeNumber) throws SageCallApiException, IOException
    {
        Images images = null;
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/season_" + seasonNumber + "/episode_" + episodeNumber + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
            }
            else
            {
                images = TVAPI.getEpisodeImages(this.request, TheMovieDBID, seasonNumber, episodeNumber);
                
                if(images.getStills().size() > 0)
                {
                    images.save(imagesFile);
                }
            }
        }
        
        return images;
    }
    
    private Images GetImages(int TheMovieDBID, String MediaType) throws SageCallApiException, IOException
    {
        Images images = null;
        
        
        if(MediaType.equalsIgnoreCase("TV"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/tv/" + TheMovieDBID + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
            }
            else
            {
                images = TVAPI.getImages(request, TheMovieDBID);
                images.save(imagesFile);
            }
        }
        else if(MediaType.equalsIgnoreCase("MOVIE"))
        {
            File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + TheMovieDBID + "/images.json");

            if(imagesFile.exists())
            {
                images = Images.parseFile(imagesFile, ConfigAPI.getConfig(this.request));
            }
            else
            {
                images = MovieAPI.getImages(request, TheMovieDBID);
                images.save(imagesFile);
            }
        }
        
        
        return images;
    }
    
}
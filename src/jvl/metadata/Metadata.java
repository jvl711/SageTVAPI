
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
    
    private void SaveTVMetadata(SearchResultShow result, int seasonNumber, int episodeNumber) throws IOException, SageCallApiException
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
            TV tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request));
            
            if(!tv.hasSeason(seasonNumber))
            {
                updateCache = true;
            }
            else
            {
                Season season = Season.parseFile(seasonFile, ConfigAPI.getConfig(request));

                if(!season.hasEpisode(episodeNumber))
                {
                    updateCache = true;
                }
            }
        }
                
        Date now = new Date();

        TV tv;
        Season season;
        Episode episode;
            
        if(updateCache)
        {
            tv = TVAPI.getDetails(this.request, result.getTmdb_ID());
            season = TVAPI.getSeasonDetails(this.request, result.getTmdb_ID(), seasonNumber);
            episode = season.getEpisode(episodeNumber);
        }
        else
        {
            tv = TV.parseFile(detailsFile, ConfigAPI.getConfig(request));
            season = Season.parseFile(seasonFile, ConfigAPI.getConfig(request));
            episode = season.getEpisode(episodeNumber);
        }

        if(!tv.hasSeason(seasonNumber))
        {
            //TODO: log the failure to system messages
            System.out.println("JVL - The season was not found for the given show");
            System.out.println("JVL - Search result: " + result.getName());
            System.out.println("JVL - Season: " + seasonNumber);

            return;
        }

        if(!season.hasEpisode(episodeNumber))
        {
            //TODO: log the failure to system messages
            System.out.println("JVL - The episode was not found for the given show and season");
            System.out.println("JVL - Search result: " + result.getName());
            System.out.println("JVL - Season: " + seasonNumber);
            System.out.println("JVL - Episode: " + episodeNumber);

            return;
        }
        
        show.SetTheMovieDBID(result.getTmdb_ID());
        show.SetTitle(tv.getName());
        show.SetEpisodeName(episode.getName());
        show.SetEpisodeNumber(episodeNumber);
        show.SetSeasonNumber(seasonNumber);
        show.SetDescription(episode.getOverview());
        show.SetCategories(tv.getGenres());
        show.SetMetadataUpdateDate(now.getTime());
        show.SetMediaType("TV");

        if(updateCache)
        {
            Images images = TVAPI.getImages(this.request, result.getTmdb_ID());
            Images seasonImages = TVAPI.getSeasonImages(request, result.getTmdb_ID(), seasonNumber);
            Images episodeImages = TVAPI.getEpisodeImages(request, result.getTmdb_ID(), seasonNumber, episodeNumber);

            tv.save(detailsFile);
            season.save(seasonFile);

            images.save(imagesFile);
            episodeImages.save(episodeImagesFile);
            seasonImages.save(seasonImagesFile);
        }
        
        
        this.GetPoster();
        this.GetBackdrop();
        this.GetSeasonPoster();
        this.GetEpisodeStill();
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
    
    public boolean HasMetadata()
    {
        return this.show.GetTheMovieDBID() != -1;
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
    
    public String GetPosterRealtime() throws SageCallApiException, IOException
    {
        return this.GetPosterRealtime(DEFAULT_POSTER_SIZE_WIDTH);
    }
    
    public String GetPosterRealtime(int preferredSize) throws SageCallApiException, IOException
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
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle(), year);                
            }
            else
            {
                results = SearchAPI.searchMovies(this.request, this.show.GetTitle());    
            }
            
            if(results.getMovies().size() > 0)
            {
                System.out.println("JVL - Found movie");
                TheMovieDBID = results.getMovies().get(0).getTmdb_ID();
            }
        }
        else
        {
            System.out.println("JVL - Looking up TV");
            MediaType = "TV";
            results = SearchAPI.searchTV(this.request, this.show.GetTitle());
            
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
        
        if(this.HasMetadata())
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
                images.save(imagesFile);
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
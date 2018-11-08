
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
import jvl.tmdb.MovieAPI;
import jvl.tmdb.model.Images;
import jvl.tmdb.model.SearchResultShow;

/*
 *  Directory Structure
 *      Root ->
 *              movies ->
 *                      [TMDB ID] ->
 *                                  details.json
 *                                  images.json
 *                                  posters ->
 *                                          size_name.jpg
 *                                  backgrounds ->
 *                                          size_name.jpg
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
    
    private void SaveTVMetadata(SearchResultShow show, int seasonNumber, int episodeNumber)
    {
        //MediaType=TV
    }
    
    private void SaveMovieMetadata(SearchResultMovie result, int year) throws SageCallApiException, FileNotFoundException, IOException
    {
        File detailsFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/detials.json");
        File imagesFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/images.json");
        
        Movie movie = MovieAPI.getDetails(request, result.getTmdb_ID());
        Images images = MovieAPI.getImages(request, result.getTmdb_ID());
        
        show.SetTheMovieDBID(result.getTmdb_ID());
        show.SetTitle(movie.getTitle());
        show.SetDescription(movie.getOverview());
        show.SetCategories(movie.getGenres());
        show.SetMediaType("Movie");
        
        //show.SetYear(result.getReleaseDate());  //TODO: I need to get the releae year.  Add method 
        
        movie.save(detailsFile);
        images.save(imagesFile);
        
        String poster_width = images.getPoster().getValidSize(Metadata.DEFAULT_POSTER_SIZE_WIDTH);
        File posterFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/posters/" + poster_width + images.getPoster().getFileName());
        images.getPoster().saveImage(posterFile, Metadata.DEFAULT_POSTER_SIZE_WIDTH);
        
        String backdrop_width = images.getBackdrop().getValidSize(Metadata.DEFAULT_BACKDROP_SIZE_WIDTH);
        File backdropFile = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/backdrops/" + backdrop_width + images.getBackdrop().getFileName());
        images.getBackdrop().saveImage(backdropFile, Metadata.DEFAULT_BACKDROP_SIZE_WIDTH);
    }
    
    private boolean HasMetadata()
    {
        if(this.show.GetTheMoiveDBID() == -1)
        {
            return false;
        }
        
        return true;
    }
    
    private Movie GetMovie()
    {
        //This assumes we already know the TMDB ID
        int id = this.show.GetTheMoiveDBID();
        
        
        if(id != -1)
        {
            File json = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + id + "movie.json");

        }
        
        return null;
    }
    
}
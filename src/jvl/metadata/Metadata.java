
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
import jvl.tmdb.MovieAPI;
import jvl.tmdb.model.Images;

/*
 *  Directory Structure
 *      Root ->
 *              movies ->
 *                      [TMDB ID] ->
 *                                  movie.json
 *                                  images.json
 *                                  posters ->
 *                                          {SIZES}
 *                                  backgrounds ->
 *                                          {SIZES}
 *  
*/



public class Metadata 
{
    //private String cacheFolder;
    private File cacheFolder;
    private Show show;
    private TMDBRequest request;
    
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
    
    
    public boolean LookupMetaData(boolean forceRefresh) throws SageCallApiException, IOException
    {
        System.out.println("JVL - Working directory: " + this.cacheFolder.getAbsolutePath());
        System.out.println("JVL - LookupMetaData was called");
        
        if(this.show.GetTheMoiveDBID() == -1 || forceRefresh)
        {
            
            if(this.show.GetMediaFile().IsTVFile()) //Get info from title and season/epsidoe of metadara
            {
                //TODO:  Add this functionallity.  Do nothing now.
                System.out.println("JVL - LookupMetaData IsTVFIle");
                
                return false;
            }
            else
            {
                FileNameParser parser = new FileNameParser(this.show.GetMediaFile().GetFileName());
                System.out.println("JVL - Filename: " + this.show.GetMediaFile().GetFileName());
                
                SearchResults results;
                
                if(parser.IsMovie())
                {
                    System.out.println("JVL - Parser IsMovie true");
                    
                    if(parser.GetReleaseYear() != -1)
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
                        SearchResultMovie result = results.getMovies().get(0);
                        File json = new File(this.cacheFolder.getAbsolutePath() + "/movies/" + result.getTmdb_ID() + "/");
                        
                        System.out.println("JVL - TMDB Search Result Count: " + results.getMovies().size());
                        
                        show.SetTheMovieDBID(result.getTmdb_ID());                        

                        Movie movie = MovieAPI.getDetails(request, result.getTmdb_ID() + "");
                        movie.save(json);
                        
                        Images images = MovieAPI.getImages(request, result.getTmdb_ID() + "");

                    }
                    else
                    {
                        System.out.println("JVL - No results found");
                        return false;
                    }
                }
                else
                {
                    //TODO:  Add this functionallity.  Do nothing now.
                    System.out.println("JVL - Parser found IsMovie is false");
                    return false;
                }
                
            }
            
            
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
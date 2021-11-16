
package jvl.metadata;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvl.logging.Logging;
import jvl.sage.SageCallApiException;
import jvl.sage.api.Airing;
import jvl.sage.api.Configuration;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaFiles;
import jvl.sage.api.Show;
import jvl.tmdb.RateLimitException;
import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import jvl.sage.api.server.MiniServer;


public class MetadataPlugin implements SageTVPlugin
{
    
    private final SageTVPluginRegistry registry;
    private MiniServer miniserver;
    
    private static final Logger LOG = Logging.getLogger(MetadataPlugin.class.getName());
    
    public MetadataPlugin(SageTVPluginRegistry registry) throws IOException
    {
        try
        {
            LOG.warning("Setting logging level for MetadataPlugin");
            String value = Configuration.GetServerProperty("jvl.metadataplugin.debuglevel", Level.WARNING.intValue() + "");
            
            Level level = Level.parse(value.toUpperCase());
            Logging.getLogger("jvl.metadata").setLevel(level);
        }
        catch(Exception ex)
        {
            LOG.log(Level.WARNING ,"Unable to parse and apply debug level for pluggin", ex);
        }
        
        LOG.info("Metadata Plugin Constructed");
        LOG.log(Level.INFO, "\tjvl.sage.api.version: {0}", jvl.sage.api.Version.getVersion());
        LOG.log(Level.INFO, "\tjvl.sage.api.build: {0}", jvl.sage.api.Version.getBuildNumber());
        LOG.log(Level.INFO, "\tjvl.sage.api.buildtime: {0}",  jvl.sage.api.Version.getBuildTime());
        
        this.registry = registry;
        
        /*
        try
        {
            miniserver = new MiniServer(8080);
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Error creating mini server.  Check that the port is available.");
        }
        */
    }
    
    @Override
    public void start() 
    {
        LOG.info("Metadata Plugin Starting");
        LOG.info("\tRegistering Events");
        this.registry.eventSubscribe(this, "RecordingStarted");
        this.registry.eventSubscribe(this, "MediaFileImported");
        this.registry.eventSubscribe(this, "MediaFileRemoved");
        this.registry.eventSubscribe(this, "ImportingCompleted");
        this.registry.eventSubscribe(this, "PlaybackStarted");
        this.registry.eventSubscribe(this, "PlaybackFinished");
        this.registry.eventSubscribe(this, "PlaybackStopped");
        this.registry.eventSubscribe(this, "WatchedStateChanged");

        //System.out.println("JVL Starting the MiniServer");
        //this.miniserver.start();
    }

    @Override
    public void stop() 
    {
        LOG.info("Metadata Plugin Stopping");
                
        /*
        System.out.println("JVL Stopping the MiniServer");
        this.miniserver.stop();
        */
    }

    @Override
    public void destroy() 
    {
        LOG.info("Metadata Plugin Destroying");
    }

    @Override
    public String[] getConfigSettings() 
    {
        return new String[]{"DebugLevel", "APIKey"};
    }

    @Override
    public String getConfigValue(String string) 
    {
        String value = "";
        
        //value = this.getProperty("jvl.MediaFormatParserPlugin.Debug", "false");
        if(string.equals("DebugLevel"))
        {
            value = Configuration.GetServerProperty("jvl.metadataplugin.debuglevel", "Warning");
        }
        else if (string.equals("APIKey"))
        {
            value = Configuration.GetServerProperty("jvl.metadataplugin.apikey", "");
        }
        
        return value;
    }


    @Override
    public int getConfigType(String string) 
    {
        if(string.equals("DebugLevel"))
        {
            return SageTVPlugin.CONFIG_CHOICE;
        }
        else if (string.equals("APIKey"))
        {
            return SageTVPlugin.CONFIG_TEXT;
        }
        else
        {
            throw new UnsupportedOperationException("Not supported yet."); 
        }
    }

    @Override
    public void setConfigValue(String string, String value) 
    {
        if(string.equals("DebugLevel"))
        {
            jvl.sage.api.Configuration.SetServerProperty("jvl.metadataplugin.debuglevel", value);
            
            try
            {
                Level level = Level.parse(value.toUpperCase());
                Logging.getLogger("jvl.metadata").setLevel(level);
            }
            catch(Exception ex)
            {
                LOG.log(Level.WARNING ,"Unable to parse and apply debug level for pluggin", ex);
            }
        }
        else if (string.equals("APIKey"))
        {
            jvl.sage.api.Configuration.SetServerProperty("jvl.metadataplugin.apikey", value);
        }
    }

    @Override
    public void setConfigValues(String string, String[] strings) 
    {
        
    }

    @Override
    public String[] getConfigOptions(String string) 
    {
        if(string.equals("DebugLevel"))
        {
            return new String[]{"Severe","Warning","Info","All"};
        }

        
        return null;
    }

    @Override
    public String getConfigHelpText(String string) 
    {
        if(string.equals("DebugLevel"))
        {
            return "The level of detail placed in the SageTV log file for the plugin";
        }
        else if (string.equals("APIKey"))
        {
            return "APIKey for TheMovieDB.  This is required to access the service.  You will need to create and account to get a key.";
        }
        
        return "";
    }

    @Override
    public String getConfigLabel(String string) 
    {
        if(string.equals("DebugLevel"))
        {
            return "Debug Level";
        }
        else if (string.equals("APIKey"))
        {
            return "TheMovieDB API Key";
        }
        
        return "";
    }

    @Override
    public void resetConfig() 
    {
        
    }

    @Override
    public void sageEvent(String event, Map args) 
    {
        try
        {
            if(event.equalsIgnoreCase("RecordingStarted"))
            {
                System.out.println("JVL Metadata Plugin - Recording Started Event Called");

                if(args.containsKey("MediaFile"))
                {
                    if(MediaFile.IsMediaFileObject(args.get("MediaFile")))
                    {
                        Show show = new Show(args.get("MediaFile"));
                        this.RecordingStartedHandler(show);
                    }
                    else
                    {
                        System.out.println("JVL Metadata Plugin - Args was not reported as a MediaFile object");
                    }
                }
                else
                {
                    System.out.println("JVL Metadata Plugin - Args missing MediaFile object");
                }
            }
            else if(event.equalsIgnoreCase("MediaFileImported"))
            {
                System.out.println("JVL Metadata Plugin - Media File Imported Called");
                
                if(args.containsKey("MediaFile"))
                {
                    if(MediaFile.IsMediaFileObject(args.get("MediaFile")))
                    {
                        Show show = new Show(args.get("MediaFile"));
                
                        System.out.println("JVL Metadata Plugin - File name: " + show.GetMediaFile().GetFileName());
                        
                        this.MediaFileImportedHandler(show);
                    }
                    else
                    {
                        System.out.println("JVL Metadata Plugin - Args was not reported as a MediaFile object");
                    }
                }
                else
                {
                    System.out.println("JVL Metadata Plugin - Args missing MediaFile object");
                }
            }
            else if(event.equalsIgnoreCase("MediaFileRemoved"))
            {
                System.out.println("JVL Metadata Plugin - Media File Removed Called");
                
                if(args.containsKey("MediaFile"))
                {
                    if(MediaFile.IsMediaFileObject(args.get("MediaFile")))
                    {
                        Show show = new Show(args.get("MediaFile"));
                
                        System.out.println("JVL Metadata Plugin - File name: " + show.GetMediaFile().GetFileName());
                    }
                    else
                    {
                        System.out.println("JVL Metadata Plugin - Args was not reported as a MediaFile object");
                    }
                }
                else
                {
                    System.out.println("JVL Metadata Plugin - Args missing MediaFile object");
                }
            }
            else if(event.equalsIgnoreCase("ImportingCompleted"))
            {
                System.out.println("JVL Metadata Plugin - Importing Completed");
                this.ImportingCompletedHandler();
            }
            else if(event.equalsIgnoreCase("PlaybackStarted"))
            {
                System.out.println("JVL Metadata Plugin - PlaybackStarted");
            }
            else if(event.equalsIgnoreCase("PlaybackStopped"))
            {
                System.out.println("JVL Metadata Plugin - PlaybackStopped");
            }
            else if(event.equalsIgnoreCase("PlaybackFinished"))
            {
                System.out.println("JVL Metadata Plugin - PlaybackFinished");
            }
            else if(event.equalsIgnoreCase("WatchedStateChanged"))
            {
                System.out.println("JVL Metadata Plugin - Watch State Changed Called");
                
                if(args.containsKey("Airing"))
                {
                    if(Airing.IsAiringObject(args.get("Airing")))
                    {
                        Airing airing = new Airing(args.get("Airing"));
                        WatchedStateChangedHandler(airing);
                        
                        System.out.println("JVL Metadata Plugin - Show: " + airing.GetShow().GetTitle());
                    }
                    else
                    {
                        System.out.println("JVL Metadata Plugin - Args was not reported as Airing object");
                    }
                }
                else
                {
                    System.out.println("JVL Metadata Plugin - Args missing Airing object");
                }
            }
            else
            {
                System.out.println("JVL Metadata Plugin - Unknown/Unregistered event fired: " + event);
            }
        }
        catch(Exception ex)
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler: " + ex.getMessage());
        }
        
    }
    
    public void MediaFileImportedHandler(Show show)
    {
        try 
        {
            System.out.println("MetadataPlugin.MediaFileImportedHandler: " + show.GetMediaFile().GetFileName());
            show.MetadataLookup(true, true);
            
            Watched watched = show.GetWatchedDetails();
            
            //Update the watched status on import to match what we have store in data
            if(watched != null)
            {
                show.GetAiring().SetWatchedStatus(watched.isWatched());
            }
        } 
        catch (SageCallApiException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (SageCallApiException): " + ex.getMessage());
            ex.printStackTrace();
        } 
        catch (IOException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (IOException): " + ex.getMessage());
            ex.printStackTrace();
        } 
        catch (RateLimitException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (RateLimitException): " + ex.getMessage());
        }
    }
    
    public void RecordingStartedHandler(Show show)
    {
        try 
        {
            show.MetadataLookup(true, true);
            
            Watched watched = show.GetWatchedDetails();
            
            //Update the watched status on import to match what we have store in data
            if(watched != null)
            {
                show.GetAiring().SetWatchedStatus(watched.isWatched());
            }
        } 
        catch (SageCallApiException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (SageCallApiException): " + ex.getMessage());
            ex.printStackTrace();
        } 
        catch (IOException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (IOException): " + ex.getMessage());
            ex.printStackTrace();
        }
        catch (RateLimitException ex) 
        {
            System.out.println("JVL Metadata Plugin - Unhandled exception processing event handler (RateLimitException): " + ex.getMessage());
        }
    }
    
    public void ImportingCompletedHandler()
    {
        try
        {
            System.out.println("JVL Metadata Plugin - Unknown File Lookup");
                    
            MediaFiles mediaFiles = MediaFile.GetUnknownFiles();
            
            System.out.println("JVL Metadata Plugin - File count " + mediaFiles.size());
            
            for(int i  = 0; i < mediaFiles.size(); i++)
            {
                MediaFile mediaFile = mediaFiles.get(i);
                
                System.out.println("JVL Metadata Plugin - Unknown File Lookup " + mediaFile.GetFileName());
                
                try
                {
                    mediaFile.GetShow().MetadataLookup(true, true);
                }
                catch(Exception ex1)
                {
                    System.out.println("JVL Metadata Plugin - Unexpected error looking up metadata");
                    ex1.printStackTrace();
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("JVL Metadata Plugin - Unexpected error");
            ex.printStackTrace();
        }
    }
    
    public void WatchedStateChangedHandler(Airing airing)
    {
        try
        {
            Show show = airing.GetShow();
            
            System.out.println("\tTitle: " + show.GetTitle());
            System.out.println("\tSeasonEpisode: " + show.GetSeasonEpisodeString());
            System.out.println("\tWatched: " + airing.IsWatched());
            
            Watched watched = show.GetWatchedDetails();
            
            if(watched != null)
            {
                watched.setWatched(airing.IsWatched());
            }
        }
        catch(Exception ex)
        {
            System.out.println("JVL Metadata Plugin - Unexpected error");
            ex.printStackTrace();
        }
    }

    @Override
    public String[] getConfigValues(String string)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

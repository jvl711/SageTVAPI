
package jvl.metadata;

import java.io.IOException;
import java.util.Map;
import jvl.sage.SageCallApiException;
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
    
    public MetadataPlugin(SageTVPluginRegistry registry) throws IOException
    {
        System.out.println("JVL - Metadata Plugin Constructed");
        this.registry = registry;
        try
        {
            miniserver = new MiniServer(8080);
        }
        catch(Exception ex)
        {
            System.out.println("JVL - Error creating mini server.  Check that the port is available.");
        }
    }
    
    @Override
    public void start() 
    {
        System.out.println("JVL Metadata Plugin - Started");
        this.registry.eventSubscribe(this, "RecordingStarted");
        this.registry.eventSubscribe(this, "MediaFileImported");
        this.registry.eventSubscribe(this, "MediaFileRemoved");
        this.registry.eventSubscribe(this, "ImportingCompleted");
        
        System.out.println("JVL Starting the MiniServer");
        this.miniserver.start();
    }

    @Override
    public void stop() 
    {
        System.out.println("JVL Metadata Plugin - Stopping");
        
        System.out.println("JVL Stopping the MiniServer");
        this.miniserver.stop();
    }

    @Override
    public void destroy() 
    {
        System.out.println("JVL Metadata Plugin - Destroying");
    }

    @Override
    public String[] getConfigSettings() 
    {
        return new String[0];
    }

    @Override
    public String getConfigValue(String string) 
    {
        return "";
    }

    @Override
    public String[] getConfigValues(String string) 
    {
        return new String[0];
    }

    @Override
    public int getConfigType(String string) 
    {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setConfigValue(String string, String string1) 
    {
        
    }

    @Override
    public void setConfigValues(String string, String[] strings) 
    {
        
    }

    @Override
    public String[] getConfigOptions(String string) 
    {
        return new String[0];
    }

    @Override
    public String getConfigHelpText(String string) 
    {
        return "";
    }

    @Override
    public String getConfigLabel(String string) 
    {
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
            show.MetadataLookup(true, true);
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
    
}


package jvl.sage;

import jvl.comskip.Marker;
import jvl.sage.api.MediaFile;
import jvl.sage.api.MediaPlayer;

public class Timebar extends Thread
{
    private MediaFile mediaFile;
    private Marker [] markers;
    private boolean comThreadRun;
    private long sleepCommThread;
    private long sleepOnSkip;
    private long commHitRange;
    
    private static final int DEFAULT_SLEEP_ON_SKIP = 10000;
    private static final int DEFAULT_COMM_HIT_RANGE = 5000;
    

    public Timebar(MediaFile mediaFile) throws SageCallApiException
    {
        this.mediaFile = mediaFile;
        this.markers = mediaFile.GetCommercialMarkers();
        this.comThreadRun = false;
        this.sleepCommThread = 0;
        this.sleepOnSkip = Timebar.DEFAULT_SLEEP_ON_SKIP;
        this.commHitRange = Timebar.DEFAULT_COMM_HIT_RANGE;
    }
    
    
    public long GetDuration() throws SageCallApiException 
    {
        return mediaFile.GetFileEndTime() - mediaFile.GetFileStartTime();
    }
    
    /***
     * Returns current watch time if the file is not currently loaded, otherwise
     * the current media player time - file start time.
     * 
     * @return
     * @throws SageCallApiException 
     */
    public long GetPlaybackTime() throws SageCallApiException
    {
        //This may be something different if it is a live airing. Will look later
        
        if(MediaPlayer.IsMediaPlayerLoaded())
        {
            return MediaPlayer.GetMediaTime() - this.mediaFile.GetFileStartTime();
        }
        else
        {
            //Get the current watch time
            long airingCurrnetWathcedTime = mediaFile.GetAiring().GetWatchedDuration() + mediaFile.GetAiring().GetAiringStartTime();
            
            return airingCurrnetWathcedTime - this.mediaFile.GetFileStartTime();
        }
        
    }
    
    public double GetPlaybackPercent() throws SageCallApiException
    {
        double temp = ((this.GetPlaybackTime() * 1.0) / (this.GetDuration() * 1.0) * 100.0);
        
        return temp;
    }
    
    public long GetPreviousMarker() throws SageCallApiException
    {
        //Take some time off of the GetMediaTime to allow for multiple skip backs
        
        for(int i = markers.length - 1; i >= 0; i--)
        {
            if(markers[i].GetEndTime() < (MediaPlayer.GetMediaTime() - 1500))
            {
                return markers[i].GetEndTime();
            }
            else if(markers[i].GetStartTime() < (MediaPlayer.GetMediaTime() - 1500))
            {
                return markers[i].GetStartTime();
            }
        }
        
        return -1;
    }
    
    public long GetNextMarker() throws SageCallApiException
    {
        for(int i = 0; i < markers.length; i++)
        {
            if(markers[i].GetStartTime() > MediaPlayer.GetMediaTime())
            {
                return markers[i].GetStartTime();
            }
            else if(markers[i].GetEndTime() > MediaPlayer.GetMediaTime())
            {
                return markers[i].GetEndTime();
            }
                
            
        }
        
        return -1;
    }
    
    public long GetNextMarkerEnd() throws SageCallApiException
    {
        for(int i = 0; i < markers.length; i++)
        {
            if(markers[i].GetStartTime() > MediaPlayer.GetMediaTime() || markers[i].GetEndTime() > MediaPlayer.GetMediaTime())
            {
                return markers[i].GetEndTime();
            }
            
        }
        
        return -1;
    }
    
    public void SkipToNextMarker() throws SageCallApiException
    {
        this.SleepCommThread();
        long markerTime = this.GetNextMarker();
        
        if(markerTime > 0 )
        {
            MediaPlayer.Seek(markerTime);
        }
    }
    
    public void SkipToPreviousMarker() throws SageCallApiException
    {
        this.SleepCommThread();
        long markerTime = this.GetPreviousMarker();
        
        if(markerTime > 0 )
        {
            MediaPlayer.Seek(markerTime);
        }
    }
    
    public void SkipToNextMarkerEnd() throws SageCallApiException
    {
        this.SleepCommThread();
        long markerTime = this.GetNextMarkerEnd();
        
        if(markerTime > 0 )
        {
            MediaPlayer.Seek(markerTime);
        }
    }

    private void SleepCommThread()
    {
        this.sleepCommThread = this.sleepOnSkip;
    }
    
    public void StartCommSkipThread()
    {
        if(!comThreadRun)
        {
            comThreadRun = true;
            this.start();
        }
    }
    
    public void StopCommSkipThread()
    {
        comThreadRun = false;
    }
    
    
    @Override
    public void run() 
    {
        System.out.println("jvl.sage.Timebar - Commercial Skipping thread started");
        
        while(comThreadRun)
        {
            try 
            {
                if(this.sleepCommThread <= 0)
                {
                    for(int i = 0; i < markers.length; i++)
                    {
                        if(markers[i].IsHit(MediaPlayer.GetMediaTime(), 5000))
                        {
                            System.out.println("jvl.sage.Timebar - Commercial Hit...  Skipping to end of marker");
                            this.SleepCommThread();
                            MediaPlayer.Seek(markers[i].GetEndTime());

                        }
                    }
                }
                else
                {
                    this.sleepCommThread = this.sleepCommThread - 1000;
                }
                
                Thread.sleep(1000);
            } 
            catch (SageCallApiException ex){ }
            catch(InterruptedException ex2){ }
        }
        
        System.out.println("jvl.sage.Timebar - Commercial Skipping thread stopped");
    }
}


package jvl.comskip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;
import jvl.sage.api.Utility;

/*
* EDL Format appears to be as follows
* StartTime[Tab]EndTime[Tab]MakerType[LF]
* Example
* 0.00 125.53  0
*
*/
public class ComskipFile 
{

    public static final String EDL_EXT = "edl";
    
    MediaFile mediaFile;
    File [] mediaSegments; 
    ArrayList<Marker> markers;
    
    public ComskipFile(Object mediaFile) 
    {
        this.mediaFile = new MediaFile(mediaFile);
        markers = new ArrayList<Marker>();
    }
 
    public ComskipFile(MediaFile mediaFile) 
    {
        this.mediaFile = mediaFile;
    }
    
    public void load() throws SageCallApiException
    {
        mediaSegments = this.mediaFile.GetSegmentFiles();
        long airingStartTime = this.mediaFile.GetAiring().GetScheduleStartTime();
        long airingEndTime = this.mediaFile.GetAiring().GetScheduleEndTime();
        
        for(int i = 0; i < mediaSegments.length; i++)
        {
            String segmentName = mediaSegments[i].getAbsolutePath();
            
            int extIndex = segmentName.lastIndexOf(".");
            
            String edlFileName = segmentName.substring(0, extIndex + 1) + EDL_EXT;
            
            File edlFile = new File(edlFileName);        
            String fileContents = "";
            
            fileContents = Utility.GetFileAsString(edlFile);
            
            if(!fileContents.equals(""))
            {
                String [] lines = fileContents.split("\n");
                long segmentStart = mediaFile.GetStartForSegment(i);
                
                for(int j = 0; j < lines.length; j++)
                {   
                    String [] cuttimes = lines[j].split("\t");

                    long startTime = (long)(Double.parseDouble(cuttimes[0]) * 1000);
                    long endTime = (long)(Double.parseDouble(cuttimes[1]) * 1000);
                   
                    //ToDo: Modify the airingStatrt and end to be scheduled.
                    Marker marker = new Marker(startTime, endTime, segmentStart, airingStartTime, airingEndTime);
                    this.markers.add(marker);
                }                
            }
            else
            {
                System.out.println("Debug - edl file was empty or not found: " + edlFileName);
            }
        }

    }
    
    public boolean hasMarkers()
    {
        return markers.size() > 0;
    }

    @Override
    public String toString()
    {
        String output = "";
        
        for(int i = 0; i < markers.size(); i++)
        {
            output += "Marker " + i + ": ";
            output += " StartTime = " + markers.get(i).GetStartTime();
            output += " EndTime = " + markers.get(i).GetEndTime();
            output += " Marker Start Percent = " + markers.get(i).getAiringStartPercent();
            output += " Marker End Percent = " + markers.get(i).getAiringEndPercent();
            output += " Marker Duration Percent = " + markers.get(i).getMarkerDurationPercent() + "\n";
        }
        
        if(output.length() == 0)
        {
            output = "No Markers!";
        }
        
        return output;
    }
    
}

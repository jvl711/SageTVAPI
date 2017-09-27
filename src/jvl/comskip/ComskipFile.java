
package jvl.comskip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;

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
        
        for(int i = 0; i < mediaSegments.length; i++)
        {
            String segmentName = mediaSegments[i].getAbsolutePath();
            
            System.out.println("Debug - segment file name: " + segmentName);
            
            int extIndex = segmentName.lastIndexOf(".");
            
            
            
            String edlFileName = segmentName.substring(0, extIndex + 1) + EDL_EXT;
            
            System.out.println("Debug - edl file name: " + edlFileName);
            
            File edlFile = new File(edlFileName);        
            
            if(edlFile.exists())
            {

                try
                {
                
                    InputStream is = new FileInputStream(edlFileName);
                    BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                
                    String temp = buf.readLine();
                
                    while(buf.readLine() != null)
                    {
                        String [] cuttimes = temp.split("\t");

                        long startTime = (long)(Double.parseDouble(cuttimes[0]) * 1000);
                        long endTime = (long)(Double.parseDouble(cuttimes[1]) * 1000);
                        long segmentStart = mediaFile.GetStartForSegment(i);
                        
                        Marker marker = new Marker(startTime, endTime, segmentStart);
                        this.markers.add(marker);
                        
                        temp = buf.readLine();
                    }                
                
                }
                catch(Exception ex)
                {
                    System.out.println("Unexpected error reading EDL file");
                }

            }
            else
            {
                System.out.println("Debug - edl file not found: " + edlFileName);
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
            output += "StartTime = " + markers.get(i).GetStartTime();
            output += "EndTime = " + markers.get(i).GetEndTime() + "\n";
        }
        
        if(output.length() == 0)
        {
            output = "No Markers!";
        }
        
        return output;
    }
    
}

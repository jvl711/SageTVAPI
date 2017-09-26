
package jvl.comskip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import jvl.sage.SageCallApiException;
import jvl.sage.api.MediaFile;


public class Comskip 
{

    public static final String EDL_EXT = "edl";
    
    MediaFile mediaFile;
    File [] mediaSegments; 
    ArrayList<File> edlFiles;
    ArrayList<Marker> markers;
    
    public Comskip(Object mediaFile) 
    {
        this.mediaFile = new MediaFile(mediaFile);
        edlFiles = new ArrayList<File>();
        markers = new ArrayList<Marker>();
    }
 
    public Comskip(MediaFile mediaFile) 
    {
        this.mediaFile = mediaFile;
        edlFiles = new ArrayList<File>();
    }
    
    public void load() throws SageCallApiException
    {
        mediaSegments = this.mediaFile.GetSegmentFiles();
        
        for(int i = 0; i < mediaSegments.length; i++)
        {
            String segmentName = mediaSegments[i].getAbsolutePath();
            int extIndex = segmentName.lastIndexOf(".");
            
            String edlFileName = segmentName.substring(0, extIndex + 1) + EDL_EXT;
            
            File edlFile = new File(edlFileName);        
            
            if(edlFile.exists())
            {
                edlFiles.add(edlFile);

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

                        //Create marker record
                        
                        temp = buf.readLine();
                    }
                
                
                }
                catch(Exception ex)
                {
                    System.out.println("Unexpected error reading EDL file");
                }
                
                
                
            }
        }
        
        /*
         * EDL Format appears to be as follows
         * StartTime[Tab]EndTime[Tab]MakerType[LF]
         * Example
         * 0.00 125.53  0
         *
         */
        
        //Parse the edlFiles and create marker records
        
    }
    
    public boolean EDLFileExists() throws SageCallApiException
    {
        return edlFiles.size() > 0;
    }
    
}


package jvl.sage;

import java.io.File;
import java.util.ArrayList;
import jvl.sage.api.MediaFile;


public class Comskip 
{

    public static final String EDL_EXT = "edl";
    
    MediaFile mediaFile;
    File [] mediaSegments; 
    ArrayList<File> edlFiles;
    
    public Comskip(Object mediaFile) 
    {
        this.mediaFile = new MediaFile(mediaFile);
        edlFiles = new ArrayList<File>();
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
            }
        }
        
        
        
    }
    
    public boolean EDLFileExists() throws SageCallApiException
    {
        return edlFiles.size() > 0;
    }
    
}

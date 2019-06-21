/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl.sage.api;

import java.io.File;
import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;

public class Utility extends SageAPI
{
    public static String GetFileAsString(File file) throws SageCallApiException 
    {
        String ret;
        
        ret = SageAPI.callApiString("GetFileAsString", file);
        
        return ret;
    }
    
    public static String GetWorkingDirectory() throws SageCallApiException 
    {
        String ret;
        
        ret = SageAPI.callApiString("GetWorkingDirectory");
        
        return ret;
    }
    
    public static long Time() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("Time");
        
        return ret;
    }
    
    /**
     * Returns a MetaImage object that refers to the specified image file. Used for passing images into Widgets.
     * 
     * @param file the file path of the image to load
     * @return the loaded image object
     */
    public static Object LoadImageFile(File file) throws SageCallApiException
    {
        Object ret;
        
        ret = SageAPI.callApiObject("LoadImageFile", file);
        
        return ret;
    }
    
    /**
     * Returns a MetaImage object that refers to a specified image resource. This can be used to load 
     * images from URLs, JAR resources or the file system. 
     * 
     * It also has a secondary purpose where you can pass it a MetaImage and then it will load that image into the current image cache so it will 
     * render as fast as possible in the next drawing cycle. Good for preloading the next image in a 
     * slideshow. If a MetaImage is passed in; this call will not return until that image is loaded into the cache.
     * 
     * @param resource if this is a MetaImage then the image is loaded into the cache, otherwise its converted to a string and then a MetaImage is returned for that resource
     * @return the MetaImage that refers to the passed specified resource, if a MetaImage was passed in then the same object is returned
     * @throws SageCallApiException 
     */
    public static Object LoadImage(Object resource) throws SageCallApiException
    {
        Object ret;
        
        ret = SageAPI.callApiObject("LoadImage", resource);
        
        return ret;
    }
}

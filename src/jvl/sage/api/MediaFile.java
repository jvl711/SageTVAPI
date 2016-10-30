
package jvl.sage.api;

import jvl.sage.SageObject;


public class MediaFile extends SageObject
{
    private Object mediafile;

    public MediaFile(Object mediafile)
    {
        this.mediafile = mediafile;
    }
    
    @Override
    public Object UnwrapObject() 
    {
        return mediafile;
    }
    
}

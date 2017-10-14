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
    
    public static long Time() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("Time");
        
        return ret;
    }
}

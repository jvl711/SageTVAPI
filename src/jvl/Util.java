/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl;

import java.util.Collection;
import java.util.Map;


public class Util 
{
    
    
    public static String [] CreateDummyArray(int Size)
    {
        String [] temp = new String[Size];
        
        for (int i = 0; i < temp.length; i++)
        {
            temp[i] = (i + 1 ) + "";
        }
    
        return temp;
    }
    
    /**
     * Returns size of Collection, Array, Map or String.  Otherwise it assumes
     * that the object is singular and returns 1
     * 
     * @param data Collection, Array, Map or String
     * @return Size of the collection or 1.
     */
    public static int Size(Object data)
    {
        int ret = 1;
        
        if (data instanceof Collection)
        {
            ret = ((Collection)data).size();
        }
        else if(data instanceof Object[])
        {
            ret = ((Object[])data).length;
        }
        else if(data instanceof Map)
        {
            ret = ((Map)data).size();
        }
        else if(data instanceof String)
        {
            ret = ((String)data).length();
        }
        
        return ret;
    }
    
    /**
     * Returns item at the index of a Collection, Array, Map or String.  
     * Otherwise it will return the object passed.
     * 
     * @param data Collection, Array, Map or String
     * @return Size of the collection or 1.
     */
    public static Object GetElement(Object data, int index)
    {
        Object ret = data;
        
        if (data instanceof Collection)
        {
            ret = ((Collection)data).toArray()[index];   
        }
        else if(data instanceof Object[])
        {
            ret = ((Object[])data)[index];
        }
        else if(data instanceof Map)
        {
            Object [] keyset = ((Map)data).keySet().toArray();
            ret = ((Map)data).get(keyset[index]);
        }
        else if(data instanceof String)
        {
            ret = ((String)data).charAt(index);
        }
        
        return ret;
    }
    
}

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
     * Takes in an Object [] and resizes it to new length starting from the 
     * start index given. It copies all of the data to the new structure.
     * 
     * @param data Object [] to resize
     * @param startIndex Index to start copying from
     * @param length Length of the new object will not exceed this length
     * @return Returns new Object [] of given length
     */
    public static Object GetSubset(Object data, int startIndex, int length)
    {
        Object ret;
        
        if(data instanceof Object[])
        {
            Object [] input = ((Object[])data);
            Object [] output;
            int j = 0;
            
            if((input.length - startIndex) < length)
            {
                output = new Object[input.length - startIndex];
            }
            else
            {
                output = new Object[length];
            }
            
            
            for(int i = startIndex; i < length && i < input.length;  i++)
            {
                output[j] = input[i];
                j++;

            }
            
            ret = output;
        }
        else
        {
            throw new RuntimeException("Unimplemented data type passed to: GetSubset");
        }
        
        return ret;
    }
    
        /**
     * Takes in an Object [] and resizes it to new length starting from the 
     * start index given. It copies all of the data to the new structure.
     * 
     * @param data Object [] to resize
     * @param startIndex Index to start copying from
     * @param minLength Minimum length of the new object
     * @param length The length of the new object will not exceed this length
     * @return Returns new Object [] of given length
     */
    public static Object GetSubset(Object data, int startIndex, int minLength, int length)
    {
        Object ret;
        
        if(data instanceof Object[])
        {
            Object [] input = ((Object[])data);
            Object [] output;
            int j = 0;
            
            if((input.length - startIndex) < minLength)
            {
                output = new Object[minLength];
            }
            else if((input.length - startIndex) < length)
            {
                output = new Object[input.length - startIndex];
            }
            else
            {
                output = new Object[length];
            }
                   
            
            for(int i = startIndex; i < length && i < input.length;  i++)
            {
                output[j] = input[i];
                j++;

            }
            
            ret = output;
        }
        else
        {
            throw new RuntimeException("Unimplemented data type passed to: GetSubset");
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl.sage;


import sage.SageTV;

public abstract class SageArrayObject<T> extends SageAPI
{
    public abstract Object [] UnwrapObject();

    public abstract <T> T Remove(int index);
    
    public abstract <T> T Get(int index);
    
    public abstract void Set(int index, T d);
    
    public abstract void Add(T d);
    
    public abstract int Size();
    
    
    
    /*
    protected Object [] callApi(String method, Object arg1) throws java.lang.reflect.InvocationTargetException
    {
        return callAPI(method, new Object [] {arg1});
    }
    
    protected Object [] callApi(String method) throws java.lang.reflect.InvocationTargetException
    {
        return callAPI(method, null);
    }
    
    protected String callApiString(String method, Object arg1) throws java.lang.reflect.InvocationTargetException
    {
        return callAPIString(method, new Object [] {arg1});
    }
    
    protected String callApiString(String method) throws java.lang.reflect.InvocationTargetException
    {
        return callAPIString(method, null);
    }
    
    private Object [] callAPI(String method, Object [] args) throws java.lang.reflect.InvocationTargetException
    {
        return (Object [])SageTV.api(method, args);
    }
    
    private String callAPIString(String method, Object [] args) throws java.lang.reflect.InvocationTargetException
    {
        return (String)SageTV.api(method, args);
    }
    */
}

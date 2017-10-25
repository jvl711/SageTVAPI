/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl.sage;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public abstract class SageArrayObject<T> extends SageAPI implements List<T>
{
    public ArrayList<T> baseList = new ArrayList<T>();
    
    
    /**
     * @deprecated 
     * @return 
     */
    public abstract Object [] UnwrapObject();

    /**
     * @deprecated 
     * @return 
     */
    //public abstract <T> T Remove(int index);
    
    /**
     * @deprecated 
     * @return 
     */
    //public abstract <T> T Get(int index);
    
    /**
     * @deprecated 
     * @return 
     */
    //public abstract void Set(int index, T d);
    
    /**
     * @deprecated 
     * @return 
     */
    //public abstract void Add(T d);
    
    /**
     * @deprecated 
     * @return 
     */
    //public abstract int Size();
    

    @Override
    public int size() 
    {
        return baseList.size();
    }

    @Override
    public boolean isEmpty() 
    {
        return baseList.isEmpty();
    }

    @Override
    public boolean contains(Object o) 
    {
        return baseList.contains(o);
    }

    @Override
    public Iterator<T> iterator() 
    {
        return baseList.iterator();
    }

    @Override
    public Object[] toArray() 
    {
        return baseList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] ts) 
    {
        return baseList.toArray(ts);
    }

    @Override
    public boolean add(T e) 
    {
        return baseList.add(e);
    }

    @Override
    public boolean remove(Object o) 
    {
        return this.baseList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> clctn) 
    {
        return baseList.containsAll(clctn);
    }

    @Override
    public boolean addAll(Collection<? extends T> clctn) 
    {
        return baseList.addAll(clctn);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> clctn) 
    {
        return this.baseList.addAll(i, clctn);
    }

    @Override
    public boolean removeAll(Collection<?> clctn) 
    {
        return this.baseList.removeAll(clctn);
    }

    @Override
    public boolean retainAll(Collection<?> clctn) 
    {
        return this.baseList.removeAll(clctn);
    }

    @Override
    public void clear() 
    {
        this.baseList.clear();
    }

    @Override
    public T get(int i) 
    {
        return this.baseList.get(i);
    }

    @Override
    public T set(int i, T e) 
    {
        return this.baseList.set(i, e);
    }

    @Override
    public void add(int i, T e) 
    {
        this.baseList.add(i, e);
    }

    @Override
    public T remove(int i) 
    {
        return this.baseList.remove(i);
    }

    @Override
    public int indexOf(Object o) 
    {
        return this.baseList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) 
    {
        return this.baseList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() 
    {
        return this.baseList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int i) 
    {
        return this.baseList.listIterator(i);
    }

    @Override
    public List<T> subList(int i, int i1) 
    {
        return this.baseList.subList(i, i1);
    }
    
}

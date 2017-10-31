
package jvl.sage;




public enum MoviesColumnSort 
{
    TITLE("Title"),
    DATE_ADDED("Date Added"),
    DATE_RELEASED("Date Released");

    private final String name;
    
    private MoviesColumnSort(String name)
    {
        this.name = name;
    }
    
    public String GetName()
    {
        return name;
    }
    
    private static MoviesColumnSort Parser(String name)
    {
        if(TITLE.GetName().equalsIgnoreCase(name))
        {
            return TITLE;
        }
        else if(DATE_ADDED.GetName().equalsIgnoreCase(name))
        {
            return DATE_ADDED;
        }
        else if(DATE_RELEASED.GetName().equalsIgnoreCase(name))
        {
            return DATE_RELEASED;
        }
        else
        {
            return null;
        }
    }
    
    public static MoviesColumnSort GetDefault()
    {
        return TITLE;
    }
    
    @Override
    public String toString()
    {
        return this.GetName();
    }
    
    public static MoviesColumnSort [] GetDisplayValues()
    {
        MoviesColumnSort [] input = MoviesColumnSort.values();
        MoviesColumnSort [] output = new MoviesColumnSort [input.length - 1];
        int outPos = 0;
        
        for(int i = 0; i < input.length; i++)
        {
            
            output[outPos] = input[i];
            outPos++;
            
        }
        
        return output;
    }
    
}
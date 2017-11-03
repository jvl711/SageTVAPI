
package jvl.sage;




public enum MoviesSortColumn 
{
    TITLE("Title"),
    DATE_ADDED("Date Added"),
    YEAR_RELEASED("Year Released");

    private final String name;
    
    private MoviesSortColumn(String name)
    {
        this.name = name;
    }
    
    public String GetName()
    {
        return name;
    }
    
    public static MoviesSortColumn Parse(String name)
    {
        if(TITLE.GetName().equalsIgnoreCase(name))
        {
            return TITLE;
        }
        else if(DATE_ADDED.GetName().equalsIgnoreCase(name))
        {
            return DATE_ADDED;
        }
        else if(YEAR_RELEASED.GetName().equalsIgnoreCase(name))
        {
            return YEAR_RELEASED;
        }
        else
        {
            return null;
        }
    }
    
    public static MoviesSortColumn GetDefault()
    {
        return TITLE;
    }
    
    @Override
    public String toString()
    {
        return this.GetName();
    }
    
    public static MoviesSortColumn [] GetDisplayValues()
    {
        MoviesSortColumn [] input = MoviesSortColumn.values();
        MoviesSortColumn [] output = new MoviesSortColumn [input.length];
        int outPos = 0;
        
        for(int i = 0; i < input.length; i++)
        {
            
            output[outPos] = input[i];
            outPos++;
            
        }
        
        return output;
    }
    
}
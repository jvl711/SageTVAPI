
package jvl.sage;


public enum SortDirection 
{
    ASC("Ascending"),
    DESC("Descending");
    
    private String name;
    
    private SortDirection(String name)
    {
        this.name = name;
    }

    public static SortDirection Parse(String name)
    {
        if(name.toLowerCase().equals(ASC.GetName().toLowerCase()))
        {
            return ASC;
        }
        else if(name.toLowerCase().equals(DESC.GetName().toLowerCase()))
        {
            return DESC;
        }
        else
        {
            return null;
        }
    }
    
    public String GetName()
    {
        return this.name;
    }
    
    public static SortDirection GetDefault()
    {
        return ASC;
    }
    
    @Override
    public String toString()
    {
        return this.GetName();
    }
    
    public static SortDirection [] GetDisplayValues()
    {
        SortDirection [] input = SortDirection.values();
        SortDirection [] output = new SortDirection [input.length - 1];
        int outPos = 0;
        
        for(int i = 0; i < input.length; i++)
        {
            
            output[outPos] = input[i];
            outPos++;
            
        }
        
        return output;
    }
    
}

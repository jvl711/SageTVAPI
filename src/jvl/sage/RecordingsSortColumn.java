
package jvl.sage;

public enum RecordingsSortColumn 
{
    TITLE("Title"),
    DATE_AIRED("Date Aired");

    private final String name;
    
    private RecordingsSortColumn(String name)
    {
        this.name = name;
    }
    
    public String GetName()
    {
        return name;
    }
    
    public static RecordingsSortColumn Parse(String name)
    {
        if(TITLE.GetName().equalsIgnoreCase(name))
        {
            return TITLE;
        }
        else if(DATE_AIRED.GetName().equalsIgnoreCase(name))
        {
            return DATE_AIRED;
        }
        else
        {
            return null;
        }
    }
    
    public static RecordingsSortColumn GetDefault()
    {
        return TITLE;
    }
    
    @Override
    public String toString()
    {
        return this.GetName();
    }
    
    public static RecordingsSortColumn [] GetDisplayValues()
    {
        return RecordingsSortColumn.values();
    }
}

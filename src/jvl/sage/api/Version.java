package jvl.sage.api;


public class Version
{
    private final static String BUILDTIME = "11/15/2021 21:20:47";
    private final static String BUILDNUMBER = "372";
    private final static String VERSION = "3.0";
    
    public static String getVersion()
    {
        return VERSION;
    }
    
    public static String getBuildNumber()
    {
        return BUILDNUMBER;
    }

    public static String getBuildTime()
    {
        return BUILDTIME;
    }
}

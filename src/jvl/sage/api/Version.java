package jvl.sage.api;


public class Version
{
    private final static String BUILDTIME = "04/02/2021 10:29:36";
    private final static String BUILDNUMBER = "24";
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

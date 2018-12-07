
package jvl.sage;

import java.util.ArrayList;
import jvl.sage.api.Shows;


/**
 * This object is meant for paging throgh the recordings by title
 * @author jolewis
 */
public class RecordingsPager 
{
    Shows shows;
    int index;
    ArrayList<String> titles;

    public RecordingsPager(Recordings recordings, String title) throws SageCallApiException
    {
        shows = recordings.GetRecordings();
        titles = shows.GetShowTitles();
    }
}

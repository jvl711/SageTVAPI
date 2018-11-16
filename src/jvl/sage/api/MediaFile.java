
package jvl.sage.api;

import java.io.File;
import java.util.ArrayList;
import jvl.chapterdb.ChapterSearch;
import jvl.chapterdb.ChapterSet;
import jvl.playback.Marker;
import jvl.playback.MarkerType;
import jvl.sage.SageAPI;
import jvl.sage.SageCallApiException;
import jvl.sage.SageObject;


public class MediaFile extends SageObject
{
    private Object mediafile;

    public MediaFile(Object mediafile)
    {
        try
        {
            if(MediaFile.IsMediaFileObject(mediafile))
            {
                this.mediafile = mediafile;
            }
            else if(Airing.IsAiringObject(mediafile))
            {
                this.mediafile = Airing.GetMediaFileForAiring(mediafile);   
            }
            else
            {
                throw new Exception("Unknown object type passed");
            }
        }
        catch(Exception ex)
        {
            throw new RuntimeException("JVL - Error constructing Airing.  The object passed was not an Airing or MediaFile");
        }
        
        this.mediafile = mediafile;
    }

    /**
     * Returns true if the specified object is a MediaFile object. No automatic 
     * type conversion will be performed on the argument. This will return false 
     * if the argument is a MediaFile object, BUT that object no longer exists 
     * in the SageTV database.
     * 
     * @param testObject The object to test
     * @return True if it is a media file object.  False otherwise.
     * @throws SageCallApiException 
     */
    public static boolean IsMediaFileObject(Object testObject) throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsMediaFileObject", testObject);
    }
    
    
    
    /**
     * Returns true if the specified object is a MediaFile object. No automatic 
     * type conversion will be performed on the argument. This will return false 
     * if the argument is a MediaFile object, BUT that object no longer exists 
     * in the SageTV database.
     * 
     * @return True if it is a media file object.  False otherwise.
     * @throws SageCallApiException 
     */
//    public boolean IsMediaFileObject() throws SageCallApiException
//    {
//        return MediaFile.callAPIBoolean("IsMediaFileObject", this.mediafile);
//    }

    public boolean IsVideoFile() throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsVideoFile", this.mediafile);
    }
    
    public boolean IsDVDFile() throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsDVDFile", this.mediafile);
    }
    
    public boolean IsBluRay() throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsBluRay", this.mediafile);
    }
    
    public boolean IsTVFile() throws SageCallApiException
    {
        return MediaFile.callAPIBoolean("IsTVFile", this.mediafile);
    }
    
    public static Object GetMediaFileAiring(Object mediaFile) throws SageCallApiException
    {
        return MediaFile.callApiObject("GetMediaFileAiring", mediaFile);
    }
    
    public Object GetMediaFileAiring() throws SageCallApiException
    {
        return MediaFile.callApiObject("GetMediaFileAiring", this.mediafile);
    }
    
    public String GetRelativePath() throws SageCallApiException
    {
        return MediaFile.callApiString("GetMediaFileRelativePath", mediafile);
    }
    
    public String GetFileName() throws SageCallApiException
    {
        return this.GetMediaFileSegments()[0].GetFileName();
    }
    
    public String GetFilePath() throws SageCallApiException
    {
        return this.GetMediaFileSegments()[0].GetFilePath();
    }
    
    /**
     * This will be the earlier of the Media Start or Scheduled start.
     * 
     * @return Long the time the file was recorded or imported in real time
     * @throws SageCallApiException 
     */
    public long GetMediaStartTime() throws SageCallApiException
    {
        long temp = this.GetFileStartTime();
        
        if(this.GetAiring().GetAiringStartTime() < temp)
        {
            temp = this.GetAiring().GetAiringStartTime();
        }
        
        if(this.GetAiring().GetScheduleStartTime() < temp)
        {
            temp = this.GetAiring().GetScheduleStartTime();
        }
        
        return temp;
    }
    
    //This will be the latest of the Media Start or Scheduled end
    public long GetMediaEndTime() throws SageCallApiException
    {
        long temp = this.GetFileEndTime();
        
        if(this.GetAiring().GetAiringEndTime() > temp)
        {
            temp = this.GetAiring().GetAiringEndTime();
        }
        
        if(this.GetAiring().GetScheduleEndTime() > temp)
        {
            temp = this.GetAiring().GetScheduleEndTime();
        }
            
        return temp;
    }

    public long GetMediaDuration() throws SageCallApiException
    {
        return this.GetMediaEndTime() - this.GetMediaStartTime();
    }
    
    public Show GetShow() throws SageCallApiException
    {
        return new Show(this.UnwrapObject());
    }
    
    public Airing GetAiring()
    {
        return new Airing(this.UnwrapObject());
    }
    
    /*
     * Format.Subtitle.NumStreams
     * Format.Subtitle[.#].Codec
     * Format.Subtitle[.#].Language
     , Format.Subtitle[.#].Index
     , Format.Subtitle[.#].ID
     */
    
    public int GetSubtitleTrackCount() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Subtitle.NumStreams");
        
        //If the string is empty assume no subtitle tracks
        if(ret.equals(""))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(ret);
        }
    }
    
    private String GetSubtitleCode(int index) throws SageCallApiException
    {
        String ret = this.GetMetadata("Format.Subtitle." + index + ".Codec");
        
        return ret;
    }
    
    private String GetSubtitleLanguage(int index) throws SageCallApiException
    {
        String ret = this.GetMetadata("Format.Subtitle." + index + ".Language");
        
        return ret;
    }
    
    public MediaFileSubtitleTrack[] GetSubtitleTracks() throws SageCallApiException
    {
        MediaFileSubtitleTrack[] tracks = new MediaFileSubtitleTrack[this.GetSubtitleTrackCount()];
        
        for(int i = 0; i < this.GetSubtitleTrackCount(); i++)
        {
            tracks[i] = this.GetSubtitleTrack(i);
        }
        
        return tracks;
    }
    
    public MediaFileSubtitleTrack GetSubtitleTrack(int index) throws SageCallApiException
    {
        return GetSubtitleTrack(index, (index + 1) + " - " + this.GetSubtitleLanguage(index) + ", " + this.GetSubtitleCode(index));
    }
    
    public MediaFileSubtitleTrack GetSubtitleTrack(int index, String description) throws SageCallApiException
    {
        if(index < this.GetSubtitleTrackCount() && index >= 0)
        {
            return new MediaFileSubtitleTrack(index, description, this.GetSubtitleCode(index), this.GetSubtitleLanguage(index));
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }
    
   
    
    // <editor-fold defaultstate="collapsed" desc="Audio Track Methods">
    
    /*
    Format.Audio[.#].Codec
    Format.Audio[.#].Channels
    Format.Audio[.#].Language
    Format.Audio[.#].SampleRate
    Format.Audio[.#].BitsPerSample
    Format.Audio[.#].Index
    Format.Audio[.#].ID
    */
    
    public MediaFileAudioTrack[] GetAudioTracks() throws SageCallApiException
    {
        MediaFileAudioTrack [] tracks = new MediaFileAudioTrack[this.GetAudioTrackCount()];
        
        for(int i = 0; i < tracks.length; i++)
        {
            tracks[i] = this.GetAudioTrack(i);
        }
        
        return tracks;
    }
    
    public MediaFileAudioTrack GetAudioTrack(int index) throws SageCallApiException
    {
        
        return this.GetAudioTrack(index, this.GetAudioCodec(index) + " " + this.GetAudioChannels(index));
    }
    
    public MediaFileAudioTrack GetAudioTrack(int index, String description) throws SageCallApiException
    {
        MediaFileAudioTrack track;
        
        if(index > this.GetAudioTrackCount())
        {
            throw new IndexOutOfBoundsException();
        }
        
        track = new MediaFileAudioTrack(index, description, this.GetAudioCodec(index), this.GetAudioChannels(index), this.GetAudioBitrate(index), this.GetAudioSampleRate(index),  this.GetAudioLanguage(index));
        
        return track;
    }
    
    public int GetAudioTrackCount() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Audio.NumStreams");
        
        //If the string is empty assume no audio tracks
        if(ret.equals(""))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(ret);
        }
    }
            
    //TODO: Look deeper into Sage to see why this does not appear to be working
    
//    public String GetAudioBitsPerSample(int tracknum) throws SageCallApiException
//    {
//        String ret;
//        
//        ret = this.GetMetadata("Format.Audio." + tracknum + ".BitsPerSample");
//        
//        return ret;
//    }
    
    private int GetAudioBitrate(int tracknum) throws SageCallApiException
    {
        String ret;
        int intRet = 0;
        
        try
        {
            ret = this.GetMetadata("Format.Audio." + tracknum + ".Bitrate").split(" ")[0];
            intRet = Integer.parseInt(ret) * 1024;
        } 
        catch(Exception ex) { } 
        
        return intRet;
    }
    
    private int GetAudioSampleRate(int tracknum) throws SageCallApiException
    {
        String ret;
        int intRet = 0;
        
        ret = this.GetMetadata("Format.Audio." + tracknum + ".SampleRate");
     
        try
        {
            intRet = Integer.parseInt(ret);
        } 
        catch(Exception ex) { } 
        
        return intRet;
    }
    
    private String GetAudioLanguage(int tracknum) throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Audio." + tracknum + ".Language");
        
        return ret;
    }
    
    private String GetAudioChannels(int tracknum) throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Audio." + tracknum + ".Channels");
        
        return ret;
    }
    
    private String GetAudioCodec(int tracknum) throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Audio." + tracknum + ".Codec");
        
        return ret;
    }
    
    // </editor-fold>
    
    /**
     * Making an assumption that if this media file is VideoFile or DVDFile
     * or IsBluRay.  This is being built in anticipation of multiple video tracks
     * in the future.
     * 
     * @return 1 if VideoFile or DVDFile or BluRay file returns 1
     * @throws SageCallApiException 
     */
    public int GetVideoTrackCount() throws SageCallApiException
    {
        if(this.IsVideoFile() || this.IsDVDFile() || this.IsBluRay())
        {
            return 1;
        }
        
        return 0;
    }
    
    public MediaFileVideoTrack GetVideoTrack(int index) throws SageCallApiException
    {
        if(index >= 0 || index < this.GetVideoTrackCount())
        {
            return new MediaFileVideoTrack(index, this.GetVideoCodec(), this.GetVideoAspect() ,this.GetVideoWidth(), this.GetVideoHeight(), this.GetVideoFPS(), this.GetVideoProgressive());
        }
        else
        {
            throw new IndexOutOfBoundsException();
        }
    }
    
    private boolean GetVideoProgressive() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Resolution");
        
        return ret.equalsIgnoreCase("true");
    }
    
    private String GetVideoResolution() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Resolution");
        
        return ret;
    }
    
    private String GetVideoAspect() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Aspect");
        
        return ret;
    }
    
    /**
     * Returns the bitrate of the video stream
     * @return Bitrate in bytes.  0 means it is undefined
     * @throws SageCallApiException 
     */
    private int GetVideoBitrate() throws SageCallApiException
    {
        String ret;
        int retInt = 0;
        
        try
        {
            ret = this.GetMetadata("Format.Video.Bitrate").split(" ")[0];
            retInt = Integer.parseInt(ret) * 1024 * 1024;
        }
        catch(Exception ex) { }
        
        return retInt;
    }
    
    private String GetVideoCodec() throws SageCallApiException
    {
        String ret;
        
        ret = this.GetMetadata("Format.Video.Codec");
        
        return ret;
    }
    
    private int GetVideoHeight() throws SageCallApiException
    {
        String ret;
        int retInt = 0;
        
        ret = this.GetMetadata("Format.Video.Height");
        
        try
        {
            retInt = Integer.parseInt(ret);
        }
        catch(Exception ex) { }
        
        return retInt;
    }
    
    private int GetVideoWidth() throws SageCallApiException
    {
        String ret;
        int retInt = 0;
        
        ret = this.GetMetadata("Format.Video.Width");
        
        try
        {
            retInt = Integer.parseInt(ret);
        }
        catch(Exception ex) { }
        
        return retInt;
    }
    
    private double GetVideoFPS() throws SageCallApiException
    {
        String ret;
        double retDouble = 0.0;
        
        ret = this.GetMetadata("Format.Video.FPS");
        
        try
        {
            retDouble = Double.parseDouble(ret);
        }
        catch(Exception ex) { }
        
        return retDouble;
    }
    
    /*
     Format.Video.Codec
     Format.Video.Resolution
     Format.Video.Aspect
     Format.Video.Bitrate
     Format.Video.Width
     Format.Video.Height
     Format.Video.FPS
     Format.Video.Interlaced 
     Format.Video.Progressive
     Format.Video.Index
     Format.Video.ID
     Format.Audio.NumStreams
     Format.Audio[.#].Codec
     Format.Audio[.#].Channels
     Format.Audio[.#].Language
     Format.Audio[.#].SampleRate
     Format.Audio[.#].BitsPerSample
     Format.Audio[.#].Index
     Format.Audio[.#].ID
     Format.Subtitle.NumStreams
     Format.Subtitle[.#].Codec
     Format.Subtitle[.#].Language
     Format.Subtitle[.#].Index
     Format.Subtitle[.#].ID
     Format.Container
    */
    
    public String GetMetadata(String name) throws SageCallApiException
    {
        String ret;
        
        ret = SageAPI.callApiString("GetMediaFileMetadata", this.mediafile, name);
        
        return ret;
    }
 
    /**
     * Sets the corresponding metadata property in the MediaFile's format. These are set in 
     * the database and are also exported to the corresponding .properties file for that MediaFile. 
     * When it exports it will append these updates to the .properties file. It will also update 
     * the property "custom_metadata_properties" (which is a semicolon/comma delimited list) 
     * which tracks the extra metadata properties that should be retained. Usage of any of the 
     * following names will update the corresponding Airing/Show object for the MediaFile as well: 
     * Title, Description, EpisodeName, Track, Duration, Genre, Language, RunningTime, Rated, 
     * ParentalRating, PartNumber, TotalParts, HDTV, CC, Stereo, SAP, Subtitled, 3D, DD5.1, 
     * Dolby, Letterbox, Live, New, Widescreen, Surround, Dubbed, Taped, SeasonNumber, 
     * EpisodeNumber Premiere, SeasonPremiere, SeriesPremiere, ChannelPremiere, SeasonFinale, 
     * SeriesFinale, ExternalID, Album, Year, OriginalAirDate, ExtendedRatings, Misc and All "Role" Names
     * 
     * @param name Key to set propperty for
     * @param value Value of the property
     * @throws SageCallApiException 
     */
    public void SetMetadata(String name, String value) throws SageCallApiException
    {
        SageAPI.callApi("SetMediaFileMetadata", mediafile, name, value);
    }
    
    public long GetSize() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetSize", this.mediafile);
        
        return ret;
    }
    
    public boolean IsFileCurrentlyRecording() throws SageCallApiException
    {
        boolean ret;
        
        ret = SageAPI.callAPIBoolean("IsFileCurrentlyRecording", mediafile);
        
        return ret;
    }
    
    public boolean DeleteFile() throws SageCallApiException
    {
        boolean ret;
        
        ret = SageAPI.callAPIBoolean("DeleteFile", mediafile);
        
        return ret;
    }
    
    public File [] GetSegmentFiles() throws SageCallApiException
    {
        File [] ret;
        
        ret = (File [])SageAPI.callApiArray("GetSegmentFiles", mediafile);
        
        return ret;
    }
    
    public long GetStartForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetStartForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    public long GetEndForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetEndForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    
    public long GetDurationForSegment(int segmentIndex) throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetDurationForSegment", mediafile, segmentIndex);
        
        return ret;
    }
    
    public long GetFileStartTime() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetFileStartTime", mediafile);
        
        return ret;
    }
    
    public long GetFileEndTime() throws SageCallApiException
    {
        long ret;
        
        ret = SageAPI.callApiLong("GetFileEndTime", mediafile);
        
        return ret;
    }
    
    public MediaFileSegment [] GetMediaFileSegments() throws SageCallApiException
    {
        File [] files = this.GetSegmentFiles();
        MediaFileSegment [] segments = new MediaFileSegment[files.length];
        
        for(int i = 0; i < files.length; i++)
        {
            MediaFileSegment mfs = new MediaFileSegment(this, i, files[i].getAbsolutePath());
            segments[i] = mfs;
        }
        
        return segments;
    }
    
    
    public void Basic()
    {
        try
        {
            GetChapterMarkers();
        }
        catch(Exception ex)
        {
            System.out.println("JVL: " + ex.getMessage());
        }
    }
    
    public Marker [] GetChapterMarkers() throws SageCallApiException
    {
        long timeAllowance = 120000; //Difference in time that is allowed for an exact match
        
        ChapterSearch search; 
        ArrayList<ChapterSet> chapterSets;
        Marker[] markers = null;
        ChapterSet hit = null;
        
        // First - Look for saved chapter list
        
        // Second - Look for a direct match (Title and Duration)
        try
        {
            System.out.println("JVL - Creating chapter search object");
            search = new ChapterSearch();
            String results = search.Execute(this.GetShow().GetTitle());
            chapterSets = search.ParseResult(results);
        
            System.out.println("JVL - Search returned: " + chapterSets.size());
            
            for(int i = 0; i < chapterSets.size(); i++)
            {
                if(chapterSets.get(i).getTitle().equalsIgnoreCase(this.GetShow().GetTitle()) 
                        || chapterSets.get(i).getTitle().equalsIgnoreCase(this.GetShow().GetTitle() + " (" + this.GetShow().GetYear() + ")"))
                {
                    //See if there is a duration that is within time allowance
                    if((this.GetMediaDuration() - timeAllowance) <= chapterSets.get(i).getSourceDuration() 
                            && (this.GetMediaDuration() + timeAllowance) >= chapterSets.get(i).getSourceDuration())
                    {
                        hit = chapterSets.get(i);
                        System.out.println("Hit found index: " + i);
                        System.out.println("\tTitle: " + chapterSets.get(i).getTitle());
                        System.out.println("\tDuration: " + chapterSets.get(i).getSourceDuration());
                        System.out.println("\tMediaFile Duration: " + this.GetMediaDuration());
                        break;
                    }
                    else
                    {
                        System.out.println("JVL - Chapters...  Not a hit");
                    }
                }
            }
        }
        catch(Exception ex)
        {
            System.out.println("EXCEPTION SEARCHING FOR CHAPTERS! " + ex.getMessage());
        }
        // Third - Look for closest match without going over duration.  May need to use chapter times
        
        
        //Fourth if there is a hit create markers and return them
        
        try
        {
            if(hit != null)
            {
                long startTime = 0;
                String name = "";
                markers = new Marker[hit.getChapterCount()];

                for(int i = 0; i < hit.getChapterCount(); i++)
                {
                    if(i != 0)
                    {
                        Marker marker = new Marker(MarkerType.COMMERCIAL, i - 1, name, startTime, hit.getChapter(i).getDuration() - 1, this.GetFileStartTime(), this.GetMediaStartTime(), this.GetMediaEndTime());
                        markers[i - 1] = marker;
                    }
                    
                    name = hit.getChapter(i).getName();
                    startTime = hit.getChapter(i).getDuration();
                }
                
                Marker marker = new Marker(MarkerType.COMMERCIAL, hit.getChapterCount() - 1, name, startTime, this.GetMediaDuration() - 1, this.GetStartForSegment(0), this.GetMediaStartTime(), this.GetMediaEndTime());
                markers[hit.getChapterCount() - 1] = marker;
            }
            else
            {
                System.out.println("NO CHAPTER HIT!");
            }
        }
        catch(Exception ex)
        {
            System.out.println("Error creating markers! " + ex.getMessage());
        }
        
        return markers;
    }
            
    //GetCommercialMarkers
    
    public Marker [] GetCommercialMarkers() throws SageCallApiException
    {
        MediaFileSegment [] segments = this.GetMediaFileSegments();
        ArrayList<Marker> temp = new ArrayList<Marker>();
        
        for(int i = 0; i < segments.length; i++)
        {
            String fileContents = Utility.GetFileAsString(new File(segments[i].GetEDLFileName()));
            
            if(!fileContents.equals(""))
            {
                String [] lines = fileContents.split("\n");
                
                for(int j = 0; j < lines.length; j++)
                {   
                    String [] cuttimes = lines[j].split("\t");

                    long startTime = (long)(Double.parseDouble(cuttimes[0]) * 1000);
                    long endTime = (long)(Double.parseDouble(cuttimes[1]) * 1000);
                   
                    Marker marker = new Marker(MarkerType.COMMERCIAL, j, startTime, endTime, segments[i].GetStartTime(), this.GetMediaStartTime(), this.GetMediaEndTime());
                    temp.add(marker);
                }                
            }
            else
            {
                System.out.println("Debug - edl file was empty or not found: " + segments[i].GetEDLFileName());
            }
        }
        
        return (Marker [])temp.toArray(new Marker[temp.size()]);
    }
    
    @Override
    public String toString() 
    {
        String output = "";
        
        try
        {
            MediaFileSegment segments [] = this.GetMediaFileSegments();
            output = "";
            
            for(int i = 0; i < segments.length; i++)
            {
                output += "Segment " + i + ": ";
                //output += " StartTime = " + markers[i].GetStartTime();
                //output += " EndTime = " + markers[i].GetEndTime();
                output += " Start Percent = " + segments[i].GetStartPercent();
                output += " End Percent = " + segments[i].GetEndPercent();
                output += " Duration Percent = " + segments[i].GetDurationPercent() + "\n";
            }
        }
        catch(Exception ex)
        {
            
        }
        
        if(output.length() == 0)
        {
            output = "No Segments!\n";
        }
        
        try
        {
            Marker [] markers = this.GetCommercialMarkers();
            //output = "";
            
            for(int i = 0; i < markers.length; i++)
            {
                output += "Marker " + i + ": ";
                //output += " StartTime = " + markers[i].GetStartTime();
                //output += " EndTime = " + markers[i].GetEndTime();
                output += " Start Percent = " + markers[i].GetStartPercent();
                output += " End Percent = " + markers[i].GetEndPercent();
                output += " Duration Percent = " + markers[i].GetDurationPercent() + "\n";
            }
        }
        catch(Exception ex)
        {
            output += " Error getting markers: " + ex.getMessage();
            ex.printStackTrace();
        }
        if(output.length() == 0)
        {
            output = "No Markers!\n";
        }

        return output;
    }
    
    /*
     * Media File Accessor methods.  I am going to start with very specific methods.
     * I may make take a different direction in the future.
     */
    
    /*********************************************************************************************/
    
    public static MediaFiles GetVideoFiles() throws SageCallApiException
    {
        Object [] objects;
        MediaFiles mediaFiles;
        
        objects = MediaFile.callApiArray("GetMediaFiles", "V");
        
        mediaFiles = new MediaFiles(objects);
        
        return mediaFiles;
    }
    
    /**
     * Gets all files that were recorded by SageTV.
     * @return
     * @throws SageCallApiException 
     */
    public static MediaFiles GetRecordingFiles() throws SageCallApiException
    {
        Object [] objects;
        MediaFiles mediaFiles;
        
        objects = MediaFile.callApiArray("GetMediaFiles", "T");
        
        mediaFiles = new MediaFiles(objects);
        
        return mediaFiles;
    }
    
    /**
     * Gets all files that are TV regardless of being recorded or imported.
     * @return TV Files as MediaFiles object
     * @throws SageCallApiException 
     */
    public static MediaFiles GetTVFiles() throws SageCallApiException
    {
        Object [] objects;
        MediaFiles mediaFiles;
        
        objects = MediaFile.callApiArray("GetMediaFiles");
        
        mediaFiles = new MediaFiles(objects);
        
        mediaFiles.FilterByMetadata("MediaType", "TV");
        
        return mediaFiles;
    }
    
    /**
     * Gets all files that are TV regardless of being recorded or imported.
     * @return Movie Files as MediaFiles object
     * @throws SageCallApiException 
     */
    public static MediaFiles GetMovieFiles() throws SageCallApiException
    {
        Object [] objects;
        MediaFiles mediaFiles;
        
        objects = MediaFile.callApiArray("GetMediaFiles");
        
        mediaFiles = new MediaFiles(objects);
        
        mediaFiles.FilterByMetadata("MediaType", "Movie");
        
        return mediaFiles;
    }
    
    /**
     * Gets all files that are not identified as TV/Movie.  They were most likely
     * not identified by the Metadata lookup routine
     * 
     * @return Unknown Files as MediaFiles object
     * @throws SageCallApiException 
     */
    public static MediaFiles GetUnknownFiles() throws SageCallApiException
    {
        Object [] objects;
        MediaFiles mediaFiles;
        
        objects = MediaFile.callApiArray("GetMediaFiles");
        
        mediaFiles = new MediaFiles(objects);
        
        mediaFiles.FilterByMetadata("MediaType", "");
        
        return mediaFiles;
    }
    
    /*********************************************************************************************/
    
    @Override
    public Object UnwrapObject() 
    {
        return mediafile;
    }
    
}

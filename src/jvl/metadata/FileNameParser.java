
package jvl.metadata;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvl.logging.Logging;


public class FileNameParser 
{
    private String fileName;
    private boolean parsed;
    private int year_max; //Max year that will be determined to be a movie
    private int year_min; //Min year that will be determined to be a movie
    
    //Output fields
    private String title;
    private int year;
    private int season;
    private int episode;
    private static final ArrayList<String> ignoreWords;
    
    private static final Logger LOG = Logging.getLogger(FileNameParser.class.getName());
    
    static
    {
        LOG.info("Adding ignore words to the array of ignore words.");
        ignoreWords = new ArrayList();
        ignoreWords.add("1080p".toLowerCase());
        ignoreWords.add("2160p".toLowerCase());
        ignoreWords.add("4K".toLowerCase());
        ignoreWords.add("HDR".toLowerCase());
        ignoreWords.add("HEVC".toLowerCase());
        ignoreWords.add("720p".toLowerCase());
        ignoreWords.add("xvid".toLowerCase());
        ignoreWords.add("BRRip".toLowerCase());
        ignoreWords.add("BluRay".toLowerCase());
        ignoreWords.add("BR-Rip".toLowerCase());
        ignoreWords.add("WebRip".toLowerCase());
        ignoreWords.add("WebDL".toLowerCase());
        ignoreWords.add("DVDRip".toLowerCase());
        ignoreWords.add("x264".toLowerCase());
        ignoreWords.add("h264".toLowerCase());
        ignoreWords.add("x265".toLowerCase());
        ignoreWords.add("h265".toLowerCase());
        ignoreWords.add("ac3".toLowerCase());
        ignoreWords.add("avc1".toLowerCase());
        ignoreWords.add("dts".toLowerCase());
        ignoreWords.add("dts-hd".toLowerCase());
        ignoreWords.add("TRUEFRENCH".toLowerCase());
        ignoreWords.add("UNRATED".toLowerCase());
        LOG.info("Ignore Words: " + ignoreWords.toString());
    }
    
    public FileNameParser(String fileName)
    {
        this.fileName = fileName;
        this.parsed = false;
        this.year_min = 1900;    
        this.year_max = 2100;
        this.title = "";
        this.year = -1;
        this.season = -1;
        this.episode = -1;
        
        this.ParseFileName();
        
    }
    
    public static void AddIgnoreWord(String input)
    {
        //@todo test
        
        if(!ignoreWords.contains(input.toLowerCase()))
        {
            ignoreWords.add(input.toLowerCase());
        }
    }
    
    public int GetSeason()
    {
        return this.season;
    }
    
    public int GetEpisode()
    {
        return this.episode;
    }
    
    public boolean IsMovie()
    {
        //Assume if no season or episode that it is a movie
        if(season == -1 && episode == -1)
        {
            return true;
        }
        
        return false;
    }
    
    public int GetReleaseYear()
    {
        if(!this.parsed)
        {
            this.ParseFileName();
        }
        
        return year;
    }
    
    public String GetTitle()
    {
        if(!this.parsed)
        {
            this.ParseFileName();
        }
    
        return title;        
    }
    
    private void ParseFileName()
    {
        //String input = this.fileName.replace('.', ' ').replace('_', ' ').replace('-', ' ');        
        //I am going to test leaving the dash.  It is breaking filename parsing on BR-Rip often
        String input = this.fileName.replace('.', ' ').replace('_', ' ');        
        String [] parts = input.split(" ");
        String output = "";
        
        System.out.println("Filename: " + this.fileName);
        
        for(int i = 0; i < parts.length; i++)
        {        
            if(this.isIgnoreWord(parts[i]))
            {
                break;
            }
            else if(this.IsDate(parts[i]))
            {
                this.year = ParseDate(parts[i]);
                //break;
            }
            else if(this.IsSeasonEpisode(parts[i]))
            {
                //System.out.println("Is Season/Episode");
                this.season = this.getSeason(parts[i]);
                this.episode = this.GetEpisode(parts[i]);
                break;
            }
            else if(parts[i].equalsIgnoreCase("-"))
            {
                //Do nothing.  This is probably just a delimiter
            }
            else
            {
                if(parts[i].length() > 0)
                {
                    output += parts[i].trim() + " "; 
                }
            }
        }
        
        this.title = output.trim();
        this.parsed = true;
    }

    private boolean isIgnoreWord(String input)
    {
        /*
        if(FileNameParser.ignoreWords.contains(input.toLowerCase()))
        {
            return true;
        }
        */
        
        for(int i = 0; i < FileNameParser.ignoreWords.size(); i++)
        {
            if(input.toLowerCase().contains(FileNameParser.ignoreWords.get(i).toLowerCase()))
            {
                return true;
            }
        }
        
        return false;
        
    }


    //Sesaon 00 denotes "Specials"
    //Out of the gate I think we will support single episodes only.
    //Formats of support.  S01E02, S1E2, 1x02
    //I good place to look for additional examples, which I may support later https://kodi.wiki/view/Naming_video_files/TV_shows
    
    private boolean IsSeasonEpisode(String input)
    {   
        if(input.length() == 0)
        {
            return false;
        }
                
        if(input.charAt(0) == 'S' || input.charAt(0) == 's')
        {
            String season = "";
            String episode = "";
            boolean episodeFound = false;
            
            for(int i = 1; i < input.length(); i++)
            {
                                
                if(input.charAt(i) != 'E' && input.charAt(i) != 'e' && !episodeFound)
                {
                    season += input.charAt(i);                    
                }
                else if(episodeFound)
                {
                    episode += input.charAt(i);
                }
                else
                {
                    episodeFound = true;
                }            
                
            }
            
            try
            {
                int tempSeason = Integer.parseInt(season);                
            }
            catch(Exception ex)
            {
                return false;
            }
            
            try
            {
                int tempEpisode = Integer.parseInt(episode);                
            }
            catch(Exception ex)
            {
                return false;
            }
                        
            return true;
        }
        else if(input.charAt(0) >= 0 || input.charAt(0) <= 9)
        {
            String [] parts = input.split("x");
            
            if(parts.length == 2)
            {
                try
                {
                    int season = Integer.parseInt(parts[0]);                    
                }
                catch(Exception ex)
                {
                    return false;
                }
                
                try
                {
                    int episode = Integer.parseInt(parts[1]);                    
                }
                catch(Exception ex)
                {
                    return false;
                }
                
                return true;
            }
            
            
        }
        
        return false;
        
    }
    
    private int getSeason(String input)
    {
        
        if(input.charAt(0) == 'S' || input.charAt(0) == 's')
        {
            String season = "";
            String episode = "";
            boolean episodeFound = false;
            
            for(int i = 1; i < input.length(); i++)
            {
                                
                if(input.charAt(i) != 'E' && input.charAt(i) != 'e' && !episodeFound)
                {
                    season += input.charAt(i);                    
                }
                else if(episodeFound)
                {
                    episode += input.charAt(i);
                }
                else
                {
                    episodeFound = true;
                }            
                
            }
            
            try
            {
                int tempEpisode = Integer.parseInt(episode);                
            }
            catch(Exception ex)
            {
                return -1;
            }
            
            try
            {
                return Integer.parseInt(season);                
            }
            catch(Exception ex)
            {
                return -1;
            }                                                
            
        }
        else if(input.charAt(0) >= 0 || input.charAt(0) <= 9)
        {
            String [] parts = input.split("x");
            
            if(parts.length == 2)
            {
                
                try
                {
                    int episode = Integer.parseInt(parts[1]);                    
                }
                catch(Exception ex)
                {
                    return -1;
                }
                
                try
                {
                    return season = Integer.parseInt(parts[0]);                    
                }
                catch(Exception ex)
                {
                    return -1;
                }
            }
        }
        
        return -1;
        
    }
    
    private int GetEpisode(String input)
    {
        
        if(input.charAt(0) == 'S' || input.charAt(0) == 's' )
        {
            String season = "";
            String episode = "";
            boolean episodeFound = false;
            
            for(int i = 1; i < input.length(); i++)
            {
                                
                if(input.charAt(i) != 'E' && input.charAt(i) != 'e' && !episodeFound)
                {
                    season += input.charAt(i);                    
                }
                else if(episodeFound)
                {
                    episode += input.charAt(i);
                }
                else
                {
                    episodeFound = true;
                }            
                
            }
            
            try
            {
                int tempSeason = Integer.parseInt(season);
            }
            catch(Exception ex)
            {
                return -1;
            }
            
            try
            {
                return Integer.parseInt(episode);                
            }
            catch(Exception ex)
            {
                return -1;
            }
                        
        }
        else if(input.charAt(0) >= 0 || input.charAt(0) <= 9)
        {
            String [] parts = input.split("x");
            
            if(parts.length == 2)
            {
                try
                {
                    int season = Integer.parseInt(parts[0]);
                }
                catch(Exception ex)
                {
                    return -1;
                }
                
                try
                {
                    return Integer.parseInt(parts[1]);                    
                }
                catch(Exception ex)
                {
                    return -1;
                }
            }
        }
                      
        return -1;
    }
    
    private boolean IsDate(String input)
    {
        //Might have parenthesis on either side
        String clean_input = input;
        
        if(clean_input.length() == 6 && clean_input.charAt(0) == '(' && clean_input.charAt(5) == ')')
        {    
            clean_input = clean_input.replace("(", "");
            clean_input = clean_input.replace(")", "");
        }
        
        try
        {
            int temp = Integer.parseInt(clean_input);
            
            if(temp >= this.year_min && temp <= this.year_max)
            {
                return true;
            }
            else
            {
                return false;
            }
            
        }
        catch(Exception ex) { return false; }
    }
    
    private int ParseDate(String input)
    {
        String clean_input = input;
        
        if(clean_input.length() == 6 && clean_input.charAt(0) == '(' && clean_input.charAt(5) == ')')
        {    
            clean_input = clean_input.replace("(", "");
            clean_input = clean_input.replace(")", "");
        }
        
        try
        {
            int temp = Integer.parseInt(clean_input);
            
            if(temp >= this.year_min && temp <= this.year_max)
            {
                return temp;
            }
            else
            {
                return 0;
            }
            
        }
        catch(Exception ex) { return 0; }
    }
    
}

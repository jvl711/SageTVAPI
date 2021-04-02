
package jvl.metadata;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import jvl.tmdb.model.Configuration;
import jvl.tmdb.model.Episode;
import org.json.JSONObject;

public class Watched
{
    JSONObject json;
    File file;
    
    public boolean isWatched()
    {
        System.out.println("Watched.isWatched called: " + file.getAbsolutePath());
        System.out.println("\tWatched: " + this.json.optBoolean("watched"));
        return this.json.optBoolean("watched");
    }
    
    public void setWatched(boolean watched)
    {
        System.out.println("Watched.GetWatched called: " + file.getAbsolutePath());
        
        System.out.println("\tSetting watched: " + watched);
        
        if(json.has("watched"))
        {
            json.put("watched", watched);
        }
        else
        {
            json.append("watched", watched);
        }
        
        this.save();
    }
    
    public void save()
    {        
        //Delete the file if it exists.
        if(file.exists())
        {
            file.delete();
        }
        
        //Create the parent directories if they do not exist
        if(file.getParentFile() != null)
        {
            file.getParentFile().mkdirs();
        }
        
        PrintStream out;
        
        try
        {
            out = new PrintStream(new FileOutputStream(file, false));
            out.print(this.json.toString());
            out.flush();
            out.close();
        } 
        catch (FileNotFoundException ex)
        {
            System.out.println("Watched.save: Error saving file - " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static Watched constructModel(File file) throws FileNotFoundException, IOException
    {
        BufferedReader reader = null;
        String line = null;
        String data = "";
        JSONObject json;
        
        if(file.exists())
        {
            try
            {
                reader = new BufferedReader(new FileReader(file));

                while((line = reader.readLine()) != null)
                {
                    data += line;
                }

                json = new JSONObject(data);
            }
            finally
            {
                if(reader != null)
                {
                    reader.close();
                }
            }
        }
        else
        {
            //If we do not have an existing file than create the default values
            json = new JSONObject();
            json.append("watched", false);
        }

        Watched watched = new Watched();
        watched.json = json;
        watched.file = file;
        
        return watched;
    }

}

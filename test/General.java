/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import jvl.metadata.FileNameParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jvl711
 */
public class General 
{
    
    
    public General() 
    {
    
    }
    
    @BeforeClass
    public static void setUpClass() 
    {
    }
    
    @AfterClass
    public static void tearDownClass() 
    {
    
    }
    
    @Before
    public void setUp() 
    {
    
    }
    
    @After
    public void tearDown() 
    {
    
    }

    /*
    	Avengers.Infinity.War.2018.1080p.WEBRip.x264-[YTS.AM].mp4
        My.Little.Pony.The.Movie.2017.TRUEFRENCH.BDRip.XviD-PREUMS.avi
        Fantastic.Beasts.and.Where.to.Find.Them.2016.1080p.BRRip.x264.AAC-ETRG.mp4
    	American Pie 1999 UNRATED 720p BRRip x264-MgB.mp4
    	Ghost.In.The.Shell.2017.1080p.BluRay.x264-[YTS.AG].mp4
        UglyDolls_2019_BR-Rip_H264.mkv
        chernobyl.s01e02.internal.1080p.web.h264-memento.mkv
    */
    
    
    @Test
    public void getTitleFromFileName()
    {
        FileNameParser parser;
        String testFileName;
        
        /* Test 1 */
        testFileName = "UglyDolls_2019_BR-Rip_H264.mkv";
        parser = new FileNameParser(testFileName);
        
        Assert.assertEquals("Title", "UglyDolls", parser.GetTitle());
        Assert.assertEquals("Year", 2019, parser.GetReleaseYear());
        
        /* Test 2 */
        testFileName = "Avengers.Infinity.War.2018.1080p.WEBRip.x264-[YTS.AM].mp4";
        parser = new FileNameParser(testFileName);
        
        Assert.assertEquals("Title", "Avengers Infinity War", parser.GetTitle());
        Assert.assertEquals("Year", 2018, parser.GetReleaseYear());
        
        /* Test 3 */
        testFileName = "American Pie 1999 UNRATED 720p BRRip x264-MgB.mp4";
        parser = new FileNameParser(testFileName);
        
        Assert.assertEquals("Title", "American Pie", parser.GetTitle());
        Assert.assertEquals("Year", 1999, parser.GetReleaseYear());
        
        /* Test 4 */
        testFileName = "chernobyl.s01e02.internal.1080p.web.h264-memento.mkv";
        parser = new FileNameParser(testFileName);
        
        Assert.assertEquals("Title", "chernobyl", parser.GetTitle());
        Assert.assertEquals("Season", 01, parser.GetSeason());
        Assert.assertEquals("Episode", 02, parser.GetEpisode());        
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl.mediaformat;

import java.io.File;
import java.util.ArrayList;
import jvl.FFmpeg.jni.AVCodec;
import jvl.FFmpeg.jni.AVCodecParameters;
import jvl.FFmpeg.jni.AVFormatContext;
import jvl.FFmpeg.jni.AVMediaType;
import jvl.FFmpeg.jni.AVStream;
import sage.media.format.AudioFormat;
import sage.media.format.BitstreamFormat;
import sage.media.format.FormatParser;
import sage.media.format.SubpictureFormat;
import sage.media.format.VideoFormat;

/**
 *
 * @author jvl711
 */
public class MediaFormatParserPlugin implements sage.media.format.FormatParserPlugin
{
    AVFormatContext avformat;
    
    
    @Override
    public void initialize(File file) 
    {
        System.out.println("MediaFormatParserPlugin initialize: " + file.getAbsolutePath());
        avformat = AVFormatContext.buildAVFormatContext();
        avformat.openInput(file.getAbsolutePath());
    }

    @Override
    public String getFormatName() 
    {
        System.out.println("Container Format Raw: " + avformat.getFormatName());
        System.out.println("Container Format: " + FormatParser.substituteName(avformat.getFormatName()));
        
        return FormatParser.substituteName(avformat.getFormatName());
    }

    @Override
    public long getDuration() 
    {
        System.out.println("Duuration: " + avformat.getDuration() / 1000);
        
        return (avformat.getDuration() / 1000);
    }

    @Override
    public long getBitrate()
    {
        System.out.println("Bitrate: " + avformat.getBitrate());
        
        return avformat.getBitrate();
    }
    
    @Override
    public BitstreamFormat[] getStreamFormats()
    {
        ArrayList<BitstreamFormat> streams = new ArrayList<BitstreamFormat>();
        
        System.out.println("Number of streams to process: " + avformat.getNumberOfStreams());

        for(int i = 0; i < avformat.getNumberOfStreams(); i++)
        {
            AVCodecParameters avparm = avformat.getAVCodecParameters(i);
            AVCodec avcodec = AVCodec.getAVCodec(avparm);
            AVStream avstream = avformat.getAVStream(i);

            System.out.println(i + " - " + avparm.getCodecType());

            if(avparm.getCodecType() == AVMediaType.VIDEO)
            {
                int arDen =0;
                int arNum =0;
                
                VideoFormat video = new VideoFormat();
                video.setFormatName(FormatParser.substituteName(avcodec.getName()));

                if(avparm.getAspectRatioString().length() > 0)
                {
                    try
                    {
                        arNum = Integer.parseInt(avparm.getAspectRatioString().split(":")[0]);
                        arDen = Integer.parseInt(avparm.getAspectRatioString().split(":")[1]);
                    }
                    catch(Exception ex){}
                }
                
                video.setArDen(arDen);
                video.setArNum(arNum);
                video.setAspectRatio((float)avparm.getAspectRatio());
                video.setFps((float)avstream.getFramerate());
                video.setWidth(avparm.getWidth());
                video.setHeight(avparm.getHeight());
                video.setInterlaced(avparm.getFieldOrder().isInterlaced());
                video.setOrderIndex(i);
                //TODO: Add colorspace
                
                streams.add(video);

            }
            else if(avparm.getCodecType() == AVMediaType.AUDIO)
            {
                AudioFormat audio = new AudioFormat();

                audio.setFormatName(FormatParser.substituteName(avcodec.getName()));
                //audio.setAudioTransport(); TODO: See if I can find this 
                audio.setChannels(avparm.getChannels());
                audio.setSamplingRate(avparm.getSampleRate());
                audio.setBitrate((int)avparm.getBitrate());
                audio.setLanguage(avstream.getLanguage());
                audio.setOrderIndex(i);
                
                streams.add(audio);

            }
            else if(avparm.getCodecType() == AVMediaType.SUBTITLE)
            {
                SubpictureFormat subpicture = new SubpictureFormat();
                
                subpicture.setFormatName(FormatParser.substituteName(avcodec.getName()));
                subpicture.setLanguage(avstream.getLanguage());
                
                subpicture.setOrderIndex(i);
                subpicture.setForced(avstream.isForced());
                
                streams.add(subpicture);
            }
            else if(avparm.getCodecType() == AVMediaType.DATA)
            {

            }
            else if(avparm.getCodecType() == AVMediaType.ATTACHMENT)
            {

            }
            else if(avparm.getCodecType() == AVMediaType.NB)
            {

            }
            else //Unknown
            {

            }
        }
        
        return (BitstreamFormat[])streams.toArray(new BitstreamFormat[0]);
    }
    
    @Override
    public void deconstruct() 
    {
        System.out.println("Deconstruct called");
         avformat.closeInput();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jvl;

import java.awt.image.*;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.*;

public class AdvancedImage 
{

    private String fileNamePath;
    private BufferedImage img;
    
    public AdvancedImage(String fileNamePath) throws java.io.IOException
    {
        this.fileNamePath = fileNamePath;
        img = javax.imageio.ImageIO.read(new File(fileNamePath));
    }
    
    public String GetPath()
    {
        File file = new File(fileNamePath);
        
        return file.getParent();
    }
    
    public AdvancedImage(BufferedImage img, String fileNamePath) 
    {
        this.fileNamePath = fileNamePath;
        this.img = img;
    }
    
    public BufferedImage getImage()
    {
        return img;
    }
    
    public void SaveImageToFile(String fileNamePath) throws java.io.IOException
    {
        if(fileNamePath.toLowerCase().endsWith(".png"))
        {
           ImageIO.write(this.img, "png", new File(fileNamePath));
        }
        else if(fileNamePath.toLowerCase().endsWith(".jpg"))
        {
           ImageIO.write(this.img, "jpg", new File(fileNamePath)); 
        }
        else if (fileNamePath.toLowerCase().endsWith(".jpeg"))
        {
            ImageIO.write(this.img, "jpg", new File(fileNamePath));
        }
    }
    
    public AdvancedImage GetCopyOfImage() 
    {   
        int w = this.getWidth();   
        int h = this.getHeight();
        
        BufferedImage copyImg = new BufferedImage(w, h, img.getType());   
        Graphics2D g = copyImg.createGraphics();
        
        //g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        g.drawImage(this.img, 0, 0, w, h, 0, 0, w, h, null);   
        
        g.dispose();   
        return new AdvancedImage(copyImg, this.fileNamePath);   
    }
    
    public void ResizeCanvas(int sx1, int sy1, int sx2, int sy2)
    {
        int newHeight = sy2 - sy1;
        int newWidth = sx2 - sx1;
        
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, img.getType());   
        Graphics2D g = newImg.createGraphics();   
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        
        g.drawImage(this.img, 0, 0, newWidth, newHeight, sx1, sy1, sx2, sy2, null);
        g.dispose();   
        
        this.img = newImg;
    }

    public void ResizeImage(int newWidth, int newHeight)
    {
        int w = this.getWidth();   
        int h = this.getHeight();
        
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, img.getType());   
        Graphics2D g = newImg.createGraphics();   
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        g.drawImage(this.img, 0, 0, newWidth, newHeight, 0, 0, w, h, null);
        g.dispose();   
        
        this.img = newImg;
    }
    
    public void ResizeImageByWidth(int newWidth, boolean KeepAspect)
    {
        int newHeight = this.getHeight();
        
        if(KeepAspect)
        {
            double ratio = (newWidth / (img.getWidth() * 1.0));
            newHeight = (int)Math.round(img.getHeight() * ratio);
        }
        
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, img.getType());   
        Graphics2D g = newImg.createGraphics();   
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        g.drawImage(this.img, 0, 0, newWidth, newHeight, 0, 0, this.getWidth(), this.getHeight(), null);
        g.dispose();   
        
        this.img = newImg;
    }
    
    public void ResizeImageByHeight(int newHeight, boolean KeepAspect)
    {
        int newWidth = this.getWidth();
        
        if(KeepAspect)
        {
            double ratio = (newHeight / (img.getHeight() * 1.0));
            newWidth = (int)Math.round(img.getWidth() * ratio);
        }
        
        BufferedImage newImg = new BufferedImage(newWidth, newHeight, img.getType());   
        Graphics2D g = newImg.createGraphics();   
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
        g.drawImage(this.img, 0, 0, newWidth, newHeight, 0, 0, this.getWidth(), this.getHeight(), null);
        g.dispose();   
        
        this.img = newImg;
    }
    
    public int getWidth()
    {
        return img.getWidth();
    }
    
    public int getHeight()
    {
        return img.getHeight();
    }
    
}


import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import java.lang.Math;

import java.util.Arrays;

import javax.imageio.*;

public class Gameboyifier {
  private final boolean FORCE_PNG = true;

  private GBPalette palette;

  public Gameboyifier(String path) {
    palette = new GBPalette();

    //GETTING ORIGINAL IMAGE
    BufferedImage img = null;
    img = readImage(path);

    //FILTERING IMAGE
    gameboyify(img);
    
    //WRITING NEW IMAGE
    System.out.println(path);
    String extension = getFileExtension(path);
    String outputFileName = path.substring(0,path.length() - extension.length() - 1) + "GB_" + palette.getLabel() + "." + (FORCE_PNG ? "png" : extension);

    System.out.println(outputFileName);

    writeImage(outputFileName, img);
    System.out.println("Done!");
  }
  
  public GBPalette getGBPalette() {
    return palette;
  }
  
  public void setGBPalette(GBPalette palette) {
    this.palette = palette;
  }
  
  public String getFileExtension(String path) {
    String[] nameParts = path.split("\\.");
    System.out.println(Arrays.toString(nameParts));
    return nameParts[nameParts.length - 1];
  }

  public void gameboyify(BufferedImage img) {
    for(int i = 0; i < img.getWidth(); i++) {
      for(int j = 0; j < img.getHeight(); j++) {
        Color pixelColor = new Color(img.getRGB(i,j));

        double[] cSpaceDistances = new double[4];
        int smallestIndex = 0;
        for(int k = 0; k < cSpaceDistances.length; k++) {
          int redDist = Math.abs(pixelColor.getRed() - palette.getColor(k).getRed());
          int blueDist = Math.abs(pixelColor.getBlue() - palette.getColor(k).getBlue());
          int greenDist = Math.abs(pixelColor.getGreen() - palette.getColor(k).getGreen());
          cSpaceDistances[k] = Math.sqrt((redDist * redDist) + (blueDist * blueDist) + (greenDist * greenDist));

          if(cSpaceDistances[k] < cSpaceDistances[smallestIndex]) {
            smallestIndex = k;
          }
        }
        
        img.setRGB(i,j,palette.getColor(smallestIndex).getRGB());

      }
    }
  }
  
  public BufferedImage readImage(String path) {
    try {
      return ImageIO.read(new File(path));
    } catch (IOException e) {
      System.out.println(e);
      System.exit(1);
    }
    return null;
  }
  
  public void writeImage(String target, BufferedImage img) {
    String extension = getFileExtension(target);
    
    try {
      if(FORCE_PNG) ImageIO.write(img,"png",new File(target));
      else ImageIO.write(img,extension,new File(target));
    } catch (IOException e) {
      System.out.println(e);
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    if(args.length < 1) {
      System.out.println("Usage: java Gameboyifier [path]");
      return;
    }

    new Gameboyifier(args[0]);
  }
}
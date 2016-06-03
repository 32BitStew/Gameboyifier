import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.lang.Math;

import java.util.Arrays;

import javax.imageio.*;

public class Gameboyifier {
  private static final boolean FORCE_PNG = true;

  private GBPalette palette;

  public Gameboyifier() {
    setGBPalette(new GBPalette());
  }

  public Gameboyifier(GBPalette palette) {
    setGBPalette(palette);
  }
  
  public GBPalette getGBPalette() {
    return palette;
  }
  
  public void setGBPalette(GBPalette palette) {
    this.palette = palette;
  }
  
  public String getFileExtension(String filename) {
    String[] nameParts = filename.split("\\.");
    System.out.println(Arrays.toString(nameParts));
    return nameParts[nameParts.length - 1];
  }

  public void gameboyify(BufferedImage img) {
    for(int i = 0; i < img.getWidth(); i++) {
      for(int j = 0; j < img.getHeight(); j++) {
        Color pixelColor = new Color(img.getRGB(i,j));


        // **THE FOLLOWING CODE DEMONSTRATES THE MATH INVOLVED TO ARRIVE AT AND COMPARE THE DISTANCES BETWEEN OUR TARGET PIXEL AND THE COLORS IN OUR PALETTE**
        //
        // double[] cSpaceDistances = new double[4];
        // int smallestIndex = 0;
        // for(int k = 0; k < cSpaceDistances.length; k++) {
        //   int redDist = Math.abs(pixelColor.getRed() - palette.getColor(k).getRed());
        //   int blueDist = Math.abs(pixelColor.getBlue() - palette.getColor(k).getBlue());
        //   int greenDist = Math.abs(pixelColor.getGreen() - palette.getColor(k).getGreen());
        //   cSpaceDistances[k] = Math.sqrt((redDist * redDist) + (blueDist * blueDist) + (greenDist * greenDist));

        //   if(cSpaceDistances[k] < cSpaceDistances[smallestIndex]) {
        //     smallestIndex = k;
        //   }
        // }
        //
        // **HOWEVER, BASED ON THE CONJECTURE "IF sqrt(a^2) > sqrt(b^2), THEN a^2 > b^2 GIVEN THAT a > 0 AND b > 0"**
        // **THE FOLLOWING VERSION ALLOWS FOR COMPARING DISTANCES WITHOUT ACTUALLY CALCULATING THEM**

        int[] componentSquaredSums = new int[4];
        int smallestIndex = 0;
        for(int k = 0; k < componentSquaredSums.length; k++) {
          int redDist = Math.abs(pixelColor.getRed() - palette.getColor(k).getRed());
          int blueDist = Math.abs(pixelColor.getBlue() - palette.getColor(k).getBlue());
          int greenDist = Math.abs(pixelColor.getGreen() - palette.getColor(k).getGreen());
          componentSquaredSums[k] = redDist * redDist + blueDist * blueDist + greenDist * greenDist;

          if(componentSquaredSums[k] < componentSquaredSums[smallestIndex]) {
            smallestIndex = k;
          }
        }

        img.setRGB(i,j,palette.getColor(smallestIndex).getRGB());

      }
    }
  }
  
  public BufferedImage readImage(String filename) {
    try {
      return ImageIO.read(new File(filename));
    } catch (IOException e) {
      System.out.println(e);
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
    }
    return;
  }

  public static void main(String[] args) {
    if(args.length < 1) {
      System.out.println("Usage: java Gameboyifier filename [palette] [-R]");
      return;
    }

    String filename = args[0];
    Path path = Paths.get(filename);
    
    if(args.length > 2 && args[2].equals("-R") && new File(args[0]).isDirectory()) {
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        for(Path p : stream) {
          main(new String[] {p.toString(),args[1],args[2]});
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      return;
    }
    
    if(new File(args[0]).isDirectory()) return;

    Gameboyifier gb = null;
    if(args.length < 2) gb = new Gameboyifier();
    else gb = new Gameboyifier(new GBPalette(args[1]));

    //GETTING ORIGINAL IMAGE
    BufferedImage img = null;
    img = gb.readImage(filename);

    //FILTERING IMAGE
    gb.gameboyify(img);
    
    //WRITING NEW IMAGE
    System.out.println(filename);
    String extension = gb.getFileExtension(filename);
    String outputFileName = filename.substring(0,filename.length() - extension.length() - 1) 
                            + "GB_" + gb.getGBPalette().getLabel() + "." + (Gameboyifier.FORCE_PNG ? "png" : extension);;

    System.out.println(outputFileName);

    gb.writeImage(outputFileName, img);
    System.out.println("Done!");
  }
}
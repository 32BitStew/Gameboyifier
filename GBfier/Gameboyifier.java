import java.io.File;

import javax.imageio.*;

import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;

import java.awt.Color;

import java.lang.Math;

import java.util.Arrays;

public class Gameboyifier {
  private final boolean FORCE_PNG = true;

  private GBPalette palette;

  public Gameboyifier(String path) {
    palette = new GBPalette();

    //GETTING ORIGINAL IMAGE
    BufferedImage img = null;

    try {
      img = ImageIO.read(new File(path));
    } catch (IOException e) {
      System.out.println(e);
      System.exit(1);
    }

    gameboyify(img);
    System.out.println(path);
    String[] nameParts = path.split("\\.");
    System.out.println(Arrays.toString(nameParts));
    String extension = nameParts[nameParts.length - 1];
    String outputFileName = path.substring(0,path.length() - extension.length() - 1) + "GB." + (FORCE_PNG ? "png" : extension);

    System.out.println(outputFileName);
    try {
      if(FORCE_PNG) ImageIO.write(img,"png",new File(outputFileName));
      else ImageIO.write(img,extension,new File(outputFileName));
    } catch (IOException e) {
      System.out.println(e);
      System.exit(1);
    }

    System.out.println("Done!");
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

  public static void main(String[] args) {
    if(args.length < 1) {
      System.out.println("Usage: java Gameboyifier [path]");
      return;
    }

    new Gameboyifier(args[0]);
  }
}
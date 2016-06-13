import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.lang.Math;

public class Gameboyifier {
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
  



}
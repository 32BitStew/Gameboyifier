import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import javax.imageio.*;

public class ImageComparer {
  public static void main(String[] args) {
    if(args.length < 2) {
      System.out.println("Usage: java ImageComparer file1 file2");
      return;
    }

    BufferedImage img1 = null;
    BufferedImage img2 = null;

    try {
      img1 = ImageIO.read(new File(args[0]));
      img2 = ImageIO.read(new File(args[1]));
    } catch (IOException e) {
      System.out.println(e);
    }

    if(img1 == null || img2 == null) {
      System.out.println("One or both files do not exist.");
      return;
    }

    if(img1.getHeight() != img2.getHeight() || img1.getWidth() != img2.getWidth()) {
      System.out.println("Images must be the same size!");
      return;
    }

    BufferedImage img3 = new BufferedImage(img1.getWidth(),img1.getHeight(),BufferedImage.TYPE_BYTE_BINARY);

    for(int r = 0; r < img1.getHeight(); r++) {
      for(int c = 0; c < img1.getWidth(); c++) {
        if(img1.getRGB(c,r) == img2.getRGB(c,r)) {
          img3.setRGB(c,r,Color.WHITE.getRGB());
        }
      }
    }

    try {
      ImageIO.write(img3,"png",new File("output.png"));
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
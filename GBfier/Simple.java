import java.awt.image.BufferedImage;

import java.io.IOException;
import java.io.File;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.*;

public class Simple {
  public static final boolean FORCE_PNG = true;

  private static GBPalette staticPalette;
  private static String rootFilename;
  private static boolean recursive;

  private static void convertAndWrite(String filename, Gameboyifier gb) {
    //GETTING ORIGINAL IMAGE
    BufferedImage img = null;
    img = readImage(filename);

    //FILTERING IMAGE
    gb.gameboyify(img);
    
    //WRITING NEW IMAGE
    System.out.println(filename);
    String extension = getFileExtension(filename);
    String outputFileName = filename.substring(0,filename.length() - extension.length() - 1) 
                            + "GB_" + gb.getGBPalette().getLabel() + "." + (FORCE_PNG ? "png" : extension);;

    System.out.println(outputFileName);

    writeImage(outputFileName, img);
    System.out.println("Done!");
  }

  private static String getFileExtension(String filename) {
    String[] nameParts = filename.split("\\.");
    return nameParts[nameParts.length - 1];
  }

  private static void parseArgs(String[] args) {
    for(int j = 0; j < args.length; j++) {
      if(args[j].charAt(0) == '-') {
        for(int i = 1; i < args[j].length(); i++) {
          //Standalone Options.
          if(args[j].charAt(i) == 'R') {
            recursive = true;
          }
          //Argument Options.
          else if(args[j].charAt(i) == 'p') {
            char option = args[j].charAt(i);
            String param = args[j].substring(i+1);
            if(param.equals("")) {
              if(args.length > j+1) {
                parseParam(args[j+1],option);
                j++;
              } else {
                printUsage();
                System.exit(1);
              }
            } else {
              parseParam(param,option);
              break;
            }
          }
        }
      } else {
        //Normal input. (Checks to make sure there's one.)
        if(rootFilename == null) {
          rootFilename = args[j];
        } else {
          System.out.println("Error: multiple filenames?");
          return;
        }
      }
    }
  }

  private static void parseParam(String param,char option) {
    if(option == 'p') {
      staticPalette = new GBPalette(param);
    } else {
      printUsage();
      System.exit(1);
    }
  }

  private static void printUsage() {
    System.out.println("Usage: Simple [-R] [-p palette] filename");
  }

  private static void traverseRecursively(String filename, Gameboyifier gb) {
    Path path = Paths.get(filename);
    System.out.println(filename);

    File file = new File(filename);

    String[] splitFilename = filename.split(File.separator);
    if(splitFilename[splitFilename.length-1].charAt(0) == '.') return; 

    if(file.isDirectory()) {
      //Go down a level.
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
        for(Path p : stream) {
          traverseRecursively(p.toString(),gb);
        }
      } catch (Exception e) {
        System.out.println("Problem traversing folders: " + e);
      }
    } else {
      //Convert file.
      convertAndWrite(filename,gb);
    }
  }

  public static BufferedImage readImage(String filename) {
    try {
      return ImageIO.read(new File(filename));
    } catch (IOException e) {
      System.out.println("Problem reading image: " + e);
    }
    return null;
  }
  
  public static void writeImage(String target, BufferedImage img) {
    String extension = getFileExtension(target);
    
    try {
      if(FORCE_PNG) ImageIO.write(img,"png",new File(target));
      else ImageIO.write(img,extension,new File(target));
    } catch (IOException e) {
      System.out.println("Problem writing image: " + e);
    }
    return;
  }

  public static void main(String[] args) {
    if(args.length < 1) {
      printUsage();
      return;
    }

    //Initializing variables to null.
    rootFilename = null;
    staticPalette = null;
    recursive = false;

    parseArgs(args);

    //Default behavior.
    if(rootFilename == null) {
      printUsage();
      return;
    }
    if(staticPalette == null) staticPalette = new GBPalette();

    Gameboyifier gb = new Gameboyifier(staticPalette);

    //Recursive mode.
    if(recursive) {
      traverseRecursively(rootFilename,gb);
      return;
    }

    //Single file behavior.
    if(new File(rootFilename).isFile()) {
      convertAndWrite(rootFilename,gb);
    } else {
      System.out.println("Input must be a file OR a directory (if recursive mode is on)");
      return;
    }

    //A GOOD WAY TO TEST
    // System.out.println("MOCK GAMEBOYIFIER");
    // System.out.println("rootFilename: \t" + rootFilename);
    // System.out.println("staticPalette: \t" + staticPalette.getLabel());
    // System.out.println("recursive: \t" + recursive);

  }
}
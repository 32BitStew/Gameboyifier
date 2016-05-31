import java.awt.Color;

public class GBPalette {
  public static final Color[] CLASSIC = {new Color(15,42,14), new Color(39,80,38), new Color(140,170,38), new Color(156,186,41)};
  public static final Color[] POCKET = {Color.BLACK, new Color(82,82,82), new Color(148,148,148), Color.WHITE};
  public static final Color[] AWAKENING = {new Color(6,24,31), new Color(50,102,81), new Color(137,191,115), new Color(224,247,210)};

  private static Color[] palette;
  private static String label;

  public GBPalette() {
    this(CLASSIC);
    this.label = "CLASSIC";
  }

  public GBPalette(Color[] colors) {
    if(colors.length == 4) this.palette = colors;
    
    if(colors == CLASSIC) this.label = "CLASSIC";
    else if(colors == POCKET) this.label = "POCKET";
    else if(colors == AWAKENING) this.label = "AWAKENING";
    else this.label = "unlabeled";
  }

  public GBPalette(Color c0, Color c1, Color c2, Color c3) {
    this.palette = new Color[] {c0,c1,c2,c3};
    this.label = "unlabeled";
  }

  public Color getColor(int x) {
    if(x > 3) return palette[3];
    if(x < 0) return palette[0];
    return palette[x];
  }
  
  public String getLabel() {
    return label;
  }
}
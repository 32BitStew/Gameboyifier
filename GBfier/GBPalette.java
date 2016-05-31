import java.awt.Color;

public class GBPalette {
  private static Color[] palette;

  public GBPalette() {
    this(new Color(15,42,14), new Color(39,80,38), new Color(140,170,38), new Color(156,186,41));
  }

  public GBPalette(Color c0, Color c1, Color c2, Color c3) {
    this.palette = new Color[] {c0,c1,c2,c3};
  }

  public Color getColor(int x) {
    if(x > 3) return palette[3];
    if(x < 0) return palette[0];
    return palette[x];
  }
}
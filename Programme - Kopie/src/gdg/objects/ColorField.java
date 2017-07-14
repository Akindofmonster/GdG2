package gdg.objects;

import java.awt.Color;

public class ColorField {

  private static final int _17 = 17;
  int rStart;
  int gStart;
  int bStart;
  int rEnd;
  int gEnd;
  int bEnd;
  Color[] colors = new Color[_17]; // 17 Farben
  // 86
  // 76
  // 74
  // 71
  // 69
  // 67
  // 66
  // 64
  // 62
  // 60
  // 59
  // 57
  // 55
  // 54
  // 50
  // 44
  // 43

  public ColorField(int rStart, int gStart, int bStart, int rEnd, int gEnd, int bEnd) {
    this.rStart = rStart;
    this.gStart = gStart;
    this.bStart = bStart;
    this.rEnd = rEnd;
    this.gEnd = gEnd;
    this.bEnd = bEnd;

  }

  public int getrStart() {
    return rStart;
  }

  public int getgStart() {
    return gStart;
  }

  public int getbStart() {
    return bStart;
  }

  public int getrEnd() {
    return rEnd;
  }

  public int getgEnd() {
    return gEnd;
  }

  public int getbEnd() {
    return bEnd;
  }

}

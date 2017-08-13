package gdg.objects;

public class ColorField {

  private static final int _17 = 17;
  int rStart;
  int gStart;
  int bStart;
  int alphaStart;
  int rEnd;
  int gEnd;
  int bEnd;
  int alphaEnd;
  
  private int rK =1;
  private int gK =1;
  private int bK =1;
  private int aK =1;
  public Color[] colors = new Color[_17]; // 17 Farben
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

  public ColorField(int rStart, int gStart, int bStart, int alphaStart, int rEnd, int gEnd, int bEnd, int alphaEnd) {
    this.rStart = rStart;
    this.gStart = gStart;
    this.bStart = bStart;
    this.alphaStart = alphaStart;
    this.rEnd = rEnd;
    this.gEnd = gEnd;
    this.bEnd = bEnd;
    this.alphaEnd = alphaEnd;
    int r = Math.abs(rStart - rEnd) / (colors.length);
    int g = Math.abs(gStart - gEnd) / (colors.length);
    int b = Math.abs(bStart - bEnd) / (colors.length);
    int alpha = Math.abs(alphaStart - alphaEnd) / (colors.length);
    if(rStart> rEnd){
    	rK = -1;
    }
    if(gStart> gEnd){
    	gK = -1;
    }
    if(bStart> bEnd){
    	bK = -1;
    }
    if(alphaStart> alphaEnd){
    	aK = -1;
    }
    for (int i = 0; i < colors.length; i++) {
      colors[i] = new Color(rStart + rK*(r * i), gStart + gK*(g * i), bStart + bK*(b * i), alphaStart + aK*(alpha * i));
      // System.out.println(colors[i].alpha);
    }
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

  private Color getCopyColor(int i) {
    Color colorToCopy = colors[i];
    return new Color(colorToCopy.r, colorToCopy.g, colorToCopy.b, colorToCopy.alpha);
  }
  
  public String toString(){
	  String s = "";
	  for(Color c : colors){
		  s+= ":"+c.toString();
	  }
	  return s;
  }

  public Color getColor(int i) {
    switch (i) {
    case 86:
      return getCopyColor(16);
    case 76:
      return getCopyColor(15);
    case 74:
      return getCopyColor(14);
    case 71:
      return getCopyColor(13);
    case 69:
      return getCopyColor(12);
    case 67:
      return getCopyColor(11);
    case 66:
      return getCopyColor(10);
    case 64:
      return getCopyColor(9);
    case 62:
      return getCopyColor(8);
    case 60:
      return getCopyColor(7);
    case 59:
      return getCopyColor(6);
    case 57:
      return getCopyColor(5);
    case 55:
      return getCopyColor(4);
    case 54:
      return getCopyColor(3);
    case 50:
      return getCopyColor(2);
    case 44:
      return getCopyColor(1);
    case 43:
      return getCopyColor(0);
    }
    return null;
  }
}

package gdg.objects;

public class Color {

  public float r;
  public float g;
  public float b;
  public float alpha;

  public Color(float r, float g, float b, float alpha) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.alpha = alpha;
  }
  
  public String toString(){
	  return "["+r+","+g+","+b+","+alpha+"]";
  }
}

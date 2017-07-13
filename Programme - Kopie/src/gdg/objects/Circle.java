package gdg.objects;

import gdg.Equalizer;
import processing.core.*;
import processing.opengl.PShader;

public class Circle {

  private static PApplet canvas;
  private PShape shape;
  public PVector position;
  private int color;

  public float getAlpha() {
    return alpha;
  }

  public void setAlpha(int alpha) {
    this.alpha = alpha;
  }

  public int getColorWithoutAlpha() {
    return colorWithoutAlpha;
  }

  public void setColorWithoutAlpha(int colorWithoutAlpha) {
    this.colorWithoutAlpha = colorWithoutAlpha;
  }

  private float alpha;
  private int colorWithoutAlpha;
  public Note node;
  public float radius;
  public float scale;
  public boolean draw = true;

  /**
   * Constructor for the Circle. Creates a PShape.
   *
   * @param papa
   *          The PApplet to draw on
   * @param position
   *          The position of the circle
   * @param radius
   *          The radius of the circle
   * @param color
   *          The color of the circles stroke
   */
  public Circle(PApplet papa, PVector position, float radius, float weight, int color, int alpha, Note node) {
    this.canvas = papa;
    this.color = papa.color(color, alpha);
    this.alpha = alpha;
    this.colorWithoutAlpha = color;
    this.scale = 1;
    this.node = node;
    this.radius = radius;
    this.position = position;

    // Shapes bring the advantage of being easily modifiable and adjustable
    papa.fill(this.color);
    shape = canvas.createShape(PConstants.ELLIPSE, 0, 0, radius, radius);
    // shape.beginShape();
    // shape.fill(color);
    // shape.noStroke();
    // shape.endShape();
    // TODO set stroke weight
    // TODO set fill method
  }

  /**
   * Displays the shape on the stored static canvas object.
   */
  public void display() {

    if (draw) {
      // canvas.filter(new PShader());
      canvas.pushMatrix();
      canvas.translate(position.x, position.y);
      canvas.shape(shape);
      if (Equalizer._vergroesserm) {
        shape.scale(scale);
      }
      // shape.setStroke(draw);
      // shape.setStroke(canvas.color(getColorWithoutAlpha(), alpha / 4));
      // shape.setStrokeWeight(shape.getStrokeWeight(0) * scale / 4);

      canvas.popMatrix();
    }
  }

  public int getColor() {
    return color;
  }

  public void setColor(int color, float alpha) {
    this.color = canvas.color(color, alpha);
    this.colorWithoutAlpha = color;
    this.alpha = alpha;
    shape.setFill(getColor());
  }
}
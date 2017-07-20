package gdg.objects;

import gdg.Equalizer;
import processing.core.*;
import processing.opengl.PShader;

public class Circle {

  private static PApplet canvas;
  private PShape shape;
  public PVector position;
  public Color color;

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
  public Circle(PApplet papa, PVector position, float radius, float weight, Color color, Note node) {
    this.canvas = papa;
    this.color = color;
    this.scale = 1;
    this.node = node;
    this.radius = radius;
    this.position = position;

    // Shapes bring the advantage of being easily modifiable and adjustable
    int colortest = papa.color(color.r, color.g, color.b, color.alpha);
    // System.out.println(colortest + " alpha:" + color.alpha);
    papa.fill(colortest);
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
    // System.out.println("draw:" + draw + " alpha:" + color.alpha);
    if (color.alpha < 0) {
      draw = false;
    }
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

  public void setColor(Color color) {
    this.color = color;
    shape.setFill(canvas.color(color.r, color.g, color.b, color.alpha));
  }
}
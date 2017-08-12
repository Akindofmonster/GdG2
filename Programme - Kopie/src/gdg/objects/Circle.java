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
  public boolean spawn = true;
  public float spawnCount = 1;
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

      // if (spawn) {
      // if (spawnCount == 1) {
      // shape.scale(0.5f);
      // } else {
      // float f = 1.08f;// + initScale * spawnCount;
      // shape.scale(f);
      // }
      //
      // spawnCount++;
      // if (spawnCount > 10) {
      // System.out.println("eteteref");
      // spawn = false;
      // scale = 1;
      // }
      // } else
      if (Equalizer._vergroessern) {
        shape.scale(scale);
        // System.out.println(scale);
      }
      canvas.translate(position.x, position.y);
      if (spawn) {
        shape.setFill(
            canvas.color(color.r, color.g, color.b, ((int) color.alpha / (canvas.frameRate / 5)) * spawnCount));

        spawnCount++;
        if (spawnCount > canvas.frameRate / 5) {
          // System.out.println("eteteref");
          spawn = false;
        }
      }
      canvas.shape(shape);
      // shape.setStroke(draw);
      // shape.setStroke(canvas.color(getColorWithoutAlpha(), alpha / 4));
      // shape.setStrokeWeight(shape.getStrokeWeight(0) * scale / 4);

      canvas.popMatrix();
    }
  }

  public void setColor(Color color) {
    this.color = color;
    shape.setFill(canvas.color(color.r, color.g, color.b, (int) color.alpha));
  }
}
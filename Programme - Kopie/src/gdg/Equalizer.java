package gdg;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import gdg.objects.Circle;
import gdg.objects.Note;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.sun.prism.paint.Color;

/**
 * The Equalizer class is the main controller for the Animation.
 *
 * @author Oliver Haerer, Niklas Schmid
 */
public class Equalizer extends PApplet {


  private static int Height = 900;
  private static int Wide = 1820;

  private static boolean _verblassen = false;
  public static boolean _vergroesserm = false;
  private static boolean _verschieben = false;
  private static boolean _wiederhohlen = false;

  private static final int AddScale = (Height + Wide) / 700;
  private static final float PosFactor = AddScale;
  private static final int Duration = 10000;
  private static final float AlphaScale = 1.2f;
  // TODO FARBE

  protected static boolean SONGPLAYING;
  private static final int _60FPS = 60;
  private static final int _10_60 = 10;
  private long lastMove;
  // A list of circles to hold the elements to display.
  private ArrayList<Circle> circles;
  private ArrayList<Note> notes;
  int counter = 0;
  int count = 0;
  long lastMilliseconds = 0;
  long timer = 0;
  long time;

  private AudioPlayer song;
  private FFT fft;

  /**
   * Main method to instantiate the PApplet.
   *
   * @param args
   *          Arguments passed to the PApplet.
   */
  public static void main(String[] args) {
    try {
      System.setOut(new PrintStream("out.txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    PApplet.main(new String[] { Equalizer.class.getName() });
  }

  /**
   * The settings() method runs before the sketch has been set up, so other
   * Processing functions cannot be used at this point.
   */
  @Override
  public void settings() {
    fullScreen();
    Height = displayHeight;
    Wide = displayWidth;
    // setSize(Wide, Height);
    smooth();
  }

  /**
   * The setup method is called before displaying any objects. The Method calls
   * commands in the Processing API.
   */
  @Override
  public void setup() {

    frameRate(1000);
    background(0);
    lastMove = System.currentTimeMillis();
    circles = new ArrayList<Circle>();
    notes = new ArrayList<Note>();
    createNotes();

    // Start the song right away

    Minim minim = new Minim(this);
    String path = ""// +Paths.get("").toAbsolutePath().toString().replace('\\',
                    // '/')
        // + "./gdg/objects/"
        + "03_m___e.s.t___seven_days_of_falling___evening_in_atlantis.mp3";
    System.out.println(path);
    song = minim.loadFile(path);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    song.play();
    SONGPLAYING = true;
    time = System.currentTimeMillis();

  }
  // TODO Processing demo library demo particals

  /**
   * Draw method is responsible for displaying all objects.
   */
  @Override
  public void draw() {

    background(0);
    noStroke();
    // if (counter <= 0) {
    // counter = 10;
    // count++;
    // System.out.println(count);
    // fill(color(255, 255, 255, 50));
    // PShape shape = createShape(PConstants.RECT, 0, 0, Wide, Height);
    // int time = song.position();
    // if (frameCount % 1000 == 0) {
    timer += (System.currentTimeMillis() - this.time);
    this.time = System.currentTimeMillis();

    double timeDifference = (((_60FPS / frameRate) * _10_60) + 6) / 2;
    println(frameRate + " Framedifference " + (timer - lastMilliseconds) + "Time " + timer + " TimeDifference "
        + timeDifference/* / (millis() / 1000f) */);
    lastMilliseconds = timer;
    // }
    for (Note n : notes) {
      // println("Note with timestamp " + n.timestamp + " lastadded " +
      // n.getLastadded());
      if (n.getTimeStamp() >= timer - timeDifference && n.getTimeStamp() <= timer + timeDifference
          && n.notAdded(timer)) {
        // println("ZeitAbstand: " + n.getTimeStamp() + ":" + timer);
        float randomX = random(Wide / 5, Wide - Wide / 5);
        float randomY = random(Height / 5, Height - Height / 5);
        int alpha = 150;
        int color = 255;

        circles.add(new Circle(this, new PVector(randomX, randomY), n.GetSize() * 5, 10, color, alpha, n));
        System.out.println("Circle added");
        n.setLastadded(timer);// TODO change to bool, change apperence in
                              // mousePressed Methode
      }
    }

    ArrayList toRemove = new ArrayList<>();
    // Display every circle available
    for (Circle c : circles) {
      if (c.draw) {
        transformCircle(timer, c);
        // c.update(jitter);
        c.display();
      } else {
        // toRemove.add(c);
      }
    }
    // circles.removeAll(toRemove);

    if ((System.currentTimeMillis() - lastMove) <= 1000) {
      stroke(255, 255, 255);
      float position = map(song.position(), 0, song.length(), 0, width);
      line(position, height - height / 70, position, height);
    }
    //
    // text("Click anywhere to jump to a position in the song.", 10, 20, 255);
    checkRestartSong();
  }

  private void transformCircle(long time2, Circle c) {
    if (_verblassen) {
      float alpha = c.getAlpha() - AlphaScale;
      c.setColor(c.getColorWithoutAlpha(), alpha);
    }
    // if (_vergroesserm) {
    int tempRadius = (int) (c.radius + AddScale);
    c.scale = tempRadius / c.radius;
    // }
    if (_verschieben) {
      float x = c.position.x;
      float y = c.position.y;
      float x2 = c.position.x;
      float y2 = c.position.y;
      // if (c.position.x >= Wide / 2) {
      x += ((x * (c.scale - 1)) / PosFactor);
      // System.out.println("x increased");
      // } else {
      // x -= ((x * (c.scale - 1)) / PosFactor);
      // // System.out.println("x decreased");
      // }
      // if (c.position.y >= Height / 2) {
      y += ((y * (c.scale - 1)) / PosFactor);
      // System.out.println("y increased");
      // } else {
      // y -= ((y * (c.scale - 1)) / PosFactor);
      // // System.out.println("y increased");
      // }
      // System.out.println(x + ":" + x2 + " " + y + ":" + y2);
      c.position = new PVector(x, y);
    }
    if (timer - c.node.getTimeStamp() >= Duration && (_verblassen || _vergroesserm || _verschieben)) {
      // System.out
      // .println("Timer " + timer + " c.node.getTimeStamp() " +
      // c.node.getTimeStamp() + " Duration " + Duration);
      c.draw = false;
    }

    // System.out.println("Circle" + c.radius);
    // fill(c.getColor());
    noStroke();

  }

  @Override
  public void mouseMoved() {
    if (mouseY >= height - height / 10)
      lastMove = System.currentTimeMillis();
    super.mouseMoved();
  }

  @Override
  public void mousePressed() {
    if (mouseY >= height - height / 40) {
      restartSong();
      // choose a position to cue to based on where the user clicked.
      // the length() method returns the length of recording in milliseconds.
      int position = (int) (map(mouseX, 0, width, 0, song.length()));
      for (Note n : notes) {
        n.setLastadded(-60);
      }
      timer = position;
      song.cue(position);
      circles = new ArrayList<Circle>();
    }
    super.mousePressed();
  }

  public void checkRestartSong() {
    if (!song.isPlaying() && _wiederhohlen) {
      Thread s = new Thread(new Runnable() {

        @Override
        public void run() {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          Equalizer.SONGPLAYING = true;

        }
      });
      if (SONGPLAYING)
        restartSong();
    }
  }

  private void restartSong() {
    {
      this.song.play(0);
      this.timer = 0;
      this.time = System.currentTimeMillis();
      circles = new ArrayList<Circle>();
    }
  }

  private void createNotes() {
    notes.add(new Note(0.068027211, 74, 0.692244898));
    notes.add(new Note(0.068027211, 69, 0.692244898));
    notes.add(new Note(0.068027211, 66, 0.692244898));
    notes.add(new Note(0.068027211, 59, 0.692244898));
    notes.add(new Note(0.068027211, 62, 0.692244898));
    notes.add(new Note(0.068027211, 54, 0.692244898));

    notes.add(new Note(2.274489796, 76, 0.615306122));
    notes.add(new Note(2.274489796, 64, 0.615306122));
    notes.add(new Note(2.274489796, 67, 0.615306122));
    notes.add(new Note(2.274489796, 59, 0.615306122));
    notes.add(new Note(2.274489796, 69, 0.615306122));
    notes.add(new Note(2.274489796, 55, 0.615306122));

    notes.add(new Note(5.580045351, 69, 0.954920635));

    notes.add(new Note(6.546575964, 74, 1.008616780));
    notes.add(new Note(6.546575964, 62, 1.008616780));
    notes.add(new Note(6.546575964, 69, 1.008616780));
    notes.add(new Note(6.546575964, 59, 1.008616780));
    notes.add(new Note(6.546575964, 54, 1.008616780));
    notes.add(new Note(6.546575964, 57, 1.008616780));

    notes.add(new Note(8.790068027, 76, 0.506485261));
    notes.add(new Note(8.790068027, 64, 0.506485261));
    notes.add(new Note(8.790068027, 59, 0.506485261));
    notes.add(new Note(8.790068027, 60, 0.506485261));

    notes.add(new Note(10.610068027, 60, 0.371519274));

    notes.add(new Note(14.037913832, 86, 0.600816327));
    notes.add(new Note(14.037913832, 69, 0.600816327));
    notes.add(new Note(14.037913832, 59, 0.600816327));
    notes.add(new Note(14.037913832, 66, 0.600816327));
    notes.add(new Note(14.037913832, 62, 0.600816327));
    notes.add(new Note(14.037913832, 57, 0.600816327));
    notes.add(new Note(14.037913832, 54, 0.600816327));

    notes.add(new Note(16.178503401, 76, 0.300408163));
    notes.add(new Note(16.178503401, 64, 0.300408163));
    notes.add(new Note(16.178503401, 67, 0.300408163));
    notes.add(new Note(16.178503401, 59, 0.300408163));
    notes.add(new Note(16.178503401, 60, 0.300408163));

    notes.add(new Note(20.218775510, 71, 0.963628118));
    notes.add(new Note(20.218775510, 59, 0.963628118));
    notes.add(new Note(20.218775510, 64, 0.963628118));
    notes.add(new Note(20.218775510, 57, 0.963628118));

    notes.add(new Note(25.512925170, 62, 1.179863946));
    notes.add(new Note(25.512925170, 69, 1.179863946));
    notes.add(new Note(25.512925170, 57, 1.179863946));

    notes.add(new Note(27.514195011, 64, 1.232108844));
    notes.add(new Note(27.514195011, 76, 1.232108844));
    notes.add(new Note(27.514195011, 69, 1.232108844));
    notes.add(new Note(27.514195011, 67, 1.232108844));
    notes.add(new Note(27.514195011, 59, 1.232108844));
    notes.add(new Note(27.514195011, 57, 1.232108844));

    notes.add(new Note(31.325170068, 62, 1.560090703));
    notes.add(new Note(31.325170068, 57, 1.560090703));
    notes.add(new Note(31.325170068, 69, 1.560090703));
    notes.add(new Note(31.325170068, 44, 1.560090703));
    notes.add(new Note(31.325170068, 55, 1.560090703));

    notes.add(new Note(33.711020408, 64, 1.071020408));
    notes.add(new Note(33.711020408, 62, 1.071020408));
    notes.add(new Note(33.711020408, 76, 1.071020408));
    notes.add(new Note(33.711020408, 59, 1.071020408));
    notes.add(new Note(33.711020408, 55, 1.071020408));
    notes.add(new Note(33.711020408, 43, 1.071020408));

    notes.add(new Note(37.831111111, 59, 1.114557823));
    notes.add(new Note(37.831111111, 62, 1.114557823));
    notes.add(new Note(37.831111111, 69, 1.114557823));
    notes.add(new Note(37.831111111, 50, 1.114557823));
    notes.add(new Note(37.831111111, 43, 1.114557823));

    notes.add(new Note(38.584716553, 62, 1.191473923));

    notes.add(new Note(40.177777778, 76, 0.779319728));
    notes.add(new Note(40.177777778, 69, 0.779319728));
    notes.add(new Note(40.177777778, 64, 0.779319728));
    notes.add(new Note(40.177777778, 71, 0.779319728));

    notes.add(new Note(44.823219955, 76, 0.920090703));
    notes.add(new Note(44.823219955, 71, 0.920090703));
    notes.add(new Note(44.823219955, 64, 0.920090703));
    notes.add(new Note(44.823219955, 59, 0.920090703));
  }
}

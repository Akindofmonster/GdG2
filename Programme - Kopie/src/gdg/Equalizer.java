package gdg;

import ddf.minim.AudioPlayer;
import processing.opengl.*;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import gdg.objects.Circle;
import gdg.objects.Color;
import gdg.objects.ColorField;
import gdg.objects.Note;
import gdg.objects.Enums.GAMESTATE;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * The Equalizer class is the main controller for the Animation.
 *
 * @author Oliver Haerer, Niklas Schmid
 */
public class Equalizer extends PApplet {

  private static final int _120 = 120;
  private static int Height = 900;
  private static int Wide = 1820;

  private static boolean _verblassen = true;
  public static boolean _vergroesserm = true;
  private static boolean _verschieben = false;
  private static boolean _wiederhohlen = false;
  private static ColorField colorField = new ColorField(200, 0, 0, 100, 255, 150, 0, 150);

  private static final float AddScale = (Height + Wide) / 700;
  private static final float PosFactor = AddScale;
  private static final int Duration = 10000;
  private static final float AlphaScale = 0.8f;

  public float getAddscale() {
    return AddScale * (_120 / frameRate);
  }

  public float getPosfactor() {
    return PosFactor * (_120 / frameRate);
  }

  public float getAlphascale() {
    return AlphaScale * (_120 / frameRate);
  }

  private GAMESTATE state = GAMESTATE.MENUE;
  protected static boolean SONGPLAYING;
  private static final int _60FPS = 60;
  private static final int _10_60 = 10;
  private long lastMove;
  // A list of circles to hold the elements to display.
  private ArrayList<Circle> circles;
  private ArrayList<Note> notes;
  PShader blurH, blurV;

  PShader blur;
  int counter = 0;
  int count = 0;
  long lastMilliseconds = 0;
  long actualTime = 0;
  long lastTime;
  boolean startSong = true;

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
    fullScreen(P2D);
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

    frameRate(200);
    background(0);
    lastMove = System.currentTimeMillis();
    circles = new ArrayList<Circle>();
    notes = new ArrayList<Note>();
    createNotes();

    blur = loadShader("blur.glsl");
    initBlurH();
    initBlurV();

    // Start the song right away

    Minim minim = new Minim(this);
    String path = ""// +Paths.get("").toAbsolutePath().toString().replace('\\',
                    // '/')
        // + "./gdg/objects/"
        + "03_m___e.s.t___seven_days_of_falling___evening_in_atlantis.mp3";
    System.out.println(path);
    song = minim.loadFile(path);
    // try {
    // Thread.sleep(2000);
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }
  // TODO Processing demo library demo particals

  /**
   * Draw method is responsible for displaying all objects.
   */
  @Override
  public void draw() {
    if (state == GAMESTATE.PAUSED) {
      song.pause();
      this.lastTime = System.currentTimeMillis();
      fill(255, 255, 255);
      textSize(32 / (height / 1080));
      textAlign(CENTER, CENTER);
      text("Paused", 0, 0, width, height);

    } else if (state == GAMESTATE.MENUE) {
      background(0);
      fill(255, 255, 255);
      textSize(32 / (height / 1080));
      textAlign(CENTER, CENTER);
      text("Press ENTER to start", 0, 0, width, height);

    } else if (state == GAMESTATE.RUNNING) {
      if (startSong) {
        background(0);
        song.play();
        SONGPLAYING = true;
        lastTime = System.currentTimeMillis();
        startSong = false;
      }
      actualTime += (System.currentTimeMillis() - this.lastTime);
      this.lastTime = System.currentTimeMillis();
      if (!song.isPlaying() && song.position() < song.length()) {
        background(0);
        song.play(song.position());
        song.cue((int) actualTime);
      }
      // background(0);
      noStroke();

      // filter(blur);

      // filter(blurH);
      // filter(blurV);

      // if (counter <= 0) {
      // counter = 10;
      // count++;
      // System.out.println(count);
      // fill(color(255, 255, 255, 50));
      // PShape shape = createShape(PConstants.RECT, 0, 0, Wide, Height);
      // int time = song.position();
      // if (frameCount % 1000 == 0) {
      double timeDifference = (((_60FPS / frameRate) * _10_60) + 2);/// 2;
      System.out.println(frameRate + " Framedifference " + (actualTime - lastMilliseconds) + "Time " + actualTime
          + " TimeDifference " + timeDifference/* / (millis() / 1000f) */);
      lastMilliseconds = actualTime;
      // }
      for (Note n : notes) {
        // println("Note with timestamp " + n.timestamp + " lastadded " +
        // n.getLastadded());
        if (n.getTimeStamp() >= actualTime - timeDifference && n.getTimeStamp() <= actualTime + timeDifference
            && n.notAdded(actualTime)) {
          // println("ZeitAbstand: " + n.getTimeStamp() + ":" + timer);
          float randomX = random(Wide / 5, Wide - Wide / 5);
          float randomY = random(Height / 5, Height - Height / 5);
          Color color = colorField.getColor((int) n.frequentcy);
          // System.out.println("Alpha:" + color.alpha + " freq:" + (int)
          // n.frequentcy);

          circles.add(new Circle(this, new PVector(randomX, randomY), n.GetSize() * 5, 10, color, n));
          System.out.println("Circle added");
          n.setLastadded(actualTime);// TODO change to bool, change apperence in
          // mousePressed Methode
        }
      }

      ArrayList toRemove = new ArrayList<>();
      // Display every circle available
      for (Circle c : circles) {
        if (c.draw) {
          transformCircle(actualTime, c);
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
  }

  private void transformCircle(long time2, Circle c) {
    if (_verblassen) {
      c.color.alpha -= getAlphascale();
      c.setColor(c.color);
    }
    // if (_vergroesserm) {
    int tempRadius = (int) (c.radius + getAddscale());
    c.scale = tempRadius / c.radius;
    // }
    if (_verschieben) {
      float x = c.position.x;
      float y = c.position.y;
      float x2 = c.position.x;
      float y2 = c.position.y;
      // if (c.position.x >= Wide / 2) {
      x += ((x * (c.scale - 1)) / getPosfactor());
      // System.out.println("x increased");
      // } else {
      // x -= ((x * (c.scale - 1)) / PosFactor);
      // // System.out.println("x decreased");
      // }
      // if (c.position.y >= Height / 2) {
      y += ((y * (c.scale - 1)) / getPosfactor());
      // System.out.println("y increased");
      // } else {
      // y -= ((y * (c.scale - 1)) / PosFactor);
      // // System.out.println("y increased");
      // }
      // System.out.println(x + ":" + x2 + " " + y + ":" + y2);
      c.position = new PVector(x, y);
    }
    if (actualTime - c.node.getTimeStamp() >= Duration && (_verblassen || _vergroesserm || _verschieben)) {
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
  public void keyPressed() {
    if (state == GAMESTATE.PAUSED) {
      if (key == ' ') {
        state = GAMESTATE.RUNNING;
      }
    } else if (state == GAMESTATE.MENUE) {
      if (key == ENTER) {
        state = GAMESTATE.RUNNING;
      }
    } else if (state == GAMESTATE.RUNNING) {
      if (key == ' ') {
        state = GAMESTATE.PAUSED;
      } else if (key == BACKSPACE || key == ENTER) {
        state = GAMESTATE.MENUE;
        startSong = true;
      }
    }
    super.keyPressed();
  }

  @Override
  public void mousePressed() {
    if (mouseY >= height - height / 40) {
      background(0);
      restartSong();
      // choose a position to cue to based on where the user clicked.
      // the length() method returns the length of recording in milliseconds.
      int position = (int) (map(mouseX, 0, width, 0, song.length()));
      for (Note n : notes) {
        n.setLastadded(-60);
      }
      actualTime = position;
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
      for (Note n : notes) {
        n.setLastadded(-60);
      }
      this.song.play(0);
      this.actualTime = 0;
      this.lastTime = System.currentTimeMillis();
      circles = new ArrayList<Circle>();
    }
  }

  void initBlurH() {
    String[] vertSource = { "uniform mat4 transform;",

        "attribute vec4 vertex;", "attribute vec2 texCoord;",

        "varying vec2 vertTexCoord;",

        "void main() {", "vertTexCoord = texCoord;", "gl_Position = transform * vertex;", "}" };
    String[] fragSource = { "uniform sampler2D texture;", "uniform vec2 texOffset;",

        "varying vec2 vertTexCoord;",

        "void main() {",
        "gl_FragColor  = 0.27901 * texture2D(texture, vec2(vertTexCoord.x - texOffset.x, vertTexCoord.y));",
        "gl_FragColor += 0.44198 * texture2D(texture, vertTexCoord);",
        "gl_FragColor += 0.27901 * texture2D(texture, vec2(vertTexCoord.x + texOffset.x, vertTexCoord.y));", "}" };
    blurH = new PShader(this, vertSource, fragSource);
  }

  void initBlurV() {
    String[] vertSource = { "uniform mat4 transform;",

        "attribute vec4 vertex;", "attribute vec2 texCoord;",

        "varying vec2 vertTexCoord;",

        "void main() {", "vertTexCoord = texCoord;", "gl_Position = transform * vertex;", "}" };
    String[] fragSource = { "uniform sampler2D texture;", "uniform vec2 texOffset;",

        "varying vec2 vertTexCoord;",

        "void main() {",
        "gl_FragColor  = 0.27901 * texture2D(texture, vec2(vertTexCoord.x, vertTexCoord.y - texOffset.y));",
        "gl_FragColor += 0.44198 * texture2D(texture, vertTexCoord);",
        "gl_FragColor += 0.27901 * texture2D(texture, vec2(vertTexCoord.x, vertTexCoord.y + texOffset.y));", "}" };
    blurV = new PShader(this, vertSource, fragSource);
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
    // notes.add(new Note(14.037913832, 62, 0.600816327));
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

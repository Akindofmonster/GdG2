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

import controlP5.Bang;
import controlP5.ColorPicker;
import controlP5.ControlP5;

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
  private static boolean _blur = false;
  private static ColorField colorField = new ColorField(100, 0, 0, 100, 255, 100, 0, 150);

  private static float Scale = 0.8f;
  private static float Pos = 0.25f;
  private static float AlphaScale = 0.8f;
  private static int Duration = 10000;
  private static int _distance = 10;

  private static float AddScale = Scale * ((Height + Wide) / 700);
  private static float PosFactor = AddScale * (1 / Pos);

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

  ControlP5 cp5;
  ArrayList main = new ArrayList<>();
  ArrayList farben = new ArrayList<>();
  ArrayList gesch = new ArrayList<>();
  ArrayList vorlagen = new ArrayList<>();
  ArrayList info = new ArrayList<>();
  boolean firstm = true;
  boolean firstf = true;
  boolean firstg = true;
  boolean firstv = true;
  boolean firsti = true;

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
    cp5 = new ControlP5(this);

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
      size(1920, 1080);
      noStroke();

      buildMain();

      // textSize(32 / (height / 1080));
      // textAlign(CENTER, CENTER);
      // text("Press ENTER to start", 0, 0, width, height);

    } else if (state == GAMESTATE.RUNNING) {
      System.out.println(cp5.isVisible());

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
      noStroke();
      if (_blur && !(_verblassen || _vergroesserm || _verschieben)) {
        // filter(blur);

        filter(blurH);
        filter(blurV);
      } else {
        background(0);
      }

      // if (counter <= 0) {
      // counter = 10;
      // count++;
      // System.out.println(count);
      // fill(color(255, 255, 255, 50));
      // PShape shape = createShape(PConstants.RECT, 0, 0, Wide, Height);
      // int time = song.position();
      // if (frameCount % 1000 == 0) {
      double timeDifference = actualTime - lastMilliseconds;// (((_60FPS / frameRate) * _10_60) + 2);/// 2;

      // System.out.println(frameRate + " Framedifference " + (actualTime -
      // lastMilliseconds) + "Time " + actualTime
      // + " TimeDifference " + timeDifference/* / (millis() / 1000f) */);

      lastMilliseconds = actualTime;
      // }
      for (Note n : notes) {
        // System.out.println("ZeitAbstand: " + n.getTimeStamp() + ":" + actualTime +
        // "+-" + timeDifference);
        if (n.getTimeStamp() >= actualTime - timeDifference && n.getTimeStamp() <= actualTime + timeDifference
            && n.notAdded(actualTime)) {
          // System.out.println("ZeitAbstand: " + n.getTimeStamp() + ":" + actualTime +
          // "+-" + timeDifference);
          float randomX = random(Wide / _distance, Wide - Wide / _distance);
          float randomY = random(Height / _distance, Height - Height / _distance);
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

  private void buildMain() {
    if (firstm) {
      // Play
      cp5.addButton("Play").setPosition(100, 300).setSize(480, 480);
      // Farben
      cp5.addButton("Farben").setPosition(1300, 200).setSize(400, 100);
      // Geschwindigkeit
      cp5.addButton("Geschwindigkeit").setPosition(1300, 350).setSize(400, 100);
      // Vorlagen
      cp5.addButton("Vorlagen").setPosition(1300, 500).setSize(400, 100);
      // Informationen
      cp5.addButton("Informationen").setPosition(1350, 700).setSize(350, 100);
      // Beenden
      cp5.addButton("Beenden").setPosition(1350, 850).setSize(350, 100);
      firstm = false;
    }
  }

  private void buildFarben() {
    cp5.addButton("FZurueck").setLabel("Zurück").setPosition(1200, 200).setSize(100, 50);
    cp5.addTextlabel("FarbenT").setText("Farben").setPosition(1350, 200).setFont(createFont("Arial", 30));

    cp5.addTextlabel("Farbe1").setText("Farbe 1").setPosition(1200, 300).setFont(createFont("Arial", 20));

    cp5.addTextlabel("Farbe2").setText("Farbe 2").setPosition(1500, 300).setFont(createFont("Arial", 20));
    // ColorPicker
    cp5.addColorPicker("C1").setPosition(1200, 350).setColorValue(color(255, 255, 255, 255));

    cp5.addColorPicker("C2").setPosition(1500, 350).setColorValue(color(255, 255, 255, 255));
    cp5.addButton("FUebernehmen").setLabel("Übernehmen").setPosition(1400, 600).setSize(350, 100);
  }

  private void buildGeschw() {
    cp5.addButton("GZurueck").setLabel("Zurück").setPosition(1200, 200).setSize(100, 50);
    cp5.addTextlabel("GeschwindigkeitT").setText("Geschwindigkeit").setPosition(1350, 200)
        .setFont(createFont("Arial", 30));

    cp5.addSlider("vergroessern").setLabel("vergrößern").setPosition(1200, 300).setSize(450, 20).setRange(0, 100)
        .setArrayValue(new float[] { 0, 0 });

    cp5.addSlider("verblassen").setPosition(1200, 375).setSize(450, 20).setRange(0, 100)
        .setArrayValue(new float[] { 0, 0 });

    cp5.addSlider("verschwimmen").setPosition(1200, 450).setSize(450, 20).setRange(0, 100)
        .setArrayValue(new float[] { 0, 0 });

    cp5.addButton("GUebernehmen").setLabel("Übernehmen").setPosition(1400, 600).setSize(450, 100);
  }

  private void buildVorlagen() {
    cp5.addButton("VZurueck").setLabel("Zurück").setPosition(1200, 200).setSize(100, 50);
    cp5.addTextlabel("VorlagenT").setText("Vorlagen").setPosition(1350, 200).setFont(createFont("Arial", 30));
    cp5.addButton("Vorlage1").setLabel("Vorlage 1").setPosition(1200, 300).setSize(550, 100);
    cp5.addTextlabel("Vorlag1T").setText("Sonnenuntergang: Rote unbewegliche Kreise mit Blur").setPosition(1200, 420)
        .setFont(createFont("Arial", 12));
    cp5.addButton("Vorlage2").setLabel("Vorlage 2").setPosition(1200, 500).setSize(550, 100);
    cp5.addTextlabel("Vorlag2T").setText("Schnesturm: Wei?e schnelle Kreise").setPosition(1200, 620)
        .setFont(createFont("Arial", 12));
    cp5.addButton("Vorlage3").setLabel("Vorlage 3").setPosition(1200, 700).setSize(550, 100);
    cp5.addTextlabel("Vorlag3T").setText("Frühling: Grüne langsam größer werdende Kreise").setPosition(1200, 820)
        .setFont(createFont("Arial", 12));
  }

  private void buildInfo() {
    cp5.addButton("IZurueck").setLabel("Zurück").setPosition(1200, 200).setSize(100, 50);
    cp5.addTextlabel("InformationenT").setText("Informationen").setPosition(1350, 200).setFont(createFont("Arial", 30));
    cp5.addTextlabel("TitelT").setText("Titel").setPosition(1200, 300).setFont(createFont("Arial", 30));
    cp5.addTextlabel("Titel").setText("Evening in Atlantis").setPosition(1250, 340).setFont(createFont("Arial", 20));
    cp5.addTextlabel("AlbumT").setText("Album").setPosition(1200, 400).setFont(createFont("Arial", 30));
    cp5.addTextlabel("Album").setText("Seven Days of Falling").setPosition(1250, 440).setFont(createFont("Arial", 20));
    cp5.addTextlabel("KuenstlerT").setText("Künstler").setPosition(1200, 500).setFont(createFont("Arial", 30));
    cp5.addTextlabel("Kuenstler").setText("EST (Esbjörn Svensson Trio").setPosition(1250, 540)
        .setFont(createFont("Arial", 20));
    cp5.addTextlabel("VeroeffentlichtT").setText("Veröffentlicht").setPosition(1200, 600)
        .setFont(createFont("Arial", 30));
    cp5.addTextlabel("Veroeffentlicht").setText("2003").setPosition(1250, 640).setFont(createFont("Arial", 20));
    cp5.addTextlabel("GruppeT").setText("Gruppe").setPosition(1200, 800).setFont(createFont("Arial", 30));
    cp5.addTextlabel("Gruppe").setText("Oliver Härer, Niklas Schmid").setPosition(1250, 840)
        .setFont(createFont("Arial", 20));
  }

  public void Play(int i) {
    removeMain();
    state = GAMESTATE.RUNNING;
  }

  public void Farben(int i) {
    // removeMain();
    buildFarben();
  }

  public void Geschwindigkeit(int i) {
    // removeMain();
    buildGeschw();
  }

  public void Vorlagen(int i) {
    // removeMain();
    buildVorlagen();
  }

  public void Informationen(int i) {
    // removeMain();
    buildInfo();

  }

  public void Beenden(int i) {
    exit();
  }

  public void FZurueck(int i) {
    // removeFarben();
    buildMain();
  }

  public void GZurueck(int i) {
    // removeGeschw();
    buildMain();
  }

  public void VZurueck(int i) {
    // removeVorlagen();
    buildMain();
  }

  public void IZurueck(int i) {
    // removeInfo();
    buildMain();
  }

  private void removeMain() {

    // System.out.println(cp5.isVisible());
    cp5.get("Play").hide();
    cp5.get("Farben").hide();
    cp5.get("Geschwindigkeit").hide();
    cp5.get("Vorlagen").hide();
    cp5.get("Informationen").hide();
    cp5.get("Beenden").hide();
    cp5.hide();
    System.out.println("hide");
    // System.out.println(cp5.isVisible());
  }

  private void removeFarben() {
    cp5.get("FZurueck").remove();
    cp5.get("FarbenT").remove();
    cp5.get("Farbe1").remove();
    cp5.get("Farbe2").remove();
    cp5.get("C1").remove();
    cp5.get("C2").remove();
    cp5.get("Uebernehmen").remove();
  }

  private void removeGeschw() {
    cp5.get("GZurueck").remove();
    cp5.get("GeschwindigkeitT").remove();
    cp5.get("vergroessern").remove();
    cp5.get("verblassen").remove();
    cp5.get("verschwimmen").remove();
    cp5.get("GUebernehmen").remove();
  }

  private void removeVorlagen() {
    cp5.get("VZurueck").remove();
    cp5.get("VorlagenT").remove();
    cp5.get("Vorlage1T").remove();
    cp5.get("Vorlage1").remove();
    cp5.get("Vorlage2T").remove();
    cp5.get("Vorlage2").remove();
    cp5.get("Vorlage3T").remove();
    cp5.get("Vorlage3T").remove();
  }

  private void removeInfo() {
    cp5.get("IZurueck").remove();
    cp5.get("InformationenT").remove();
    cp5.get("TitelT").remove();
    cp5.get("Titel").remove();
    cp5.get("AlbumT").remove();
    cp5.get("Album").remove();
    cp5.get("KuenstlerT").remove();
    cp5.get("Kuenstler").remove();
    cp5.get("VeroeffentlichtT").remove();
    cp5.get("Veroeffentlicht").remove();
    cp5.get("GruppeT").remove();
    cp5.get("Gruppe").remove();
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
      if (c.position.x >= Wide / 2) {
        x += ((x * (c.scale - 1)) / getPosfactor());
        // System.out.println("x increased");
      } else {
        x -= (((x + Wide) * (c.scale - 1)) / PosFactor);
        // // System.out.println("x decreased");
      }
      if (c.position.y >= Height / 2) {
        y += ((y * (c.scale - 1)) / getPosfactor());
        // System.out.println("y increased");
      } else {
        y -= (((y + Height) * (c.scale - 1)) / PosFactor);
        // System.out.println("y increased");
      }
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
    if (mouseY >= height - height / 10 && !_blur)
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
    if (state == GAMESTATE.MENUE) {

    } else if (mouseY >= height - height / 40) {
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

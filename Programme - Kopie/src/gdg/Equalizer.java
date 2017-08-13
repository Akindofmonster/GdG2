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
import processing.core.PFont;
import processing.core.PShape;
import processing.core.PVector;
import processing.opengl.PShader;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.ArrayList;

import controlP5.Button;
import controlP5.ColorPicker;
import controlP5.ControlEvent;
import controlP5.ControlFont;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.Slider;
import controlP5.Textlabel;
import controlP5.Toggle;

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
  public static boolean _vergroessern = true;
  private static boolean _verschieben = false;
  private static boolean _wiederholen = false;
  private static boolean _blur = true;

  static int R1 = 100;
  static int G1 = 0;
  static int B1 = 0;
  static int A1 = 100;

  static int R2 = 255;
  static int G2 = 100;
  static int B2 = 0;
  static int A2 = 150;
  private static ColorField colorField = new ColorField(R1, G1, B1, A1, R2, G2, B2, A2);

  private static float Scale = 0.8f;
  private static float Pos = 0.25f;
  private static float AlphaScale = 0.8f;
  private static int Duration = 10000;
  private static float _distance = 10;

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

  ArrayList<Button> main = new ArrayList<Button>();

  ArrayList<Textlabel> farbenl = new ArrayList<Textlabel>();
  ArrayList<ColorPicker> farbenc = new ArrayList<ColorPicker>();
  ArrayList<Button> farbenb = new ArrayList<Button>();

  ArrayList<Textlabel> geschl = new ArrayList<Textlabel>();
  ArrayList<Slider> geschs = new ArrayList<Slider>();
  ArrayList<Toggle> gescht = new ArrayList<Toggle>();
  ArrayList<Button> geschb = new ArrayList<Button>();

  ArrayList<Button> vorlagenb = new ArrayList<Button>();
  ArrayList<Textlabel> vorlagenl = new ArrayList<Textlabel>();

  ArrayList<Button> info = new ArrayList<Button>();
  ArrayList<Textlabel> infol = new ArrayList<Textlabel>();

  int tempR1 = 100;
  int tempG1 = 0;
  int tempB1 = 0;
  int tempA1 = 100;

  int tempR2 = 255;
  int tempG2 = 100;
  int tempB2 = 0;
  int tempA2 = 150;

  float tempVergroessern = 1.5f;
  float tempVerblassen = 0;
  float tempVerschieben = 0;
  long tempDauer = 10000;
  float tempAbstand = 10;

  boolean tempBlur = true;
  boolean tempReplay = false;

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

    buildMain();
    buildFarben();
    buildGeschw();
    buildInfo();
    buildVorlagen();

    uebernehmen();

    showMain();
    removeFarben();
    removeGeschw();
    removeInfo();
    removeVorlagen();

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
      // size(1920, 1080);
      noStroke();

      // buildMain();
      // showMain();
      // removeFarben();
      // removeGeschw();
      // removeInfo();
      // removeVorlagen();

      // textSize(32 / (height / 1080));
      // textAlign(CENTER, CENTER);
      // text("Press ENTER to start", 0, 0, width, height);

    } else if (state == GAMESTATE.RUNNING) {
      System.out.println(cp5.isVisible());

      if (startSong) {
        background(0);
        song.play();
        SONGPLAYING = true;
        actualTime = 0;
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
      if (_blur && !(_verblassen || _vergroessern || _verschieben)) {
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
      double timeDifference = actualTime - lastMilliseconds;// (((_60FPS /
      // frameRate)
      // * _10_60)
      // + 2);///
      // 2;

      // System.out.println(frameRate + " Framedifference " + (actualTime
      // -
      // lastMilliseconds) + "Time " + actualTime
      // + " TimeDifference " + timeDifference/* / (millis() / 1000f) */);

      lastMilliseconds = actualTime;
      // }
      for (Note n : notes) {
        // System.out.println("ZeitAbstand: " + n.getTimeStamp() + ":" +
        // actualTime +
        // "+-" + timeDifference);
        if (n.getTimeStamp() >= actualTime - timeDifference && n.getTimeStamp() <= actualTime + timeDifference
            && n.notAdded(actualTime)) {
          // System.out.println("ZeitAbstand: " + n.getTimeStamp() +
          // ":" + actualTime +
          // "+-" + timeDifference);
          float randomX = random(Wide / _distance, Wide - Wide / _distance);
          float randomY = random(Height / _distance, Height - Height / _distance);
          Color color = colorField.getColor((int) n.frequentcy);
          // System.out.println("Alpha:" + color.alpha + " freq:" +
          // (int)
          // n.frequentcy);

          circles.add(new Circle(this, new PVector(randomX, randomY), n.GetSize() * 5, 10, color, n));
          System.out.println("Circle added");
          n.setLastadded(actualTime);// TODO change to bool, change
          // apperence in
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
      // text("Click anywhere to jump to a position in the song.", 10, 20,
      // 255);
      checkRestartSong();
    }
  }

  private void buildMain() {

    // Play
    main.add(cp5.addButton("Play").setPosition((float) (width / 19.2), (float) (height / 3.6))
        .setSize((int) (width / 3), (int) (height / 2)));
    // Farben
    main.add(cp5.addButton("Farben").setPosition((float) (width / 1.5), (float) (height / 5.4))
        .setSize((int) (width / 4.8), (int) (height / 10.88)));
    // Einstellungen
    main.add(cp5.addButton("Einstellungen").setPosition((float) (width / 1.5), (float) (height / 3.08571))
        .setSize((int) (width / 4.8), (int) (height / 10.8)));
    // Vorlagen
    main.add(cp5.addButton("Vorlagen").setPosition((float) (width / 1.5), (float) (height / 2.16))
        .setSize((int) (width / 4.8), (int) (height / 10.8)));
    // Informationen
    main.add(cp5.addButton("Informationen").setPosition((float) (width / 1.5), (float) (height / 1.54286))
        .setSize((int) (width / 4.8), (int) (height / 10.8)));
    // Beenden
    main.add(cp5.addButton("Beenden").setPosition((float) (width / 1.5), (float) (height / 1.27059))
        .setSize((int) (width / 4.8), (int) (height / 10.8)));

  }

  private void buildFarben() {
    farbenb.add(cp5.addButton("FZurueck").setLabel("zurueck").setPosition((float) (width / 1.6), (float) (height / 5.4))
        .setSize((int) (width / 19.2), (int) (height / 21.6)));
    farbenl.add(
        cp5.addTextlabel("FarbenT").setText("Farben").setPosition((float) (width / 1.42222), (float) (height / 5.4))
            .setFont(createFont("Arial", (float) (height / 21.6))));

    farbenl.add(cp5.addTextlabel("Farbe1").setText("Farbe 1").setPosition((float) (width / 1.6), (float) (height / 3.6))
        .setFont(createFont("Arial", (float) (height / 54))));

    farbenl
        .add(cp5.addTextlabel("Farbe2").setText("Farbe 2").setPosition((float) (width / 1.28), (float) (height / 3.6))
            .setFont(createFont("Arial", (float) (height / 54))));
    // ColorPicker
    farbenc.add(cp5.addColorPicker("C1").setPosition((float) (width / 1.6), (float) (height / 3.08571))
        .setColorValue(color(R1, G1, B1, A1)));
    farbenc.add(cp5.addColorPicker("C2").setPosition((float) (width / 1.28), (float) (height / 3.08571))
        .setColorValue(color(R2, G2, B2, A2)));
    farbenb.add(cp5.addButton("FUebernehmen").setLabel("uebernehmen")
        .setPosition((float) (width / 1.37143), (float) (height / 1.8))
        .setSize((int) (width / 5.48571), (int) (height / 10.8)));
  }

  private void buildGeschw() {
    geschb.add(cp5.addButton("GZurueck").setLabel("zurueck").setPosition((float) (width / 1.6), (float) (height / 5.4))
        .setSize((int) (width / 19.2), (int) (height / 21.6)));
    geschl.add(cp5.addTextlabel("EinstellungenT").setText("Einstellungen")
        .setPosition((float) (width / 1.42222), (float) (height / 5.4))
        .setFont(createFont("Arial", (float) (height / 21.6))));
    // 300
    geschs.add(cp5.addSlider("vergroessern").setPosition((float) (width / 1.6), (float) (height / 3.6))
        .setSize((int) (width / 4.26667), (int) (height / 54)).setRange(0, 2).setValue(Scale));
    // 375
    geschs.add(cp5.addSlider("verblassen").setPosition((float) (width / 1.6), (float) (height / 2.88))
        .setSize((int) (width / 4.26667), (int) (height / 54)).setRange(0, 2).setValue(AlphaScale));
    // 450
    geschs.add(cp5.addSlider("verschieben").setPosition((float) (width / 1.6), (float) (height / 2.4))
        .setSize((int) (width / 4.26667), (int) (height / 54)).setRange(0, 2).setValue(Pos));
    // 525
    geschs.add(cp5.addSlider("Dauer").setPosition((float) (width / 1.6), (float) (height / 2.05714))
        .setSize((int) (width / 4.26667), (int) (height / 54)).setRange(2, 10).setValue(Duration / 1000));
    // 600
    geschs.add(cp5.addSlider("Abstand").setPosition((float) (width / 1.6), (float) (height / 1.8))
        .setSize((int) (width / 4.26667), (int) (height / 54)).setRange(5, 30).setValue(_distance));
    // 675
    gescht.add(cp5.addToggle("verschwimmen").setPosition((float) (width / 1.6), (float) (height / 1.6))
        .setSize((int) (width / 19.2), (int) (height / 54)).setValue(_blur));
    gescht.add(cp5.addToggle("wiederholen").setPosition((float) (width / 1.23871), (float) (height / 1.6))
        .setSize((int) (width / 19.2), (int) (height / 54)).setValue(_wiederholen));

    geschb.add(cp5.addButton("GUebernehmen").setLabel("uebernehmen")
        .setPosition((float) (width / 1.37143), (float) (height / 1.35))
        .setSize((int) (width / 5.48571), (int) (height / 10.8)));
  }

  private void buildVorlagen() {
    vorlagenb
        .add(cp5.addButton("VZurueck").setLabel("zurueck").setPosition((float) (width / 1.6), (float) (height / 5.4))
            .setSize((int) (width / 19.2), (int) (height / 21.6)));
    vorlagenl.add(
        cp5.addTextlabel("VorlagenT").setText("Vorlagen").setPosition((float) (width / 1.42222), (float) (height / 5.4))
            .setFont(createFont("Arial", (float) (height / 21.6))));
    vorlagenb.add(cp5.addButton("Vorlage1").setLabel("Sonnenuntergang: Rote unbewegliche Kreise")
        .setPosition((float) (width / 1.6), (float) (height / 3.6))
        .setSize((int) (width / 3.49091), (int) (height / 10.8)));
    vorlagenb.add(cp5.addButton("Vorlage2").setLabel("Schneesturm: Weisse schnelle Kreise")
        .setPosition((float) (width / 1.6), (float) (height / 2.16))
        .setSize((int) (width / 3.49091), (int) (height / 10.8)));
    vorlagenb.add(cp5.addButton("Vorlage3").setLabel("Wald: Gruene langsam groesser werdende Kreise")
        .setPosition((float) (width / 1.6), (float) (height / 1.54286))
        .setSize((int) (width / 3.49091), (int) (height / 10.8)));
  }

  private void buildInfo() {
    info.add(cp5.addButton("IZurueck").setLabel("zurueck").setPosition((float) (width / 1.6), (float) (height / 5.4))
        .setSize((int) (width / 19.2), (int) (height / 21.6)));
    infol.add(cp5.addTextlabel("InformationenT").setText("Informationen")
        .setPosition((float) (width / 1.42222), (float) (height / 5.4))
        .setFont(createFont("Arial", (float) (height / 21.6))));
    infol.add(cp5.addTextlabel("TitelT").setText("Titel").setPosition((float) (width / 1.6), (float) (height / 3.6))
        .setFont(createFont("Arial", (float) (height / 36))));
    infol.add(cp5.addTextlabel("Titel").setText("Evening in Atlantis")
        .setPosition((float) (width / 1.32414), (float) (height / 3.49515))
        .setFont(createFont("Arial", (float) (height / 54))));
    infol.add(cp5.addTextlabel("AlbumT").setText("Album").setPosition((float) (width / 1.6), (float) (height / 2.7))
        .setFont(createFont("Arial", (float) (height / 36))));
    infol.add(cp5.addTextlabel("Album").setText("Seven Days of Falling")
        .setPosition((float) (width / 1.32414), (float) (height / 2.64059))
        .setFont(createFont("Arial", (float) (height / 54))));
    infol.add(
        cp5.addTextlabel("KuenstlerT").setText("Kuenstler").setPosition((float) (width / 1.6), (float) (height / 2.16))
            .setFont(createFont("Arial", (float) (height / 36))));
    infol.add(cp5.addTextlabel("Kuenstler").setText("EST (Esbjoern Svensson Trio")
        .setPosition((float) (width / 1.32414), (float) (height / 2.12181))
        .setFont(createFont("Arial", (float) (height / 54))));
    infol.add(cp5.addTextlabel("VeroeffentlichtT").setText("Veroeffentlicht")
        .setPosition((float) (width / 1.6), (float) (height / 1.8))
        .setFont(createFont("Arial", (float) (height / 36))));
    infol.add(cp5.addTextlabel("Veroeffentlicht").setText("2003")
        .setPosition((float) (width / 1.32414), (float) (height / 1.77340))
        .setFont(createFont("Arial", (float) (height / 54))));
    infol.add(cp5.addTextlabel("GruppeT").setText("Gruppe").setPosition((float) (width / 1.6), (float) (height / 1.35))
        .setFont(createFont("Arial", (float) (height / 36))));
    infol.add(cp5.addTextlabel("Gruppe").setText("Oliver Haerer, Niklas Schmid")
        .setPosition((float) (width / 1.32414), (float) (height / 1.33498))
        .setFont(createFont("Arial", (float) (height / 54))));
  }

  public void showMain() {
    PFont pfont = createFont("Arial", (float) (height / 21.6), true);
    ControlFont font = new ControlFont(pfont, 241);

    for (Button b : main) {
      if (b.getLabel() == "Play") {
        b.getCaptionLabel().setFont(font).setSize(50).toUpperCase(false);
        b.setColorBackground(color(0));
        b.setColorForeground(color(190));
        b.setColorActive(color(100));
        b.show();
      } else {
        b.getCaptionLabel().setFont(font).setSize(50).toUpperCase(false).alignX(0);
        b.setColorBackground(color(0));
        b.setColorForeground(color(190));
        b.setColorActive(color(100));
        b.show();
      }

    }
  }

  public void Play(int i) {
    removeMain();
    state = GAMESTATE.RUNNING;
  }

  public void Farben(int i) {
    PFont pfont = createFont("Arial", (float) (height / 21.6), true);
    ControlFont font = new ControlFont(pfont, 241);

    removeMain();
    for (Button b : farbenb) {
      b.getCaptionLabel().setFont(font).setSize((int) (height / 54)).toUpperCase(false);
      b.setColorBackground(color(50));
      b.setColorForeground(color(190));
      b.setColorActive(color(100));
      b.show();
    }
    for (Textlabel l : farbenl) {
      l.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      l.show();
    }
    for (ColorPicker c : farbenc) {
      c.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      c.show();
    }
  }

  public void Einstellungen(int i) {
    PFont pfont = createFont("Arial", (float) (height / 21.6), true);
    ControlFont font = new ControlFont(pfont, 241);

    removeMain();
    for (Button b : geschb) {
      b.getCaptionLabel().setFont(font).setSize((int) (height / 54)).toUpperCase(false);
      b.setColorBackground(color(50));
      b.setColorForeground(color(190));
      b.setColorActive(color(100));
      b.show();
    }
    for (Textlabel l : geschl) {
      l.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      l.show();
    }
    for (Slider s : geschs) {
      s.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      s.getValueLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      s.setColorBackground(color(50));
      s.setColorForeground(color(100));
      s.setColorActive(color(190));
      s.show();
    }
    for (Toggle t : gescht) {
      t.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      t.setColorBackground(color(50));
      t.setColorForeground(color(100));
      t.setColorActive(color(190));
      t.show();
    }
  }

  public void Vorlagen(int i) {
    PFont pfont = createFont("Arial", (float) (height / 21.6), true);
    ControlFont font = new ControlFont(pfont, 241);

    removeMain();
    for (Button b : vorlagenb) {
      if (b.getLabel() == "zurueck") {
        b.getCaptionLabel().setFont(font).setSize((int) (height / 54)).toUpperCase(false);
        b.setColorBackground(color(50));
        b.setColorForeground(color(190));
        b.setColorActive(color(100));
        b.show();
      } else {
        b.getCaptionLabel().setFont(font).setSize((int) (height / 54)).toUpperCase(false).alignX(0);
        ;
        b.setColorBackground(color(50));
        b.setColorForeground(color(190));
        b.setColorActive(color(100));
        b.show();
      }
    }
    for (Textlabel l : vorlagenl) {
      l.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      l.show();
    }
  }

  public void Informationen(int i) {
    PFont pfont = createFont("Arial", (float) (height / 21.6), true);
    ControlFont font = new ControlFont(pfont, 241);

    removeMain();
    for (Button b : info) {
      b.getCaptionLabel().setFont(font).setSize((int) (height / 54)).toUpperCase(false);
      b.setColorBackground(color(50));
      b.setColorForeground(color(190));
      b.setColorActive(color(100));
      b.show();
    }
    for (Textlabel l : infol) {
      l.getCaptionLabel().setFont(font).setSize((int) (height / 90)).toUpperCase(false);
      l.show();
    }

  }

  public void Beenden(int i) {
    exit();
  }

  public void FZurueck(int i) {
    zurueck();
    removeFarben();
    showMain();
  }

  public void GZurueck(int i) {
    zurueck();
    removeGeschw();
    showMain();
  }

  public void VZurueck(int i) {
    zurueck();
    removeVorlagen();
    showMain();
  }

  public void IZurueck(int i) {
    removeInfo();
    showMain();
  }

  public void Farbe1(int col) {
    System.out.println("Farbe1");
    tempR1 = (int) red(col);
    tempG1 = (int) green(col);
    tempB1 = (int) blue(col);
    tempA1 = (int) alpha(col);
  }

  public void Farbe2(int col) {
    System.out.println("Farbe2");
    tempR2 = (int) red(col);
    tempG2 = (int) green(col);
    tempB2 = (int) blue(col);
    tempA2 = (int) alpha(col);
  }

  public void FUebernehmen(int i) {
    System.out.println("Ü1" + R1 + ", " + G1 + ", " + B1 + ", " + A1);
    System.out.println("Ü2" + R2 + ", " + G2 + ", " + B2 + ", " + A2);
    System.out.println("Ü3" + tempR1 + ", " + tempG1 + ", " + tempB1 + ", " + tempA1);
    System.out.println("Ü4" + tempR2 + ", " + tempG2 + ", " + tempB2 + ", " + tempA2);
    uebernehmen();
    System.out.println("Ü5" + R1 + ", " + G1 + ", " + B1 + ", " + A1);
    System.out.println("Ü6" + R2 + ", " + G2 + ", " + B2 + ", " + A2);
    removeFarben();
    showMain();
  }

  public void controlEvent(ControlEvent c) {
    // when a value change from a ColorPicker is received, extract the ARGB values
    // from the controller's array value
    if (c.isFrom("C1")) {
      System.out.println("CE Farbe1");
      tempR1 = (int) (c.getArrayValue(0));
      tempG1 = (int) (c.getArrayValue(1));
      tempB1 = (int) (c.getArrayValue(2));
      tempA1 = (int) (c.getArrayValue(3));
      System.out.println(tempR1 + ", " + tempG1 + ", " + tempB1 + ", " + tempA1);
    }
    if (c.isFrom("C2")) {
      System.out.println("CE Farbe2");
      tempR2 = (int) (c.getArrayValue(0));
      tempG2 = (int) (c.getArrayValue(1));
      tempB2 = (int) (c.getArrayValue(2));
      tempA2 = (int) (c.getArrayValue(3));
      System.out.println(tempR2 + ", " + tempG2 + ", " + tempB2 + ", " + tempA2);
    }
  }

  public void vergroessern(float f) {
    tempVergroessern = (long) f;
  }

  public void verblassen(float f) {
    tempVerblassen = (long) f;
  }

  public void verschieben(float f) {
    tempVerschieben = (long) f;
  }

  public void Dauer(float f) {
    tempDauer = (long) f;
  }

  public void Abstand(float f) {
    tempAbstand = (long) f;
  }

  public void verschwimmen(boolean b) {
    tempBlur = b;
  }

  public void wiederholen(boolean b) {
    tempReplay = b;
  }

  public void GUebernehmen(int i) {
    uebernehmen();
    removeGeschw();
    showMain();

  }

  public void Vorlage1(int i) {
    tempBlur = true;
    tempReplay = false;

    tempVergroessern = 0;
    tempVerblassen = 0;
    tempVerschieben = 0;
    tempDauer = 1000000;
    tempAbstand = 10;

    tempR1 = 100;
    tempG1 = 0;
    tempB1 = 0;
    tempA1 = 100;

    tempR2 = 255;
    tempG2 = 100;
    tempB2 = 0;
    tempA2 = 150;

    uebernehmen();
    removeVorlagen();
    showMain();
  }

  public void Vorlage2(int i) {
    tempBlur = false;
    tempReplay = false;

    tempVergroessern = 0.8f;
    tempVerblassen = 0.8f;
    tempVerschieben = 0;
    tempDauer = 10000;
    tempAbstand = 10;

    tempR1 = 255;
    tempG1 = 255;
    tempB1 = 255;
    tempA1 = 100;

    tempR2 = 255;
    tempG2 = 255;
    tempB2 = 255;
    tempA2 = 150;

    uebernehmen();
    removeVorlagen();
    showMain();
  }

  public void Vorlage3(int i) {
    tempBlur = false;
    tempReplay = false;

    tempVergroessern = 0.4f;
    tempVerblassen = 0.4f;
    tempVerschieben = 0.15f;
    tempDauer = 10000;
    tempAbstand = 20;

    tempR1 = 50;
    tempG1 = 205;
    tempB1 = 50;
    tempA1 = 150;

    tempR2 = 0;
    tempG2 = 100;
    tempB2 = 0;
    tempA2 = 200;

    uebernehmen();
    removeVorlagen();
    showMain();
  }

  private void uebernehmen() {
    _blur = tempBlur;
    _wiederholen = tempReplay;

    _vergroessern = tempVergroessern > 0;
    _verschieben = tempVerschieben > 0;
    _verblassen = tempVerblassen > 0;

    Scale = tempVergroessern;
    Pos = tempVerschieben;
    AlphaScale = tempVerblassen;
    Duration = (int) tempDauer * 1000;
    _distance = tempAbstand;

    R1 = tempR1;
    G1 = tempG1;
    B1 = tempB1;
    A1 = tempA1;

    R2 = tempR2;
    G2 = tempG2;
    B2 = tempB2;
    A2 = tempA2;

    colorField = new ColorField(R1, G1, B1, A1, R2, G2, B2, A2);
    
    cp5.get("C1").setColorValue(color(R1, G1, B1, A1));
    cp5.get("C2").setColorValue(color(R2, G2, B2, A2));
    cp5.get("vergroessern").setValue(Scale);
    cp5.get("verblassen").setValue(AlphaScale);
    cp5.get("verschieben").setValue(Pos);
    cp5.get("Dauer").setValue(Duration/1000);
    cp5.get("Abstand").setValue(_distance);
    ((Toggle)cp5.get("verschwimmen")).setValue(_blur);
    ((Toggle)cp5.get("wiederholen")).setValue(_wiederholen);
    
    System.out.println(colorField.toString());
  }

  private void zurueck() {
    tempBlur = _blur;
    tempReplay = _wiederholen;

    tempVergroessern = Scale;
    tempVerschieben = Pos;
    tempVerblassen = AlphaScale;

    if (!_vergroessern) {
      tempVergroessern = 0;
    }
    if (!_verschieben) {
      tempVerschieben = 0;
    }
    if (!_verblassen) {
      tempVerblassen = 0;
    }

    tempDauer = Duration / 1000;
    tempAbstand = _distance;

    tempR1 = R1;
    tempG1 = G1;
    tempB1 = B1;
    tempA1 = A1;

    tempR2 = R2;
    tempG2 = G2;
    tempB2 = B2;
    tempA2 = A2;
    
    cp5.get("C1").setColorValue(color(R1, G1, B1, A1));
    cp5.get("C2").setColorValue(color(R2, G2, B2, A2));
    cp5.get("vergroessern").setValue(Scale);
    cp5.get("verblassen").setValue(AlphaScale);
    cp5.get("verschieben").setValue(Pos);
    cp5.get("Dauer").setValue(Duration/1000);
    cp5.get("Abstand").setValue(_distance);
    ((Toggle)cp5.get("verschwimmen")).setValue(_blur);
    ((Toggle)cp5.get("wiederholen")).setValue(_wiederholen);
  }

  private void removeMain() {
    for (Button b : main) {
      b.hide();
    }

    // cp5.get("Play").hide();
    // cp5.get("Farben").hide();
    // cp5.get("Einstellungen").hide();
    // cp5.get("Vorlagen").hide();
    // cp5.get("Informationen").hide();
    // cp5.get("Beenden").hide();
    // cp5.hide();
  }

  private void removeFarben() {
    for (Button b : farbenb) {
      b.hide();
    }
    for (ColorPicker c : farbenc) {
      c.hide();
    }
    for (Textlabel l : farbenl) {
      l.hide();
    }

    // cp5.get("FZurueck").hide();
    // cp5.get("FarbenT").hide();
    // cp5.get("Farbe1").hide();
    // cp5.get("Farbe2").hide();
    // cp5.get("C1").hide();
    // cp5.get("C2").hide();
    // cp5.get("FUebernehmen").hide();
  }

  private void removeGeschw() {
    for (Button b : geschb) {
      b.hide();
    }
    for (Slider s : geschs) {
      s.hide();
    }
    for (Textlabel l : geschl) {
      l.hide();
    }
    for (Toggle t : gescht) {
      t.hide();
    }

    // cp5.get("GZurueck");
    // cp5.get("EinstellungenT");
    // cp5.get("vergroessern");
    // cp5.get("verblassen");
    // cp5.get("verschwimmen");
    // cp5.get("GUebernehmen");
  }

  private void removeVorlagen() {
    for (Button b : vorlagenb) {
      b.hide();
    }
    for (Textlabel l : vorlagenl) {
      l.hide();
    }
    // cp5.get("VZurueck");
    // cp5.get("VorlagenT");
    // cp5.get("Vorlage1T");
    // cp5.get("Vorlage1");
    // cp5.get("Vorlage2T");
    // cp5.get("Vorlage2");
    // cp5.get("Vorlage3T");
    // cp5.get("Vorlage3");
  }

  private void removeInfo() {
    for (Button b : info) {
      b.hide();
    }
    for (Textlabel l : infol) {
      l.hide();
    }
    // cp5.get("IZurueck").hide();
    // cp5.get("InformationenT").hide();
    // cp5.get("TitelT").hide();
    // cp5.get("Titel").hide();
    // cp5.get("AlbumT").hide();
    // cp5.get("Album").hide();
    // cp5.get("KuenstlerT").hide();
    // cp5.get("Kuenstler").hide();
    // cp5.get("VeroeffentlichtT").hide();
    // cp5.get("Veroeffentlicht").hide();
    // cp5.get("GruppeT").hide();
    // cp5.get("Gruppe").hide();
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
    if (actualTime - c.node.getTimeStamp() >= Duration && (_verblassen || _vergroessern || _verschieben)) {
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
        notes.clear();
        createNotes();
        circles.clear();
        state = GAMESTATE.MENUE;
        song.pause();
        song.rewind();
        song.cue(0);

        showMain();
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
      // the length() method returns the length of recording in
      // milliseconds.
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
    if (!song.isPlaying() && _wiederholen) {
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

    notes.add(new Note(41.240, 59, 0.779319728));

    notes.add(new Note(44.823219955, 76, 0.920090703));
    notes.add(new Note(44.823219955, 71, 0.920090703));
    notes.add(new Note(44.823219955, 64, 0.920090703));
    notes.add(new Note(44.823219955, 59, 0.920090703));
  }
}

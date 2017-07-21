/* =========================================================
 * ====                   WARNING                        ===
 * =========================================================
 * The code in this tab has been generated from the GUI form
 * designer and care should be taken when editing this file.
 * Only add/edit code inside the event handlers i.e. only
 * use lines between the matching comment tags. e.g.

 void myBtnEvents(GButton button) { //_CODE_:button1:12356:
     // It is safe to enter your event code here  
 } //_CODE_:button1:12356:
 
 * Do not rename this tab!
 * =========================================================
 */

public void resolution_click1(GButton source, GEvent event) { //_CODE_:resolution:978814:
  println("resolution - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:resolution:978814:

public void colors_click1(GButton source, GEvent event) { //_CODE_:colors:406220:
  println("colors - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:colors:406220:

public void speed_click1(GButton source, GEvent event) { //_CODE_:speed:469939:
  println("speed - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:speed:469939:

public void presets_click1(GButton source, GEvent event) { //_CODE_:presets:525589:
  println("presets - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:presets:525589:

public void information_click1(GButton source, GEvent event) { //_CODE_:information:425069:
  println("information - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:information:425069:

public void exit_click1(GButton source, GEvent event) { //_CODE_:exit:210162:
  println("exit - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:exit:210162:

public void play_click1(GButton source, GEvent event) { //_CODE_:play:773471:
  println("play - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:play:773471:

public void sound_click1(GButton source, GEvent event) { //_CODE_:sound:270024:
  println("sound - GButton >> GEvent." + event + " @ " + millis());
} //_CODE_:sound:270024:



// Create all the GUI controls. 
// autogenerated do not edit
public void createGUI(){
  G4P.messagesEnabled(false);
  G4P.setGlobalColorScheme(GCScheme.BLUE_SCHEME);
  G4P.setCursor(ARROW);
  surface.setTitle("Sketch Window");
  resolution = new GButton(this, 1400, 50, 400, 100);
  resolution.setText("Auflösung");
  resolution.setTextBold();
  resolution.addEventHandler(this, "resolution_click1");
  colors = new GButton(this, 1400, 200, 400, 100);
  colors.setText("Farben");
  colors.setTextBold();
  colors.addEventHandler(this, "colors_click1");
  speed = new GButton(this, 1400, 350, 400, 100);
  speed.setText("Geschwindigkeit");
  speed.setTextBold();
  speed.addEventHandler(this, "speed_click1");
  presets = new GButton(this, 1400, 500, 400, 100);
  presets.setText("Presets");
  presets.setTextBold();
  presets.addEventHandler(this, "presets_click1");
  information = new GButton(this, 1500, 800, 300, 75);
  information.setText("Informationen");
  information.setTextBold();
  information.addEventHandler(this, "information_click1");
  exit = new GButton(this, 1500, 925, 300, 75);
  exit.setText("Beenden");
  exit.setTextBold();
  exit.addEventHandler(this, "exit_click1");
  play = new GButton(this, 150, 400, 380, 380);
  play.setText("Play");
  play.setTextBold();
  play.addEventHandler(this, "play_click1");
  sound = new GButton(this, 50, 930, 100, 100);
  sound.setText("Lautstärke");
  sound.setTextBold();
  sound.addEventHandler(this, "sound_click1");
}

// Variable declarations 
// autogenerated do not edit
GButton resolution; 
GButton colors; 
GButton speed; 
GButton presets; 
GButton information; 
GButton exit; 
GButton play; 
GButton sound; 
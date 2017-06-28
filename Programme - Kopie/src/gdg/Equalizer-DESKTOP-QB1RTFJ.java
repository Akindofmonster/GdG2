package gdg;

import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import gdg.objects.Circle;
import gdg.objects.Note;
import processing.core.PApplet;
import processing.core.PVector;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * The Equalizer class is the main controller for the Animation.
 *
 * @author Tobias Lahmann
 */
public class Equalizer extends PApplet {

	private static int Height = 900;
	private static int Wide = 1820;
	private static final int AddScale = (Height + Wide) / 350;
	private static final float PosFactor = AddScale;
	// A list of circles to hold the elements to display.
	private ArrayList<Circle> circles;
	private ArrayList<Note> notes;
	private ArrayList<Note> delNote = new ArrayList<Note>();
	private ArrayList<Circle> delCircle = new ArrayList<Circle>();
	int counter = 0;
	int count = 0;

	int alpha = 150;
	private AudioPlayer song;
	private FFT fft;

	/**
	 * The settings() method runs before the sketch has been set up, so other
	 * Processing functions cannot be used at this point.
	 */
	@Override
	public void settings() {

		fullScreen(2);
		Height = displayHeight;
		Wide = displayWidth;
		// setSize(Wide, Height);
		smooth();
		System.out.println("test");
	}

	/**
	 * The setup method is called before displaying any objects. The Method
	 * calls commands in the Processing API.
	 */
	@Override
	public void setup() {

		background(0);
		frameRate(30);
		noStroke();
		circles = new ArrayList<>();
		notes = new ArrayList<>();
		createNotes();

		// float random = random(50, 100);
		// // Creates random circles to see what an animation could look like
		//
		// PVector position = new PVector(this.width / 2, this.height / 2);
		// for (int i = 0; i < 10; i++) {
		// float radius = random * i * random(1f, 2);//
		// random(i*50,i*120);//this.width*i/10/5,
		// // this.width*i/8);
		// float weight = 40;// random(25, 35);
		// float alpha = random(50, 100);
		// int color = color(random(0, 50f), random(155f, 255f), random(0, 50f),
		// alpha);
		// circles.add(new Circle(this, position, radius, weight, color));
		// }
		fill(255, 150);
		rect(100, 100, 100, 100);
		Minim minim = new Minim(this);
		song = minim.loadFile("./gdg/objects/03_m___e.s.t___seven_days_of_falling___evening_in_atlantis.mp3");
		// song = minim.loadFile("./data/Kontinuum - First Rain.mp3");
		// fft = new FFT(song.bufferSize(), song.sampleRate());

		// spectra = new ArrayList<>();
		// for (int i = 1; i < 7; i++) {
		// float radius = i * 50;
		// float weight = 4;
		// int color = color(255, 255, random(0, 100), 150);
		// int side = i % 2 == 0 ? 1 : -1;
		// PVector orientation = new PVector(side * PConstants.HALF_PI, side);
		// spectra.add(new Spectrum(this, position, radius, weight, color,
		// fft.specSize(), orientation));
		// }

		// Start the song right away
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		song.play();

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
	}

	/**
	 * Draw method is responsible for displaying all objects.
	 */
	@Override
	public void draw() {

		for (Circle c : delCircle) {
			circles.remove(c);
		}
		for (Note n : delNote) {
			notes.remove(n);
		}

		background(0);
		noStroke();
		// if (counter <= 0) {
		// counter = 10;
		// count++;
		// System.out.println(count);
		// fill(color(255, 255, 255, 50));
		// PShape shape = createShape(PConstants.RECT, 0, 0, Wide, Height);
		int time = song.position();

		for (Note n : notes) {
			if (n.getTimeStamp() >= time - 50 && n.getTimeStamp() <= time + 50 && n.notAdded()) {
				float randomX = random(Wide / 5, Wide - Wide / 5);
				float randomY = random(Height / 5, Height - Height / 5);
				int color = color(255,255,255);
				circles.add(new Circle(this, new PVector(randomX, randomY), n.GetSize() * 5, 10, color, alpha, n));
				n.add = true;
				delNote.add(n);
			}
		}
		// fft.forward(song.mix);
		// // Add a jitter variable from fft of the song to the objects
		// float jitter = fft.getBand(1);
		// // Display every circle available
		for (Circle c : circles) {
			if (c.draw) {
//				Thread t = new Thread(){ public void run() {transformCircle(c);}};
//				t.start();
				transformCircle(c);
				 if (time - c.node.getTimeStamp() >= 2000) {
				 c.draw = false;
				 delCircle.add(c);
				 }
				//
				// // System.out.println("Circle" + c.radius);
				// // fill(c.getColor());
				// noStroke();
				// c.update(jitter);
				c.display();
			}
		}

		// // update every spectral arc
		// float val;
		// for (int i = 0; i < fft.specSize(); i++) {
		// // update the left spectral arcs
		// val = song.left.get(i);
		// for (int j = 1; j < spectra.size(); j += 2) {
		// spectra.get(j).update(i, val * 40);
		// }
		//
		// // update the right spectral arcs
		//
		// for (int j = 0; j < spectra.size(); j += 2) {
		// spectra.get(j).update(i, val * 40);
		// }
		// }
		//
		// // display every spectrum available
		// for (Spectrum s : spectra) {
		// s.display();
		// }
		// } else {
		// counter--;
		// for (Circle c : circles) {
		// c.display();
		// }
		// }
	}

	private void transformCircle(Circle c) {
		
//		float alpha = c.getAlpha() - 2.2f;
//		System.out.println(alpha);
//		c.setColor(c.getColorWithoutAlpha(), alpha);
		 int tempRadius = (int) (c.radius + AddScale);
		 c.scale = tempRadius / c.radius;
//		 float x = c.position.x;
//		 float y = c.position.y;
//		 if (c.position.x >= Wide / 2) {
//		 x += ((x * (c.scale - 1)) / PosFactor);
//		 // System.out.println("x increased");
//		 } else {
//		 x -= ((x * (c.scale - 1)) / PosFactor);
//		 // System.out.println("x decreased");
//		 }
//		 if (c.position.y >= Height / 2) {
//		 y += ((y * (c.scale - 1)) / PosFactor);
//		 // System.out.println("y increased");
//		 } else {
//		 y -= ((y * (c.scale - 1)) / PosFactor);
//		 // System.out.println("y increased");
//		 }
//		 // System.out.println(x + ":" + x2 + " " + y + ":" + y2);
//		 c.position = new PVector(x, c.position.y);
	}

	/**
	 * Main method to instantiate the PApplet.
	 *
	 * @param args
	 *            Arguments passed to the PApplet.
	 */
	public static void main(String[] args) {
		PrintStream out;
		try {
			out = new PrintStream(new FileOutputStream("output.txt"));
			System.setOut(out);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("test");
		PApplet.main(new String[] { Equalizer.class.getName() });
	}

	public FFT getFft() {
		return fft;
	}

	public void setFft(FFT fft) {
		this.fft = fft;
	}

	public static float getPosfactor() {
		return PosFactor;
	}
}

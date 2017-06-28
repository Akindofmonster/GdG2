package gdg.objects;

public class Note {

	private final float maxFre = 86;
	private final float minFre = 43;

	public double timestamp;
	public double frequentcy;
	public double duration;
	public boolean add;

	public Note(double timestamp, double frequentcy, double duration) {
		this.timestamp = timestamp;
		this.frequentcy = frequentcy;
		this.duration = duration;
	}

	public float GetSize() {

		return (float) (frequentcy - minFre / 2);

	}

	public boolean notAdded() {
		// TODO Auto-generated method stub
		return !add;
	}

	public int getTimeStamp() {
		return (int) (timestamp * 1000);
	}

	public int getDuration() {
		return (int) (duration * 1000);
	}

	public float getMaxFre() {
		return maxFre;
	}

}

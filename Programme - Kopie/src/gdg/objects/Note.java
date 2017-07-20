package gdg.objects;

public class Note {

  private final float maxFre = 86;
  private final float minFre = 43;

  public double timestamp;
  public double frequentcy;
  public double duration;
  private long lastadded = -60;

  public long getLastadded() {
    return lastadded;
  }

  public void setLastadded(long lastadded) {
    // System.out.println("LastSetter set to n at time " + timestamp);
    this.lastadded = lastadded;
  }

  public Note(double timestamp, double frequentcy, double duration) {
    this.timestamp = timestamp;
    this.frequentcy = frequentcy;
    this.duration = duration;
  }

  public float GetSize() {

    return (float) (frequentcy - minFre / 1.5);

  }

  public boolean notAdded(long time) {
    System.out.println(String.format("(time %s- lastadded %d)      ", time, lastadded)
        + ((time - lastadded) >= 100 || (time - lastadded) <= -100));
    return (time - lastadded) >= 100 || (time - lastadded) <= -100;
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

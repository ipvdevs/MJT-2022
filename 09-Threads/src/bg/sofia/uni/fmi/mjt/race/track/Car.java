package bg.sofia.uni.fmi.mjt.race.track;

import java.util.Random;

public class Car implements Runnable {

    private final int id;
    private final Track track;
    private final int nPitStops;

    private static final int MAX_RACING_TIME = 1001;

    private static final Random CAR_RANDOM = new Random();

    public Car(int id, int nPitStops, Track track) {
        this.id = id;
        this.nPitStops = nPitStops;
        this.track = track;
    }

    @Override
    public void run() {
        int sleepMillis = CAR_RANDOM.nextInt(MAX_RACING_TIME);

        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            System.err.print("Unexpected exception was thrown: " + e.getMessage());
            e.printStackTrace();
        }

        track.enterPit(this);
    }

    public int getCarId() {
        return id;
    }

    public int getNPitStops() {
        return nPitStops;
    }

    public Track getTrack() {
        return track;
    }

}
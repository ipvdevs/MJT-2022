package bg.sofia.uni.fmi.mjt.race.track.pit;

import bg.sofia.uni.fmi.mjt.race.track.Car;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Pit {
    private final Queue<Car> waiting;
    private final List<PitTeam> pitTeams;

    private int stopsCount;

    private boolean finished = false;

    public boolean isFinished() {
        return finished;
    }

    public Pit(int nPitTeams) {
        this.pitTeams = new ArrayList<>(nPitTeams);
        this.waiting = new ArrayDeque<>();
        this.stopsCount = 0;

        initPitTeams(nPitTeams);
    }

    private void initPitTeams(int nPitTeams) {
        for (int i = 0; i < nPitTeams; i++) {
            PitTeam team = new PitTeam(i, this);
            pitTeams.add(team);
            team.start();
        }
    }

    public synchronized void submitCar(Car car) {
        if (!finished) {
            waiting.add(car);
            ++stopsCount;
        }

        this.notifyAll();
    }

    public synchronized Car getCar() {
        while (!finished && waiting.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return waiting.poll();
    }

    public int getPitStopsCount() {
        return stopsCount;
    }

    public List<PitTeam> getPitTeams() {
        return pitTeams;
    }

    public synchronized void finishRace() {
        finished = true;
        this.notifyAll();
    }

}
package bg.sofia.uni.fmi.mjt.race.track;

import bg.sofia.uni.fmi.mjt.race.track.pit.Pit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RaceTrack implements Track {
    private final Pit pit;
    private final List<Integer> finishedCars;

    public RaceTrack(int nPitTeams) {
        this.pit = new Pit(nPitTeams);
        this.finishedCars = new ArrayList<>();
    }

    @Override
    public void enterPit(Car car) {
        if (car.getNPitStops() == 0) {
            finishedCars.add(car.getCarId());
        } else {
            pit.submitCar(car);
        }
    }

    @Override
    public int getNumberOfFinishedCars() {
        return finishedCars.size();
    }

    @Override
    public List<Integer> getFinishedCarsIds() {
        return Collections.unmodifiableList(finishedCars);
    }

    @Override
    public Pit getPit() {
        return pit;
    }
}

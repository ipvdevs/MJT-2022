package bg.sofia.uni.fmi.mjt.race.track.pit;

import bg.sofia.uni.fmi.mjt.race.track.Car;

import java.util.Random;

public class PitTeam extends Thread {
    private static final Random REPAIR_RANDOM = new Random();
    private static final int MAX_REPAIR_TIME = 201;

    private final int id;
    private final Pit pitStop;

    private int repairedCars;

    public PitTeam(int id, Pit pitStop) {
        this.id = id;
        this.pitStop = pitStop;
    }

    @Override
    public void run() {
        repairCar();
    }

    private void repairCar() {
        Car car;
        while ((car = pitStop.getCar()) != null) {
            int repairTime = REPAIR_RANDOM.nextInt(MAX_REPAIR_TIME);

            try {
                Thread.sleep(repairTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread repairedCar = new Thread(new Car(
                    car.getCarId(),
                    car.getNPitStops() - 1,
                    car.getTrack()));

            ++repairedCars;

            repairedCar.start();
        }
    }


    public int getPitStoppedCars() {
        return repairedCars;
    }

}
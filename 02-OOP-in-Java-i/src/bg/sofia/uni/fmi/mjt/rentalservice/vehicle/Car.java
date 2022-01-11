package bg.sofia.uni.fmi.mjt.rentalservice.vehicle;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;

public class Car extends AbstractVehicle {
    private static final String TYPE = "CAR";
    private static final double PRICE_PER_MINUTE = 0.50;

    public Car(String id, Location location) {
        super(id, location);
    }

    @Override
    public double getPricePerMinute() {
        return PRICE_PER_MINUTE;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}

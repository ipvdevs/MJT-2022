package bg.sofia.uni.fmi.mjt.rentalservice.service;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;
import bg.sofia.uni.fmi.mjt.rentalservice.vehicle.Vehicle;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class RentalService implements RentalServiceAPI {
    Vehicle[] vehicles;

    public RentalService(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public double rentUntil(Vehicle vehicle, LocalDateTime until) {
        if (until == null || until.isBefore(LocalDateTime.now()) || vehicle == null) {
            return -1;
        }

        for (Vehicle v : vehicles) {

            if (v.equals(vehicle) && v.isAvailable()) {
                v.setEndOfReservationPeriod(until);

                long timeDifferenceInSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), until);

                long usageInMinutes = timeDifferenceInSeconds / 60;

                // A new minute has begun
                if (timeDifferenceInSeconds % 60 > 0) {
                    ++usageInMinutes;
                }

                return v.getPricePerMinute() * usageInMinutes;
            }
        }

        return -1;
    }

    @Override
    public Vehicle findNearestAvailableVehicleInRadius(String type, Location location, double maxDistance) {
        if (type == null || location == null || maxDistance < 0.0) {
            return null;
        }

        Vehicle nearest = null;

        for (Vehicle v : vehicles) {

            if (v.getType().equals(type) && v.isAvailable()) {
                double currentDistance = location.getDistanceTo(v.getLocation());

                if (nearest == null) {
                    if (currentDistance <= maxDistance) {
                        nearest = v;
                    }
                    continue;
                }

                double nearestDistance = location.getDistanceTo(nearest.getLocation());

                if (currentDistance <= Math.min(maxDistance, nearestDistance)) {
                    nearest = v;
                }

            }
        }

        return nearest;
    }
}

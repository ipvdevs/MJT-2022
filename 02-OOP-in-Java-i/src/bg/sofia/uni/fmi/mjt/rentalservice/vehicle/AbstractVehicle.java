package bg.sofia.uni.fmi.mjt.rentalservice.vehicle;

import bg.sofia.uni.fmi.mjt.rentalservice.location.Location;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class AbstractVehicle implements Vehicle {
    private final String id;
    private final Location location;

    private LocalDateTime until;

    public AbstractVehicle(String id, Location location) {
        this.id = id;
        this.location = location;

        until = LocalDateTime.now();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public LocalDateTime getEndOfReservationPeriod() {
        return until;
    }

    @Override
    public void setEndOfReservationPeriod(LocalDateTime until) {
        this.until = until;
    }

    @Override
    public boolean isAvailable() {
        return until == null || until.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractVehicle that = (AbstractVehicle) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

package vehicle;

import enums.Direction;
import enums.VehicleStatus;
import enums.VehicleType;

public class StandardVehicle extends Vehicle {

    public StandardVehicle(String id, VehicleType type, int speed, Direction direction) {
        super(id, type, speed, 1, direction);
    }

    @Override
    public void move() {
        status = VehicleStatus.MOVING;
        System.out.println(vehicleType + " " + id + " is moving");
    }
}
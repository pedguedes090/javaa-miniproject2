package vehicle;

import enums.Direction;
import enums.VehicleStatus;
import enums.VehicleType;
import enums.LightColor;

public abstract class Vehicle {

    protected String id;
    protected VehicleType vehicleType;
    protected int speed;
    protected int priority;
    protected Direction direction;
    protected VehicleStatus status;


    public Vehicle(String id, VehicleType vehicleType, int speed, int priority, Direction direction) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.speed = speed;
        this.priority = priority;
        this.direction = direction;
        this.status = VehicleStatus.WAITING;

    }

    public abstract void move();

    public void stop() {
        status = VehicleStatus.STOPPED;
        System.out.println(vehicleType + " " + id + " stopped.");
    }

    public void yield() {
        System.out.println(vehicleType + " " + id + " is yielding.");
    }

    public boolean canPassRedLight() {
        return false;
    }

    public boolean canMove(LightColor lightColor) {
        return lightColor == LightColor.GREEN || canPassRedLight();
    }

    public String getId() {
        return id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public Direction getDirection() {
        return direction;
    }

    public VehicleStatus getStatus() {
        return status;
    }

}
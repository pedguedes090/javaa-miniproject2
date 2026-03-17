package queue;

import observer.TrafficObserver;
import enums.LightColor;

public class VehicleQueueManager implements TrafficObserver {
    @Override
    public void update(LightColor color) {
        switch (color) {
            case GREEN:
                System.out.println("Vehicles are moving...");
                // set vehicle status = MOVING
                break;
            case RED:
                System.out.println("Vehicles must stop!");
                break;
            case YELLOW:
                System.out.println("Vehicles should slow down!");
                break;
        }
    }
}
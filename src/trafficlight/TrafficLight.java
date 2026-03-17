package trafficlight;

import config.SimulationConfig;
import enums.LightColor;

public class TrafficLight {

    private LightState currentState;
    private int remainingTime;
    private SimulationConfig config;

    public TrafficLight() {
        this.config = new SimulationConfig();
        this.currentState = new RedState();
        this.remainingTime = config.getRedLightTime();
    }

    public void tick() {
        remainingTime--;

        if (remainingTime <= 0) {
            changeState();
        }
    }

    public void changeState() {
        currentState.handle(this);
    }

    public LightColor getCurrentColor() {
        return currentState.getColor();
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public void setState(LightState state) {
        this.currentState = state;
    }

    public void setRemainingTime(int time) {
        this.remainingTime = time;
    }
}
package trafficlight;

import config.SimulationConfig;
import enums.LightColor;
import observer.TrafficObserver;
import observer.TrafficSubject;
import java.util.ArrayList;
import java.util.List;

public class TrafficLight implements TrafficSubject {
    private List<TrafficObserver> observers = new ArrayList<>();
    @Override
    public void addObserver(TrafficObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(TrafficObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (TrafficObserver observer : observers) {
            observer.update(currentState.getColor());
        }
    }

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
        notifyObservers();
    }

    public void setRemainingTime(int time) {
        this.remainingTime = time;
    }
}
package trafficlight;

import enums.LightColor;

public class RedState implements LightState {

    @Override
    public void handle(TrafficLight light) {
        light.setRemainingTime(light.getConfig().getRedLightTime());
        light.setState(new GreenState());
    }

    @Override
    public LightColor getColor() {
        return LightColor.RED;
    }
}
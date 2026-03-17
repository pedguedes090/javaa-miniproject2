package trafficlight;

import enums.LightColor;

public class GreenState implements LightState {

    @Override
    public void handle(TrafficLight light) {
        light.setRemainingTime(light.getConfig().getGreenLightTime());
        light.setState(new YellowState());
    }

    @Override
    public LightColor getColor() {
        return LightColor.GREEN;
    }
}
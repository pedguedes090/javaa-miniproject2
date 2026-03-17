package trafficlight;

import enums.LightColor;

public class YellowState implements LightState {

    @Override
    public void handle(TrafficLight light) {
        light.setRemainingTime(light.getConfig().getYellowLightTime());
        light.setState(new RedState());
    }

    @Override
    public LightColor getColor() {
        return LightColor.YELLOW;
    }
}
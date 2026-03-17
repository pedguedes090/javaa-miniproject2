package trafficlight;

import enums.LightColor;

public interface LightState {
    void handle(TrafficLight light);
    LightColor getColor();
}
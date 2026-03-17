package observer;

import enums.LightColor;

public interface TrafficObserver {
    void update(LightColor color);
}
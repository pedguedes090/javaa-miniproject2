package config;

/*Class cấu hình chung cho hệ thống mô phỏng giao thông*/
public class SimulationConfig {
    private int greenLightTime;
    private int yellowLightTime;
    private int redLightTime;

    private int maxVehicleQueue;
    private int numberOfLanes;

    public SimulationConfig() {
        // lấy giá trị mặc định từ constants
        this.greenLightTime = AppConstants.GREEN_LIGHT_TIME;
        this.yellowLightTime = AppConstants.YELLOW_LIGHT_TIME;
        this.redLightTime = AppConstants.RED_LIGHT_TIME;
        this.maxVehicleQueue = AppConstants.MAX_VEHICLE_QUEUE;
        this.numberOfLanes = AppConstants.NUMBER_OF_LANES;
    }
    public int getGreenLightTime() {
        return greenLightTime;
    }
    public int getYellowLightTime() {
        return yellowLightTime;
    }
    public int getRedLightTime() {
        return redLightTime;
    }
    public int getMaxVehicleQueue() {
        return maxVehicleQueue;
    }
    public int getNumberOfLanes() {
        return numberOfLanes;
    }
}
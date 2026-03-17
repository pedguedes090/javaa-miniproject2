package config;

/*Class chứa các hằng số dùng chung cho toàn bộ hệ thống*/
public class AppConstants {
    // Thời gian đèn giao thông (đơn vị: giây)
    public static final int GREEN_LIGHT_TIME = 10;
    public static final int YELLOW_LIGHT_TIME = 3;
    public static final int RED_LIGHT_TIME = 10;
    // Số xe tối đa được phép chờ trong 1 lane
    public static final int MAX_VEHICLE_QUEUE = 20;
    // Số lane tại giao lộ
    public static final int NUMBER_OF_LANES = 4;
    // Thời gian tick mô phỏng (1 vòng lặp simulation)
    public static final int SIMULATION_TICK = 1;
}
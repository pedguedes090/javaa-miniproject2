package enums;

/*Trạng thái hiện tại của xe trong mô phỏng*/
public enum VehicleStatus {

    MOVING,      // đang di chuyển
    WAITING,     // đang chờ đèn đỏ hoặc tắc
    STOPPED,     // đã dừng
    PASSED       // đã đi qua giao lộ
}
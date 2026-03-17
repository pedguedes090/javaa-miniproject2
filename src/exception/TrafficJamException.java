package exception;
/**
 * Ngoại lệ được ném ra khi hàng chờ tại ngã tư vượt quá công suất cho phép,
 * hoặc khi một phương tiện phải chờ quá lâu dẫn đến kẹt xe cục bộ.
 */
public class TrafficJamException extends RuntimeException {

    public TrafficJamException() {
        super("Cảnh báo: Đã xảy ra tình trạng kẹt xe tại giao lộ!");
    }

    public TrafficJamException(String message) {
        super(message);
    }

    public TrafficJamException(String message, Throwable cause) {
        super(message, cause);
    }
}

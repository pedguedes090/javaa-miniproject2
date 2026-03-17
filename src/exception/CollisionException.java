package exception;

/**
 * Ngoại lệ nghiêm trọng được ném ra khi xử lý Lock/Đồng bộ hóa không tốt,
 * dẫn đến việc có nhiều hơn số lượng phương tiện cho phép cùng đi vào Critical Section (Giao lộ),
 * gây ra Race Condition và va chạm.
 */
public class CollisionException extends RuntimeException {

    public CollisionException() {
        super("Lỗi nghiêm trọng: Xảy ra va chạm phương tiện do lỗi đồng bộ hóa (Race Condition)!");
    }

    public CollisionException(String message) {
        super(message);
    }

    public CollisionException(String message, Throwable cause) {
        super(message, cause);
    }
}

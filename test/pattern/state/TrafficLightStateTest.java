package pattern.state;

import static org.junit.jupiter.api.Assertions.*;

import enums.LightColor;
import observer.TrafficObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import trafficlight.TrafficLight;

import static org.junit.jupiter.api.Assertions.*;

class TrafficLightStateTest {

    private TrafficLight trafficLight;

    @BeforeEach
    void setUp() {
        // Khởi tạo một đối tượng TrafficLight mới trước mỗi test case
        trafficLight = new TrafficLight();
    }

    @Test
    void testInitialStateIsRed() {
        // Kiểm tra xem trạng thái ban đầu khi khởi tạo có đúng là đèn Đỏ không
        assertEquals(LightColor.RED, trafficLight.getCurrentColor(), "Trạng thái ban đầu phải là màu ĐỎ");
        assertTrue(trafficLight.getRemainingTime() > 0, "Thời gian đèn đỏ phải lớn hơn 0");
    }

    @Test
    void testStateTransitions() {
        // 1. Đang Đỏ -> Chuyển trạng thái -> Sang Xanh
        trafficLight.changeState();
        assertEquals(LightColor.GREEN, trafficLight.getCurrentColor(), "Đèn Đỏ phải chuyển sang đèn XANH");

        // 2. Đang Xanh -> Chuyển trạng thái -> Sang Vàng
        trafficLight.changeState();
        assertEquals(LightColor.YELLOW, trafficLight.getCurrentColor(), "Đèn Xanh phải chuyển sang đèn VÀNG");

        // 3. Đang Vàng -> Chuyển trạng thái -> Về lại Đỏ
        trafficLight.changeState();
        assertEquals(LightColor.RED, trafficLight.getCurrentColor(), "Đèn Vàng phải chuyển về lại đèn ĐỎ");
    }

    @Test
    void testTickMethodTriggersStateChange() {
        // Giả lập thời gian của đèn hiện tại chỉ còn 1 giây
        trafficLight.setRemainingTime(1);

        // Gọi tick() sẽ làm thời gian giảm về 0 và kích hoạt changeState()
        trafficLight.tick();

        assertEquals(LightColor.GREEN, trafficLight.getCurrentColor(), "Khi thời gian về 0, đèn Đỏ phải tự động chuyển sang Xanh");
    }

    @Test
    void testObserverIsNotifiedOnChange() {
        // Tạo một Observer giả (Mock) để hứng dữ liệu
        TestObserver mockObserver = new TestObserver();
        trafficLight.addObserver(mockObserver);

        // Kích hoạt việc đổi trạng thái (chuyển sang Xanh)
        trafficLight.changeState();

        // Kiểm tra xem Observer có nhận được thông báo chuyển sang màu XANH chưa
        assertTrue(mockObserver.isNotified, "Observer chưa nhận được thông báo");
        assertEquals(LightColor.GREEN, mockObserver.receivedColor, "Observer nhận sai màu đèn");
    }

    // Lớp nội bộ hỗ trợ việc test Observer Pattern
    private static class TestObserver implements TrafficObserver {
        boolean isNotified = false;
        LightColor receivedColor = null;

        @Override
        public void update(LightColor color) {
            this.isNotified = true;
            this.receivedColor = color;
        }
    }
}
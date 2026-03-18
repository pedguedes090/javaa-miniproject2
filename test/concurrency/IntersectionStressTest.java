package concurrency;

import engine.Intersection;
import enums.Direction;
import enums.VehicleType;
import exception.CollisionException;
import org.junit.jupiter.api.Test;
import vehicle.StandardVehicle;
import vehicle.Vehicle;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class IntersectionStressTest {

    @Test
    void stressTestIntersectionHandles100VehiclesWithoutCollision() throws InterruptedException {
        int totalVehicles = 100;
        Intersection intersection = new Intersection();

        // Tạo ThreadPool 100 luồng
        ExecutorService executor = Executors.newFixedThreadPool(totalVehicles);

        // startGate chặn ở vạch xuất phát, endGate đếm số xe về đích
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(totalVehicles);

        // Biến thread-safe để kiểm tra số xe thực tế đang lưu thông trong ngã tư
        AtomicInteger activeVehiclesInIntersection = new AtomicInteger(0);
        AtomicBoolean collisionOccurred = new AtomicBoolean(false);

        for (int i = 0; i < totalVehicles; i++) {
            final int id = i;
            executor.submit(() -> {
                // Khởi tạo một xe đại diện để test
                Vehicle vehicle = new StandardVehicle("V" + id, VehicleType.CAR, 50, Direction.NORTH);

                try {
                    // 1. CHỜ Ở VẠCH XUẤT PHÁT: Tất cả 100 luồng sẽ bị block ở đây
                    startGate.await();

                    // 2. TRANH CHẤP TÀI NGUYÊN: 100 luồng cùng gọi hàm này một lúc
                    intersection.requestEntry(vehicle);

                    // --- BẮT ĐẦU VÀO GIAO LỘ (Critical Section Test) ---
                    int currentActive = activeVehiclesInIntersection.incrementAndGet();

                    // Trong `Intersection.java`, MAX_VEHICLES = 3.
                    // Nếu Lock hoạt động không đúng, số lượng xe lọt vào sẽ > 3.
                    if (currentActive > 3) {
                        collisionOccurred.set(true);
                        throw new CollisionException("Va chạm! Có " + currentActive + " xe trong ngã tư cùng lúc!");
                    }

                    // Giả lập thời gian xe đang lăn bánh qua ngã tư
                    Thread.sleep(10);

                    activeVehiclesInIntersection.decrementAndGet();
                    // --- KẾT THÚC VÙNG GIAO LỘ ---

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (CollisionException e) {
                    System.err.println(e.getMessage());
                } finally {
                    // 3. RỜI NGÃ TƯ VÀ BÁO CÁO HOÀN THÀNH
                    intersection.exit(vehicle);
                    endGate.countDown();
                }
            });
        }

        // BẮT ĐẦU: Nhả chốt startGate để 100 luồng cùng lao lên
        System.out.println("BẮT ĐẦU STRESS TEST: Nhả chốt cho 100 xe...");
        startGate.countDown();

        // Chờ toàn bộ 100 xe xử lý xong (thoát khỏi ngã tư)
        endGate.await();
        executor.shutdown();

        // ĐÁNH GIÁ KẾT QUẢ
        assertFalse(collisionOccurred.get(), "Stress test thất bại: ReentrantLock không ngăn được Race Condition!");
        System.out.println("STRESS TEST THÀNH CÔNG: ReentrantLock hoạt động hoàn hảo, không có va chạm!");
    }
}
# Cơ chế xử lý Deadlock và Đồng bộ hóa (Synchronization)

Trong môi trường đa luồng (Multithreading) với hàng trăm phương tiện cùng hoạt động, việc tranh chấp tài nguyên tại vùng **Ngã tư (Intersection)** rất dễ dẫn đến các sự cố nghiêm trọng như **Race Condition** (Tranh chấp điều khiển) và **Deadlock** (Bế tắc).

Hệ thống mô phỏng này được thiết kế chặt chẽ để hoàn toàn loại bỏ nguy cơ Deadlock thông qua các cơ chế sau:



## 1. Loại bỏ "Circular Wait" (Chờ đợi vòng tròn) bằng Đơn giản hóa Khóa
* **Vấn đề:** Deadlock cổ điển xảy ra khi Xe A giữ Khóa 1 và đợi Khóa 2, trong khi Xe B giữ Khóa 2 và đợi Khóa 1.
* **Giải pháp trong hệ thống:** Lớp `Intersection` được thiết kế theo mẫu **Monitor Object**. Tại đây, chúng ta chỉ sử dụng **một khóa duy nhất** (`private final ReentrantLock lock = new ReentrantLock(true);`) để bảo vệ toàn bộ trạng thái của ngã tư (bao gồm `currentVehicles`, `waitingQueue`, `priorityQueue`). Việc một luồng (Thread) chỉ cần xin cấp phát một khóa duy nhất để thực thi tiến trình giúp triệt tiêu hoàn toàn rủi ro khóa chéo (Circular Wait).

## 2. Giải quyết "Hold and Wait" bằng Biến điều kiện (Condition Variables)
* **Vấn đề:** Nếu một xe tiến vào ngã tư nhưng nhận thấy ngã tư đã đầy (`currentVehicles >= MAX_VEHICLES`), nó không được phép tiếp tục giữ khóa (`lock`) rồi đứng chờ vô thời hạn. Nếu làm vậy, các xe khác đang ở trong ngã tư cũng không thể trả khóa khi thoát ra (do khóa đang bị xe kia ôm chặt), dẫn đến toàn hệ thống bị "đóng băng".
* **Giải pháp trong hệ thống:** Áp dụng `Condition condition = lock.newCondition();`.
  Khi hàm `canEnter(vehicle)` trả về `false`, luồng hiện tại sẽ gọi `condition.await()`. Lệnh này có tác dụng **tạm thời nhả khóa (release lock)** và đưa luồng vào trạng thái chờ (Waiting state). Điều này cho phép các xe đang ở trong ngã tư có thể chạy tiếp và gọi hàm `exit()`. Khi một xe rời đi, nó gọi `condition.signalAll()` để đánh thức các xe đang chờ tỉnh dậy và cấp lại khóa cho chúng.

## 3. Chống rò rỉ Khóa (Lock Leakage) bằng Try-Finally
* **Vấn đề:** Nếu một xe (luồng) đang giữ khóa nhưng đột ngột gặp lỗi (Exception) và crash, khóa đó sẽ vĩnh viễn không bao giờ được trả lại. Các xe khác sẽ chờ đợi mãi mãi (Deadlock hệ thống).
* **Giải pháp trong hệ thống:** Mọi thao tác tranh chấp tài nguyên đều được bọc trong khối `try-finally`:
  ```java
  lock.lock();
  try {
      // Logic xử lý vào/ra ngã tư
  } finally {
      lock.unlock(); // Luôn luôn nhả khóa dù có Exception xảy ra
  }
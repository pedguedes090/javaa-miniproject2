package factory;

import enums.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vehicle.PriorityVehicle;
import vehicle.StandardVehicle;
import vehicle.Vehicle;

import java.util.EnumMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VehicleFactoryTest {

    private VehicleFactory defaultFactory;

    @BeforeEach
    void setUp() {
        defaultFactory = new VehicleFactory();
    }

    @Test
    void testCreateSingleRandomVehicle() {
        Vehicle vehicle = defaultFactory.createRandomVehicle();

        assertNotNull(vehicle, "Factory không được trả về null");
        assertNotNull(vehicle.getId(), "ID của xe không được null");
        assertNotNull(vehicle.getVehicleType(), "Loại xe không được null");
        assertNotNull(vehicle.getDirection(), "Hướng di chuyển không được null");

        // Kiểm tra tiền tố của ID có khớp với chữ cái đầu của loại xe không (VD: C0001 cho CAR)
        String expectedPrefix = vehicle.getVehicleType().name().substring(0, 1);
        assertTrue(vehicle.getId().startsWith(expectedPrefix), "ID xe phải bắt đầu bằng chữ cái đầu của VehicleType");
    }

    @Test
    void testCreateBatchVehicles() {
        int count = 10;
        List<Vehicle> vehicles = defaultFactory.createRandomVehicles(count);

        assertNotNull(vehicles);
        assertEquals(count, vehicles.size(), "Factory phải sinh ra đúng số lượng xe yêu cầu");
    }

    @Test
    void testCreateBatchVehicles_ThrowsExceptionOnNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            defaultFactory.createRandomVehicles(-5);
        }, "Phải ném ngoại lệ khi truyền số lượng xe âm");
    }

    @Test
    void testForceSpawnAmbulance_HasHighPriority() {
        // Cấu hình trọng số: 100% ra xe cứu thương
        EnumMap<VehicleType, Integer> weights = new EnumMap<>(VehicleType.class);
        weights.put(VehicleType.AMBULANCE, 100);
        weights.put(VehicleType.CAR, 0);
        weights.put(VehicleType.MOTORBIKE, 0);
        weights.put(VehicleType.TRUCK, 0);

        VehicleFactory ambulanceFactory = new VehicleFactory(weights);
        Vehicle vehicle = ambulanceFactory.createRandomVehicle();

        assertEquals(VehicleType.AMBULANCE, vehicle.getVehicleType(), "Xe sinh ra phải là AMBULANCE");
        assertTrue(vehicle instanceof PriorityVehicle, "Xe cứu thương phải là instance của PriorityVehicle");
        assertTrue(vehicle.canPassRedLight(), "Xe cứu thương phải được phép vượt đèn đỏ");
    }

    @Test
    void testForceSpawnCar_IsStandardVehicle() {
        // Cấu hình trọng số: 100% ra xe ô tô (CAR)
        EnumMap<VehicleType, Integer> weights = new EnumMap<>(VehicleType.class);
        weights.put(VehicleType.CAR, 100);

        VehicleFactory carFactory = new VehicleFactory(weights);
        Vehicle vehicle = carFactory.createRandomVehicle();

        assertEquals(VehicleType.CAR, vehicle.getVehicleType(), "Xe sinh ra phải là CAR");
        assertTrue(vehicle instanceof StandardVehicle, "Xe ô tô phải là instance của StandardVehicle");
        assertFalse(vehicle.canPassRedLight(), "Xe ô tô thông thường không được vượt đèn đỏ");
    }

    @Test
    void testFactoryThrowsExceptionOnInvalidSpeedBounds() {
        EnumMap<VehicleType, Integer> weights = new EnumMap<>(VehicleType.class);
        weights.put(VehicleType.CAR, 10);

        // Min speed > Max speed
        assertThrows(IllegalArgumentException.class, () -> {
            new VehicleFactory(weights, 80, 20);
        }, "Phải ném ngoại lệ khi minSpeed > maxSpeed");

        // Negative speed
        assertThrows(IllegalArgumentException.class, () -> {
            new VehicleFactory(weights, -10, 50);
        }, "Phải ném ngoại lệ khi tốc độ âm");
    }
}
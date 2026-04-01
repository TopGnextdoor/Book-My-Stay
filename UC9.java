import java.util.*;

class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public void validateRoomType(String type) throws InvalidBookingException {
        if (!inventory.containsKey(type)) {
            throw new InvalidBookingException("Invalid room type: " + type);
        }
    }

    public void validateAvailability(String type) throws InvalidBookingException {
        if (inventory.get(type) <= 0) {
            throw new InvalidBookingException("No availability for room type: " + type);
        }
    }

    public void decrement(String type) throws InvalidBookingException {
        int count = inventory.get(type);
        if (count <= 0) {
            throw new InvalidBookingException("Cannot allocate. Inventory is already zero for: " + type);
        }
        inventory.put(type, count - 1);
    }

    public void displayInventory() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

class BookingService {
    private RoomInventory inventory;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public void bookRoom(String guest, String type) {
        try {
            inventory.validateRoomType(type);
            inventory.validateAvailability(type);
            inventory.decrement(type);

            System.out.println("Booking Confirmed → Guest: " + guest + " | Room: " + type);

        } catch (InvalidBookingException e) {
            System.out.println("Booking Failed → Guest: " + guest + " | Reason: " + e.getMessage());
        }
    }
}

public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v9.0");
        System.out.println("=======================================\n");

        RoomInventory inventory = new RoomInventory();
        BookingService service = new BookingService(inventory);

        service.bookRoom("Alice", "Single Room");
        service.bookRoom("Bob", "Luxury Room");
        service.bookRoom("Charlie", "Suite Room");
        service.bookRoom("Diana", "Suite Room");

        inventory.displayInventory();

        System.out.println("\nApplication executed successfully!");
    }
}

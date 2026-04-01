import java.util.*;

class RoomInventory {
    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public void increment(String type) {
        inventory.put(type, inventory.get(type) + 1);
    }

    public void display() {
        System.out.println("\nCurrent Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

class BookingRecord {
    private String guest;
    private String roomType;
    private String roomId;

    public BookingRecord(String guest, String roomType, String roomId) {
        this.guest = guest;
        this.roomType = roomType;
        this.roomId = roomId;
    }

    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
}

class BookingService {
    private RoomInventory inventory;
    private Map<String, BookingRecord> bookings = new HashMap<>();
    private Stack<String> rollbackStack = new Stack<>();
    private int counter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    private String generateRoomId(String type) {
        return type.replace(" ", "").toUpperCase() + "_" + (counter++);
    }

    public void book(String guest, String type) {
        if (inventory.getAvailability(type) <= 0) {
            System.out.println("Booking Failed → " + guest + " (" + type + ")");
            return;
        }

        String roomId = generateRoomId(type);
        inventory.decrement(type);

        BookingRecord record = new BookingRecord(guest, type, roomId);
        bookings.put(roomId, record);

        System.out.println("Booking Confirmed → " + guest + " | " + type + " | ID: " + roomId);
    }

    public void cancel(String roomId) {
        if (!bookings.containsKey(roomId)) {
            System.out.println("Cancellation Failed → Invalid Room ID: " + roomId);
            return;
        }

        BookingRecord record = bookings.remove(roomId);

        rollbackStack.push(roomId);
        inventory.increment(record.getRoomType());

        System.out.println("Cancelled → " + record.getGuest() +
                " | " + record.getRoomType() +
                " | ID: " + roomId);
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent Cancellations):");
        for (String id : rollbackStack) {
            System.out.println(id);
        }
    }
}

public class UseCase10BookingCancellation {
    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v10.0");
        System.out.println("=======================================\n");

        RoomInventory inventory = new RoomInventory();
        BookingService service = new BookingService(inventory);

        service.book("Alice", "Single Room");
        service.book("Bob", "Double Room");
        service.book("Charlie", "Suite Room");

        service.cancel("SINGLEROOM_1");
        service.cancel("INVALID_ID");

        inventory.display();
        service.showRollbackStack();

        System.out.println("\nApplication executed successfully!");
    }
}

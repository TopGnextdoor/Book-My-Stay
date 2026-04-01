import java.io.*;
import java.util.*;

class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public HashMap<String, Integer> getInventory() {
        return inventory;
    }

    public void setInventory(HashMap<String, Integer> inventory) {
        this.inventory = inventory;
    }

    public void display() {
        System.out.println("\nInventory State:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

class BookingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private String guest;
    private String roomType;

    public BookingRecord(String guest, String roomType) {
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;
    private RoomInventory inventory;
    private List<BookingRecord> bookings;

    public SystemState(RoomInventory inventory, List<BookingRecord> bookings) {
        this.inventory = inventory;
        this.bookings = bookings;
    }

    public RoomInventory getInventory() { return inventory; }
    public List<BookingRecord> getBookings() { return bookings; }
}

class PersistenceService {
    private static final String FILE_NAME = "system_state.dat";

    public void save(SystemState state) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(state);
            System.out.println("State saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state.");
        }
    }

    public SystemState load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("State loaded successfully.");
            return (SystemState) in.readObject();
        } catch (Exception e) {
            System.out.println("No previous state found. Starting fresh.");
            return null;
        }
    }
}

public class UseCase12DataPersistenceRecovery {
    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v12.0");
        System.out.println("=======================================\n");

        PersistenceService persistence = new PersistenceService();
        SystemState state = persistence.load();

        RoomInventory inventory;
        List<BookingRecord> bookings;

        if (state != null) {
            inventory = state.getInventory();
            bookings = state.getBookings();
        } else {
            inventory = new RoomInventory();
            bookings = new ArrayList<>();
        }

        bookings.add(new BookingRecord("Alice", "Single Room"));
        inventory.getInventory().put("Single Room", inventory.getInventory().get("Single Room") - 1);

        bookings.add(new BookingRecord("Bob", "Double Room"));
        inventory.getInventory().put("Double Room", inventory.getInventory().get("Double Room") - 1);

        inventory.display();

        System.out.println("\nBookings:");
        for (BookingRecord b : bookings) {
            System.out.println(b.getGuest() + " → " + b.getRoomType());
        }

        SystemState newState = new SystemState(inventory, bookings);
        persistence.save(newState);

        System.out.println("\nApplication executed successfully!");
    }
}

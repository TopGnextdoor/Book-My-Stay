import java.util.*;

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

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
}

class RoomAllocationService {
    private RoomInventory inventory;
    private Set<String> allocatedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> roomTypeToIds = new HashMap<>();
    private int counter = 1;

    public RoomAllocationService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    private String generateRoomId(String type) {
        return type.replace(" ", "").toUpperCase() + "_" + (counter++);
    }

    public void process(BookingRequestQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.getNextRequest();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {
                String roomId = generateRoomId(type);

                while (allocatedRoomIds.contains(roomId)) {
                    roomId = generateRoomId(type);
                }

                allocatedRoomIds.add(roomId);

                roomTypeToIds.putIfAbsent(type, new HashSet<>());
                roomTypeToIds.get(type).add(roomId);

                inventory.decrement(type);

                System.out.println("Booking Confirmed → Guest: " + r.getGuestName() +
                        " | Room: " + type + " | Room ID: " + roomId);
            } else {
                System.out.println("Booking Failed → Guest: " + r.getGuestName() +
                        " | Room: " + type + " (Not Available)");
            }
        }
    }
}

public class UseCase6RoomAllocationService {
    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v6.0");
        System.out.println("=======================================\n");

        BookingRequestQueue queue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService service = new RoomAllocationService(inventory);

        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Single Room"));
        queue.addRequest(new Reservation("Charlie", "Single Room"));
        queue.addRequest(new Reservation("Diana", "Suite Room"));
        queue.addRequest(new Reservation("Eve", "Suite Room"));

        service.process(queue);

        System.out.println("\nApplication executed successfully!");
    }
}

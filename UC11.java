import java.util.*;

class Reservation {
    private String guest;
    private String roomType;

    public Reservation(String guest, String roomType) {
        this.guest = guest;
        this.roomType = roomType;
    }

    public String getGuest() { return guest; }
    public String getRoomType() { return roomType; }
}

class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.offer(r);
    }

    public synchronized Reservation get() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 2);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room", 1);
    }

    public synchronized boolean allocate(String type) {
        int count = inventory.getOrDefault(type, 0);
        if (count > 0) {
            inventory.put(type, count - 1);
            return true;
        }
        return false;
    }

    public synchronized void display() {
        System.out.println("\nFinal Inventory:");
        for (Map.Entry<String, Integer> e : inventory.entrySet()) {
            System.out.println(e.getKey() + " → " + e.getValue());
        }
    }
}

class BookingProcessor extends Thread {
    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(BookingQueue queue, RoomInventory inventory) {
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run() {
        while (true) {
            Reservation r;

            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.get();
            }

            if (r != null) {
                boolean success = inventory.allocate(r.getRoomType());

                if (success) {
                    System.out.println(Thread.currentThread().getName() +
                            " → Confirmed: " + r.getGuest() + " | " + r.getRoomType());
                } else {
                    System.out.println(Thread.currentThread().getName() +
                            " → Failed: " + r.getGuest() + " | " + r.getRoomType());
                }
            }
        }
    }
}

public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {

        System.out.println("=======================================");
        System.out.println("   Welcome to Book My Stay App");
        System.out.println("   Hotel Booking System v11.0");
        System.out.println("=======================================\n");

        BookingQueue queue = new BookingQueue();
        RoomInventory inventory = new RoomInventory();

        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Single Room"));
        queue.add(new Reservation("Charlie", "Single Room"));
        queue.add(new Reservation("Diana", "Suite Room"));
        queue.add(new Reservation("Eve", "Suite Room"));

        BookingProcessor t1 = new BookingProcessor(queue, inventory);
        BookingProcessor t2 = new BookingProcessor(queue, inventory);
        BookingProcessor t3 = new BookingProcessor(queue, inventory);

        t1.setName("Thread-1");
        t2.setName("Thread-2");
        t3.setName("Thread-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inventory.display();

        System.out.println("\nApplication executed successfully!");
    }
}

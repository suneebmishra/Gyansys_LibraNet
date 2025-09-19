import java.util.*;
import java.time.*;

// --- Base Class ---
abstract class LibraryItem {
    protected int itemId;
    protected String title;
    protected String author;
    protected boolean isBorrowed;

    public LibraryItem(int itemId, String title, String author) {
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void borrow() {
        isBorrowed = true;
    }

    public void returnItem() {
        isBorrowed = false;
    }

    public abstract String getDetails();
}

// --- Book ---
class Book extends LibraryItem {
    private int pageCount;

    public Book(int itemId, String title, String author, int pageCount) {
        super(itemId, title, author);
        this.pageCount = pageCount;
    }

    @Override
    public String getDetails() {
        String availability = isBorrowed ? "Borrowed" : "Available";
        return "[Book] ID: " + itemId + ", Title: " + title + ", Author: " + author +
               ", Pages: " + pageCount + ", Status: " + availability;
    }
}

// --- Audiobook ---
class Audiobook extends LibraryItem {
    private int durationMinutes;

    public Audiobook(int itemId, String title, String author, int durationMinutes) {
        super(itemId, title, author);
        this.durationMinutes = durationMinutes;
    }

    public String getDuration() {
        int hours = durationMinutes / 60;
        int minutes = durationMinutes % 60;
        return hours + "h " + minutes + "m";
    }

    public void play() {
        System.out.println("Playing '" + title + "'...");
    }

    @Override
    public String getDetails() {
        String availability = isBorrowed ? "Borrowed" : "Available";
        return "[Audiobook] ID: " + itemId + ", Title: " + title + ", Author: " + author +
               ", Duration: " + getDuration() + ", Status: " + availability;
    }
}

// --- EMagazine ---
class EMagazine extends LibraryItem {
    private int issueNumber;
    private boolean isArchived;

    public EMagazine(int itemId, String title, String publisher, int issueNumber) {
        super(itemId, title, publisher);
        this.issueNumber = issueNumber;
        this.isArchived = false;
    }

    public void archiveIssue() {
        isArchived = true;
        System.out.println("EMagazine '" + title + "' Issue " + issueNumber + " has been archived.");
    }

    @Override
    public String getDetails() {
        String availability = isBorrowed ? "Borrowed" : "Available";
        return "[E-Magazine] ID: " + itemId + ", Title: " + title + ", Publisher: " + author +
               ", Issue: " + issueNumber + ", Status: " + availability;
    }
}

// --- LibraNet System ---
class LibraNet {
    private static final double FINE_PER_DAY = 10.0; // in rupees
    private Map<Integer, LibraryItem> items;
    private Map<Integer, LocalDate> borrowedRecords;

    public LibraNet() {
        items = new HashMap<>();
        borrowedRecords = new HashMap<>();
    }

    public void addItem(LibraryItem item) {
        items.put(item.itemId, item);
        System.out.println("Added '" + item.title + "' to the library.");
    }

    // Normal borrowing (uses today's date)
    public void borrowItem(int itemId, int durationDays) {
        borrowItem(itemId, durationDays, LocalDate.now());
    }

    // Overloaded: Borrow with custom borrow date (for testing overdue)
    public void borrowItem(int itemId, int durationDays, LocalDate borrowDate) {
        if (!items.containsKey(itemId)) {
            System.out.println("Error: No item found with ID: " + itemId);
            return;
        }

        LibraryItem item = items.get(itemId);
        if (item.isBorrowed()) {
            System.out.println("Error: '" + item.title + "' is already borrowed.");
            return;
        }

        item.borrow();
        LocalDate dueDate = borrowDate.plusDays(durationDays);
        borrowedRecords.put(itemId, dueDate);

        System.out.println("'" + item.title + "' has been borrowed on " + borrowDate +
                           ". It is due on " + dueDate + ".");
    }

    public void returnItem(int itemId) {
        if (!items.containsKey(itemId)) {
            System.out.println("Error: No item found with ID: " + itemId);
            return;
        }

        LibraryItem item = items.get(itemId);
        if (!item.isBorrowed()) {
            System.out.println("Error: '" + item.title + "' is not currently borrowed.");
            return;
        }

        LocalDate dueDate = borrowedRecords.get(itemId);
        LocalDate today = LocalDate.now();

        if (today.isAfter(dueDate)) {
            long daysOverdue = Duration.between(dueDate.atStartOfDay(), today.atStartOfDay()).toDays();
            double fine = daysOverdue * FINE_PER_DAY;
            System.out.printf("A fine of Rs %.2f is due for the late return of '%s'.%n", fine, item.title);
        }

        item.returnItem();
        borrowedRecords.remove(itemId);
        System.out.println("'" + item.title + "' has been successfully returned.");
    }

    public void listAllItems() {
        System.out.println("\n--- LibraNet Catalog ---");
        for (LibraryItem item : items.values()) {
            System.out.println(item.getDetails());
        }
        System.out.println("------------------------");
    }

    // Shown borrowed items and due dates
    public void listBorrowedItems() {
        System.out.println("\n--- Borrowed Items ---");
        if (borrowedRecords.isEmpty()) {
            System.out.println("No items are currently borrowed.");
        } else {
            for (int id : borrowedRecords.keySet()) {
                LibraryItem item = items.get(id);
                LocalDate dueDate = borrowedRecords.get(id);
                String status = (LocalDate.now().isAfter(dueDate)) ? "Overdue" : "On Time";
                System.out.println(item.getDetails() + " | Due: " + dueDate + " (" + status + ")");
            }
        }
        System.out.println("-----------------------");
    }
}

// --- Main Execution ---
public class Main {
    public static void main(String[] args) {
        LibraNet libraNet = new LibraNet();

        System.out.println("--- Populating the Library ---");
        Book book1 = new Book(110, "Harry Potter and the Prisoner of Azkaban", "J.K. Rowling", 435);
        Book book2 = new Book(115, "The 3 Mistakes of My Life", "Chetan Bhagat", 258);
        Audiobook audiobook1 = new Audiobook(210, "The Lord of the Rings: The Fellowship of the Ring", "J.R.R. Tolkien", 1257);
        EMagazine emagazine1 = new EMagazine(310, "National Geographic Traveller India", "ACK Media", 150);

        libraNet.addItem(book1);
        libraNet.addItem(book2);
        libraNet.addItem(audiobook1);
        libraNet.addItem(emagazine1);

        libraNet.listAllItems();

        System.out.println("\n--- Borrowing Items ---");
        // Borrowing different items with different durations
        libraNet.borrowItem(115, 7); // Normal borrow for 1 week
        libraNet.borrowItem(210, 20, LocalDate.now().minusDays(25)); // Overdue borrow
        
        // Let's also borrow the e-magazine
        libraNet.borrowItem(310, 10); 
        libraNet.listBorrowedItems();

        System.out.println("\n--- Returning Items ---");
        libraNet.returnItem(115); // On-time return
        libraNet.returnItem(210); // Overdue return with a fine
        
        // Try returning an item that isn't borrowed to test error handling
        System.out.println("\n--- Testing Error Case ---");
        libraNet.returnItem(110);
        
        System.out.println("\n--- Final Library Status ---");
        libraNet.listBorrowedItems();
        libraNet.listAllItems();
    }
}
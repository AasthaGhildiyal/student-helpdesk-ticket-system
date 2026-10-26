import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Beginner-level Student Helpdesk / Ticket Management System.
 * Save as Main.java, then run with: javac Main.java && java Main
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Helpdesk helpdesk = new Helpdesk();

    public static void main(String[] args) {
        seedCategories();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");

            switch (choice) {
                case 1:
                    createTicket();
                    break;
                case 2:
                    viewAllTickets();
                    break;
                case 3:
                    searchTicket();
                    break;
                case 4:
                    updateTicket();
                    break;
                case 5:
                    resolveTicket();
                    break;
                case 6:
                    System.out.println("\nAvailable categories:");
                    helpdesk.showCategories();
                    break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using the Student Helpdesk System!");
                    break;
                default:
                    System.out.println("Invalid menu option. Please choose a number from 0 to 6.");
            }
        }
        scanner.close();
    }

    private static void seedCategories() {
        helpdesk.addCategory(new Category("IT Support", "Wi-Fi, login, computer or software issues"));
        helpdesk.addCategory(new Category("Academics", "Assignments, timetable or course-related issues"));
        helpdesk.addCategory(new Category("Facilities", "Classroom, library or campus facilities"));
        helpdesk.addCategory(new Category("Other", "Any issue not covered by the other categories"));
    }

    private static void printMenu() {
        System.out.println("\n==================================");
        System.out.println("   STUDENT HELPDESK TICKET SYSTEM");
        System.out.println("==================================");
        System.out.println("1. Create a ticket");
        System.out.println("2. View all tickets");
        System.out.println("3. Search/view a ticket by ID");
        System.out.println("4. Update an open ticket");
        System.out.println("5. Resolve/close a ticket");
        System.out.println("6. View categories");
        System.out.println("0. Exit");
    }

    private static void createTicket() {
        System.out.println("\n--- Create Ticket ---");
        String studentName = readNonEmpty("Student name: ");
        String studentId = readNonEmpty("Student ID: ");
        String title = readNonEmpty("Ticket title: ");
        String description = readNonEmpty("Description: ");

        System.out.println("\nChoose a category:");
        helpdesk.showCategories();
        String categoryName = readNonEmpty("Category name: ");
        Category category = helpdesk.findCategory(categoryName);
        if (category == null) {
            System.out.println("That category does not exist. Ticket was not created.");
            return;
        }

        Priority priority = choosePriority();
        Student student = new Student(studentName, studentId);
        Ticket ticket = helpdesk.createTicket(student, title, description, category, priority);

        if (ticket == null) {
            System.out.println("Ticket could not be created because the title or details were invalid.");
        } else {
            System.out.println("Ticket created successfully. Your ticket ID is: " + ticket.getId());
        }
    }

    private static Priority choosePriority() {
        while (true) {
            System.out.println("Priority: 1. LOW  2. MEDIUM  3. HIGH");
            int choice = readInt("Choose priority: ");
            switch (choice) {
                case 1: return Priority.LOW;
                case 2: return Priority.MEDIUM;
                case 3: return Priority.HIGH;
                default: System.out.println("Invalid priority. Choose 1, 2 or 3.");
            }
        }
    }

    private static void viewAllTickets() {
        System.out.println("\n--- All Tickets ---");
        List<Ticket> tickets = helpdesk.getAllTickets();
        if (tickets.isEmpty()) {
            System.out.println("No tickets have been created yet.");
            return;
        }
        for (Ticket ticket : tickets) {
            ticket.displaySummary();
        }
    }

    private static void searchTicket() {
        System.out.println("\n--- Search Ticket ---");
        int id = readInt("Enter ticket ID: ");
        Ticket ticket = helpdesk.findTicket(id);
        if (ticket == null) {
            System.out.println("No ticket found with ID " + id + ".");
        } else {
            ticket.displayDetails();
        }
    }

    private static void updateTicket() {
        System.out.println("\n--- Update Ticket ---");
        int id = readInt("Enter ticket ID: ");
        Ticket ticket = helpdesk.findTicket(id);

        if (ticket == null) {
            System.out.println("No ticket found with ID " + id + ".");
            return;
        }
        if (ticket.getStatus() != TicketStatus.OPEN) {
            System.out.println("Only OPEN tickets can be updated. Current status: " + ticket.getStatus());
            return;
        }

        String newTitle = readNonEmpty("New title: ");
        String newDescription = readNonEmpty("New description: ");
        ticket.updateDetails(newTitle, newDescription);
        System.out.println("Ticket updated successfully.");
    }

    private static void resolveTicket() {
        System.out.println("\n--- Resolve Ticket ---");
        int id = readInt("Enter ticket ID: ");
        Ticket ticket = helpdesk.findTicket(id);

        if (ticket == null) {
            System.out.println("No ticket found with ID " + id + ".");
            return;
        }
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            System.out.println("This ticket is already resolved.");
            return;
        }

        ticket.resolve();
        System.out.println("Ticket #" + ticket.getId() + " has been marked as RESOLVED.");
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty. Please try again.");
        }
    }
}

enum TicketStatus {
    OPEN,
    RESOLVED
}

enum Priority {
    LOW,
    MEDIUM,
    HIGH
}

class Student {
    private final String name;
    private final String studentId;

    public Student(String name, String studentId) {
        this.name = name;
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public String getStudentId() {
        return studentId;
    }
}

class Category {
    private final String name;
    private final String description;

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

class Ticket {
    private final int id;
    private final Student student;
    private String title;
    private String description;
    private final Category category;
    private final Priority priority;
    private TicketStatus status;

    public Ticket(int id, Student student, String title, String description,
                  Category category, Priority priority) {
        this.id = id;
        this.student = student;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = TicketStatus.OPEN;
    }

    public int getId() {
        return id;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void updateDetails(String title, String description) {
        if (status != TicketStatus.OPEN) {
            throw new IllegalStateException("Resolved tickets cannot be updated.");
        }
        if (title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Title and description cannot be empty.");
        }
        this.title = title.trim();
        this.description = description.trim();
    }

    public void resolve() {
        status = TicketStatus.RESOLVED;
    }

    public void displaySummary() {
        System.out.println("#" + id + " | " + title + " | " + category.getName()
                + " | " + priority + " | " + status + " | Student: " + student.getName());
    }

    public void displayDetails() {
        System.out.println("\nTicket ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Student: " + student.getName());
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Category: " + category.getName());
        System.out.println("Priority: " + priority);
        System.out.println("Status: " + status);
    }
}

class Helpdesk {
    private final List<Ticket> tickets = new ArrayList<>();
    private final List<Category> categories = new ArrayList<>();
    private int nextTicketId = 1001;

    public void addCategory(Category category) {
        if (category != null && findCategory(category.getName()) == null) {
            categories.add(category);
        }
    }

    public Category findCategory(String name) {
        if (name == null) return null;
        for (Category category : categories) {
            if (category.getName().equalsIgnoreCase(name.trim())) {
                return category;
            }
        }
        return null;
    }

    public void showCategories() {
        for (Category category : categories) {
            System.out.println("- " + category.getName() + ": " + category.getDescription());
        }
    }

    public Ticket createTicket(Student student, String title, String description,
                               Category category, Priority priority) {
        if (student == null || title == null || title.trim().isEmpty()
                || description == null || description.trim().isEmpty()
                || category == null || priority == null) {
            return null;
        }

        // IDs are generated centrally, so duplicate ticket IDs are avoided.
        Ticket ticket = new Ticket(nextTicketId++, student, title.trim(),
                description.trim(), category, priority);
        tickets.add(ticket);
        return ticket;
    }

    public Ticket findTicket(int id) {
        for (Ticket ticket : tickets) {
            if (ticket.getId() == id) {
                return ticket;
            }
        }
        return null;
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets);
    }
}

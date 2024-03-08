package org.example;

import java.util.*;
import java.util.Calendar;
import java.util.Date;

class Member {
    private String name;
    private int age;
    private String memberID;
    private double fines;
    private List<Book> borrowedBooks;

    public Member(String name, int age, String memberID) {
        this.name = name;
        this.age = age;
        this.memberID = memberID;
        this.fines = 0.0;
        this.borrowedBooks = new ArrayList<>();
    }

    public void payFine(double amount) {
        if (amount > 0) {
            fines -= amount;
            if (fines < 0) {
                fines = 0;
            }
        }
    }

    public void borrowBook(Book book) {
        if (book != null && !borrowedBooks.contains(book)) {
            borrowedBooks.add(book);
        }
    }

    public double getFines() {
        return fines;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getMemberDetails() {
        return "Name: " + name + ", Age: " + age + ", Member Number: " + memberID;
    }

    public String getName() {
        return name;
    }

    public void returnBook(Book book) {
        if (book != null && borrowedBooks.contains(book)) {
            book.setBorrowed(false);

            // Calculate the number of days overdue
            Date currentDate = new Date();
            long overdueMillis = currentDate.getTime() - book.getDueDate().getTime();
            long overdueDays = overdueMillis / (24 * 60 * 60 * 1000);

            if (overdueDays > 0) {
                // Calculate and apply the fine
                double fineAmount = overdueDays * 3; // 3 rupees per day
                payFine(fineAmount);
                System.out.println("Book titled '" + book.getTitle() + "' has been returned by " + getMemberDetails());
                System.out.println("Fine for overdue: $" + fineAmount);
            } else {
                System.out.println("Book titled '" + book.getTitle() + "' has been returned by " + getMemberDetails());
            }

            borrowedBooks.remove(book);
            book.setQuantity(book.getQuantity() + 1);
        } else {
            System.out.println("You did not borrow the book.");
        }
    }
}

class Book {
    private static int bookIDCounter = 1;
    private static List<Book> books = new ArrayList<>();

    private int bookID;
    private String title;
    private String author;
    private int quantity;
    private boolean borrowed;
    private Date dueDate;

    public Book(String title, String author, int quantity) {
        this.bookID = bookIDCounter++;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
        this.borrowed = false;

        // Calculate and set the due date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 10); // Due date is 10 days from today
        calendar.set(Calendar.HOUR_OF_DAY, 0); // Set the time to midnight (0 hours)
        calendar.set(Calendar.MINUTE, 0); // Set the minutes to 0
        calendar.set(Calendar.SECOND, 0); // Set the seconds to 0
        calendar.set(Calendar.MILLISECOND, 0); // Set the milliseconds to 0
        dueDate = calendar.getTime();
    }

    public int getBookID() {
        return bookID;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public static List<Book> getAllBooks() {
        return books;
    }

    public static void addBook(Book book) {
        books.add(book);
    }

    public static Book findBookById(int bookIdToRemove) {
        for (Book book : books) {
            if (book.bookID == bookIdToRemove) {
                return book;
            }
        }
        return null;
    }

    public static void removeBookById(int bookIdToRemove) {
        Book bookToRemove = findBookById(bookIdToRemove);

        if (bookToRemove != null) {
            books.remove(bookToRemove);
            System.out.println("Book with ID " + bookIdToRemove + " has been removed.");
        } else {
            System.out.println("Book with ID " + bookIdToRemove + " not found.");
        }
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

class Library {
    private List<Book> books;
    private List<Member> members;
    private Member loggedInMember;

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.loggedInMember = null;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int choice;
        Library library = new Library();

        do {
            if (library.loggedInMember == null) {
                // Main menu
                System.out.println("Library Portal Initialized….");
                System.out.println("---------------------------------");
                System.out.println("1. Enter as a librarian");
                System.out.println("2. Enter as a member");
                System.out.println("3. Exit");
                System.out.println("---------------------------------");

                choice = input.nextInt(); // scans input
                input.nextLine();
                switch (choice) {
                    case 1:
                        // Librarian menu
                        library.showLibrarianMenu(input);
                        break;
                    case 2:
                        // Member login
                        library.memberLogin(input);
                        break;
                    case 3:
                        System.out.println("Thank you for visiting.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                        break;
                }
            } else {
                // Member menu
                library.showMemberMenu(input);
            }
        } while (true);
    }

    public void showLibrarianMenu(Scanner input) {
        int choice;
        do {
            System.out.println("Librarian Menu:");
            System.out.println("1. Register a member");
            System.out.println("2. Remove a member");
            System.out.println("3. Add a book");
            System.out.println("4. Remove a book");
            System.out.println("5. View all members along with their books and fines");
            System.out.println("6. View all books");
            System.out.println("7. Back");

            choice = input.nextInt();
            input.nextLine();
            switch (choice) {
                case 1 -> registerMember(input);
                case 2 -> removeMember(input);
                case 3 -> {
                    // Read book details from the user
                    System.out.print("Enter the title of the book: ");
                    String title = input.nextLine();
                    System.out.print("Enter the author of the book: ");
                    String author = input.nextLine();
                    System.out.print("Enter the quantity of the book: ");
                    int quantity = input.nextInt();
                    input.nextLine(); // Consume the newline character
                    Book newBook = new Book(title, author, quantity);
                    Book.addBook(newBook);
                    System.out.println("Book added successfully.");

                    // Display the updated list of all books
                    listAllBooks(); // Call the method to show the updated list
                    break;
                }
                case 4 -> {
                    System.out.print("Enter the ID of the book to remove: ");
                    int bookIdToRemove = input.nextInt();
                    input.nextLine(); // Consume the newline character
                    Book.removeBookById(bookIdToRemove);
                    break;
                }
                case 5 -> viewMembersWithBooksAndFines();
                case 6 -> listAllBooks();
                case 7 -> {
                    loggedInMember = null;
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (true);
    }

    public void registerMember(Scanner input) {
        System.out.println("Enter Name: ");
        String name = input.nextLine();
        System.out.println("Enter member age: ");
        int age = input.nextInt();
        input.nextLine();
        System.out.println("Enter Phone No.: ");
        String memberId = input.nextLine();
        Member newMember = new Member(name, age, memberId);
        members.add(newMember);
        System.out.println("Member registered successfully with " + memberId);
    }

    public void memberLogin(Scanner input) {
        System.out.print("Enter your name: ");
        String name = input.nextLine();
        Member existingMember = findMemberByName(name);

        if (existingMember != null) {
            loggedInMember = existingMember;
            System.out.println("Welcome back, " + name + "!");
        } else {
            System.out.println("Member not found. Registering as a new member.");
            registerMember(input);
        }
    }

    public void showMemberMenu(Scanner input) {
        int choice;
        do {
            System.out.println("Member Menu:");
            System.out.println("1. Issue a book");
            System.out.println("2. Return a book");
            System.out.println("3. View your borrowed books");
            System.out.println("4. Pay fines");
            System.out.println("5. View Available books");
            System.out.println("6. Back");

            choice = input.nextInt();
            input.nextLine(); // Consume the newline character

            switch (choice) {
                case 1 -> {
                    if (loggedInMember != null) {
                        issueBook(input);
                    } else {
                        System.out.println("Please log in first.");
                    }
                }
                case 2 -> {
                    if (loggedInMember != null) {
                        returnBook(input);
                    } else {
                        System.out.println("Please log in first.");
                    }
                }
                case 3 -> {
                    if (loggedInMember != null) {
                        viewBorrowedBooks(loggedInMember);
                    } else {
                        System.out.println("Please log in first.");
                    }
                }
                case 4 -> {
                    if (loggedInMember != null) {
                        System.out.print("Enter the amount to pay: $");
                        double amountToPay = input.nextDouble();
                        input.nextLine(); // Consume the newline character
                        payFine(amountToPay);
                    } else {
                        System.out.println("Please log in first.");
                    }
                }
                case 5 -> listAllBooks();
                case 6 -> {
                    loggedInMember = null;
                    return;
                }
                default -> System.out.println("Invalid choice. Please enter a valid option.");
            }
        } while (true);
    }

    public Member findMemberByName(String name) {
        for (Member member : members) {
            if (member.getName().equals(name)) {
                return member;
            }
        }
        return null;
    }

    public void removeMember(Scanner input) {
        System.out.print("Enter the member ID to remove: ");
        String memberIdToRemove = input.nextLine();

        Member memberToRemove = findMemberById(memberIdToRemove);

        if (memberToRemove != null) {
            members.remove(memberToRemove);
            System.out.println("Member with ID " + memberIdToRemove + " has been removed.");
        } else {
            System.out.println("Member with ID " + memberIdToRemove + " not found.");
        }
    }

    public Member findMemberById(String memberId) {
        for (Member member : members) {
            if (member.getMemberID().equals(memberId)) {
                return member;
            }
        }
        return null;
    }

    public void issueBook(Scanner input) {
        System.out.print("Enter the book ID to borrow: ");
        int bookIdToBorrow = input.nextInt();
        input.nextLine(); // Consume the newline character

        Book book = Book.findBookById(bookIdToBorrow);

        if (book != null) {
            if (!book.isBorrowed()) {
                if (book.getQuantity() > 0) {
                    book.setBorrowed(true);
                    loggedInMember.borrowBook(book);
                    book.setQuantity(book.getQuantity() - 1);
                    System.out.println("Book titled '" + book.getTitle() + "' has been issued to " + loggedInMember.getMemberDetails());
                } else {
                    System.out.println("Sorry, there are no copies left of the book titled '" + book.getTitle() + "'.");
                }
            } else {
                System.out.println("Sorry, the book is already borrowed.");
            }
        } else {
            System.out.println("Book with ID " + bookIdToBorrow + " not found.");
        }
    }

    public void returnBook(Scanner input) {
        System.out.print("Enter the book ID to return: ");
        int bookIdToReturn = input.nextInt();
        input.nextLine(); // Consume the newline character

        Book book = Book.findBookById(bookIdToReturn);

        if (book != null) {
            if (loggedInMember.getBorrowedBooks().contains(book)) {
                book.setBorrowed(false);

                // Calculate the number of days overdue
                Date currentDate = new Date();
                long overdueMillis = currentDate.getTime() - book.getDueDate().getTime();
                long overdueDays = overdueMillis / (24 * 60 * 60 * 1000);

                if (overdueDays > 0) {
                    // Calculate and apply the fine
                    double fineAmount = overdueDays * 3; // 3 rupees per day
                    loggedInMember.payFine(fineAmount);
                    System.out.println("Book titled '" + book.getTitle() + "' has been returned by " + loggedInMember.getMemberDetails());
                    System.out.println("Fine for overdue: $" + fineAmount);
                } else {
                    System.out.println("Book titled '" + book.getTitle() + "' has been returned by " + loggedInMember.getMemberDetails());
                }

                loggedInMember.returnBook(book);
                book.setQuantity(book.getQuantity() + 1);
            } else {
                System.out.println("You did not borrow the book with ID " + bookIdToReturn + ".");
            }
        } else {
            System.out.println("Book with ID " + bookIdToReturn + " not found.");
        }
    }

    public void viewMembersWithBooksAndFines() {
        System.out.println("Members with Their Borrowed Books and Fines:");
        for (Member member : members) {
            System.out.println("Member Details: " + member.getMemberDetails());

            List<Book> borrowedBooks = member.getBorrowedBooks();
            if (!borrowedBooks.isEmpty()) {
                System.out.println("Borrowed Books:");
                for (Book book : borrowedBooks) {
                    System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor());
                }
            } else {
                System.out.println("No books borrowed.");
            }

            double fines = member.getFines();
            System.out.println("Fines: $" + fines);

            System.out.println("---------------");
        }
    }

    public void payFine(double amountToPay) {
        if (amountToPay > 0) {
            loggedInMember.payFine(amountToPay);
            System.out.println("Fines paid successfully. Updated fine amount: $" + loggedInMember.getFines());
        } else {
            System.out.println("Invalid amount. Please enter a positive amount to pay fines.");
        }
    }

    public void listAllBooks() {
        System.out.println("List of All Books:");
        for (Book book : books) {
            System.out.println("Title: " + book.getTitle());
            System.out.println("Author: " + book.getAuthor());
            System.out.println("Quantity: " + book.getQuantity());
        }
    }

    public void viewBorrowedBooks(Member member) {
        List<Book> borrowedBooks = member.getBorrowedBooks();
        if (!borrowedBooks.isEmpty()) {
            System.out.println("Borrowed Books for " + member.getName() + ":");
            for (Book book : borrowedBooks) {
                System.out.println("Title: " + book.getTitle() + ", Author: " + book.getAuthor());
            }
        } else {
            System.out.println("No books borrowed by " + member.getName() + ".");
        }
    }
}

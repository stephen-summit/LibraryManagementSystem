
package com.library;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("================================");

        try (Connection ignored = DBConnection.getConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return;
        }

        while (true) {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book");
            System.out.println("4. Update Book");
            System.out.println("5. Delete Book");
            System.out.println("6. Register Member");
            System.out.println("7. View Members");
            System.out.println("8. Issue Book");
            System.out.println("9. Return Book");
            System.out.println("10. View Active Loans");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");

            int choice = readInt();

            try {
                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> viewBooks();
                    case 3 -> searchBook();
                    case 4 -> updateBook();
                    case 5 -> deleteBook();
                    case 6 -> addMember();
                    case 7 -> viewMembers();
                    case 8 -> issueBook();
                    case 9 -> returnBook();
                    case 10 -> viewActiveLoans();
                    case 0 -> {
                        System.out.println("Thank you for using the system!");
                        return;
                    }
                    default -> System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }

    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Enter a valid number: ");
            }
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // 1. Add a book
    private static void addBook() throws SQLException {
        String title = readText("Book title: ");
        String author = readText("Author: ");
        System.out.print("Number of copies: ");
        int copies = readInt();

        if (title.isBlank() || author.isBlank() || copies <= 0) {
            System.out.println("Enter valid book details and copies.");
            return;
        }

        String sql = """
                INSERT INTO books
                (title, author, total_copies, available_copies)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, copies);
            ps.setInt(4, copies);

            ps.executeUpdate();
            System.out.println("Book added successfully!");
        }
    }

    // 2. View all books
    private static void viewBooks() throws SQLException {
        String sql = "SELECT * FROM books ORDER BY book_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nID | Title | Author | Total | Available");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %d | %d%n",
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies"));
            }
        }
    }

    // 3. Search by title or author
    private static void searchBook() throws SQLException {
        String keyword = readText("Enter title or author: ");

        String sql = """
                SELECT * FROM books
                WHERE title LIKE ? OR author LIKE ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("%d | %s | %s | Available: %d%n",
                            rs.getInt("book_id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getInt("available_copies"));
                }
            }
        }
    }

    // 4. Update book details
    private static void updateBook() throws SQLException {
        System.out.print("Book ID to update: ");
        int id = readInt();
        String title = readText("New title: ");
        String author = readText("New author: ");

        if (title.isBlank() || author.isBlank()) {
            System.out.println("Title and author cannot be empty.");
            return;
        }

        String sql = """
                UPDATE books SET title = ?, author = ?
                WHERE book_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, id);

            if (ps.executeUpdate() > 0) {
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("Book ID not found.");
            }
        }
    }

    // 5. Delete a book
    private static void deleteBook() throws SQLException {
        System.out.print("Book ID to delete: ");
        int id = readInt();

        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Book ID not found.");
            }
        }
    }

    // 6. Register a member
    private static void addMember() throws SQLException {
        String name = readText("Member name: ");
        String email = readText("Member email: ");

        if (name.isBlank() || !email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            System.out.println("Enter a valid name and email.");
            return;
        }

        String sql = "INSERT INTO members (name, email) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.executeUpdate();

            System.out.println("Member registered successfully!");
        }
    }

    // 7. View members
    private static void viewMembers() throws SQLException {
        String sql = "SELECT * FROM members ORDER BY member_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                System.out.printf("%d | %s | %s%n",
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"));
            }
        }
    }

    // 8. Issue a book
    private static void issueBook() throws SQLException {
        System.out.print("Book ID: ");
        int bookId = readInt();
        System.out.print("Member ID: ");
        int memberId = readInt();

        String checkBook =
                "SELECT available_copies FROM books WHERE book_id = ?";

        String insertLoan = """
                INSERT INTO loans
                (book_id, member_id, issue_date, due_date)
                VALUES (?, ?, ?, ?)
                """;

        String updateStock = """
                UPDATE books SET available_copies = available_copies - 1
                WHERE book_id = ? AND available_copies > 0
                """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps =
                             con.prepareStatement(checkBook)) {
                    ps.setInt(1, bookId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Book ID not found.");
                        }
                        if (rs.getInt("available_copies") <= 0) {
                            throw new SQLException("No copies available.");
                        }
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(updateStock)) {
                    ps.setInt(1, bookId);
                    if (ps.executeUpdate() != 1) {
                        throw new SQLException("Could not update stock.");
                    }
                }

                LocalDate today = LocalDate.now();

                try (PreparedStatement ps =
                             con.prepareStatement(insertLoan)) {
                    ps.setInt(1, bookId);
                    ps.setInt(2, memberId);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(14)));
                    ps.executeUpdate();
                }

                con.commit();
                System.out.println("Book issued! Due date: "
                        + today.plusDays(14));

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Issue failed: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // 9. Return a book
    private static void returnBook() throws SQLException {
        System.out.print("Loan ID to return: ");
        int loanId = readInt();

        String findLoan = """
                SELECT book_id FROM loans
                WHERE loan_id = ? AND return_date IS NULL
                """;

        String closeLoan =
                "UPDATE loans SET return_date = ? WHERE loan_id = ?";

        String restoreStock = """
                UPDATE books SET available_copies = available_copies + 1
                WHERE book_id = ?
                """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int bookId;

                try (PreparedStatement ps =
                             con.prepareStatement(findLoan)) {
                    ps.setInt(1, loanId);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Active loan not found.");
                        }
                        bookId = rs.getInt("book_id");
                    }
                }

                try (PreparedStatement ps =
                             con.prepareStatement(closeLoan)) {
                    ps.setDate(1, Date.valueOf(LocalDate.now()));
                    ps.setInt(2, loanId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps =
                             con.prepareStatement(restoreStock)) {
                    ps.setInt(1, bookId);
                    ps.executeUpdate();
                }

                con.commit();
                System.out.println("Book returned successfully!");

            } catch (SQLException e) {
                con.rollback();
                System.out.println("Return failed: " + e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // 10. List books that have not yet been returned
    private static void viewActiveLoans() throws SQLException {
        String sql = """
                SELECT l.loan_id, b.title, m.name,
                       l.issue_date, l.due_date
                FROM loans l
                JOIN books b ON l.book_id = b.book_id
                JOIN members m ON l.member_id = m.member_id
                WHERE l.return_date IS NULL
                ORDER BY l.due_date
                """;

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\nLoan ID | Book | Member | Issue Date | Due Date");
            while (rs.next()) {
                System.out.printf("%d | %s | %s | %s | %s%n",
                        rs.getInt("loan_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date"));
            }
        }
    }
}

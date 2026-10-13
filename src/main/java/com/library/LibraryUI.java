package com.library;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class LibraryUI extends JFrame {

    // Balanced, accessible palette: soft blue-gray surfaces and readable dark text.
    private final Color navy = new Color(30, 48, 75);
    private final Color blue = new Color(59, 110, 165);
    private final Color background = new Color(239, 244, 249);
    private final Color cardBackground = new Color(255, 255, 255);
    private final Color textColor = new Color(42, 54, 70);
    private final Color teal = new Color(35, 135, 125);
    private final Color borderColor = new Color(215, 224, 234);

    private final DefaultTableModel bookModel = model(
            "ID", "Title", "Author", "Total Copies", "Available");
    private final DefaultTableModel memberModel = model(
            "ID", "Name", "Email");
    private final DefaultTableModel loanModel = model(
            "Loan ID", "Book", "Member", "Issue Date", "Due Date");

    private final JTable bookTable = new JTable(bookModel);
    private final JTable memberTable = new JTable(memberModel);
    private final JTable loanTable = new JTable(loanModel);

    private final JLabel booksLabel = new JLabel("0");
    private final JLabel membersLabel = new JLabel("0");
    private final JLabel loansLabel = new JLabel("0");

    public LibraryUI() {
        setTitle("Library Management System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(850, 580));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Window icon (images folder is beside pom.xml).
        ImageIcon windowIcon = loadLogo("images/library-logo.png", 32, 32);
        if (windowIcon != null) {
            setIconImage(windowIcon.getImage());
        }

        getContentPane().setBackground(background);
        setLayout(new BorderLayout());

        // Professional header: logo, title and subtitle.
        JPanel headerPanel = new JPanel(new BorderLayout(16, 0));
        headerPanel.setBackground(navy);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel logoLabel = new JLabel();
        ImageIcon headerLogo = loadLogo("images/library-logo.png", 54, 54);
        if (headerLogo == null) {
            headerLogo = loadLogo("images/book-logo.png", 54, 54);
        }
        if (headerLogo != null) {
            logoLabel.setIcon(headerLogo);
        } else {
            logoLabel.setText("LIB");
            logoLabel.setForeground(Color.WHITE);
            logoLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        }

        JLabel titleLabel = new JLabel("LIBRARY MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 23));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Manage books, members and lending in one place");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(211, 224, 240));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(logoLabel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabs.setBackground(background);
        tabs.setForeground(textColor);
        tabs.addTab("Dashboard", createDashboard());
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Issue / Return", createLoansPanel());
        add(tabs, BorderLayout.CENTER);

        // Footer: project name, technology stack and developer credit.
        JPanel footer = new JPanel(new BorderLayout(12, 4));
        footer.setBackground(new Color(222, 231, 241));
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, borderColor),
                BorderFactory.createEmptyBorder(11, 20, 11, 20)));

        JLabel footerLeft = new JLabel("Library Management System  |  Java Swing + JDBC + MySQL");
        footerLeft.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footerLeft.setForeground(textColor);

        JLabel footerRight = new JLabel("Developed by Sumit Kumar Ram");
        footerRight.setFont(new Font("SansSerif", Font.BOLD, 12));
        footerRight.setForeground(navy);

        footer.add(footerLeft, BorderLayout.WEST);
        footer.add(footerRight, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        refreshAll();
    }

    // Loads and scales a logo from the project's images folder.
    private ImageIcon loadLogo(String path, int width, int height) {
        java.io.File file = new java.io.File(path);
        if (!file.isFile()) {
            return null;
        }
        Image image = new ImageIcon(path).getImage()
                .getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private JPanel createDashboard() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 25, 40, 25));
        panel.setBackground(background);

        panel.add(statCard("Total Book Titles", booksLabel));
        panel.add(statCard("Registered Members", membersLabel));
        panel.add(statCard("Active Loans", loansLabel));

        return panel;
    }

    private JPanel statCard(String title, JLabel value) {
        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));
        card.setBackground(cardBackground);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 16));
        heading.setForeground(textColor);

        value.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(new Font("SansSerif", Font.BOLD, 34));
        value.setForeground(blue);

        card.add(heading);
        card.add(value);
        return card;
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        styleTable(bookTable);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttons.setBackground(background);
        buttons.add(button("Add Book", this::addBook));
        buttons.add(button("Update Selected", this::updateBook));
        buttons.add(button("Delete Selected", this::deleteBook));
        buttons.add(button("Search", this::searchBook));
        buttons.add(button("Refresh", this::loadBooks));

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        styleTable(memberTable);
        panel.add(new JScrollPane(memberTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttons.setBackground(background);
        buttons.add(button("Register Member", this::addMember));
        buttons.add(button("Refresh", this::loadMembers));

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        styleTable(loanTable);
        panel.add(new JScrollPane(loanTable), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttons.setBackground(background);
        buttons.add(button("Issue Book", this::issueBook));
        buttons.add(button("Return Selected Book", this::returnBook));
        buttons.add(button("Refresh", this::loadLoans));

        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JButton button(String text, Runnable action) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(Color.WHITE);
        b.setBackground(text.startsWith("Issue") || text.startsWith("Register")
                ? teal : blue);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMargin(new Insets(9, 14, 9, 14));
        b.addActionListener(e -> action.run());
        return b;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setForeground(textColor);
        table.setBackground(cardBackground);
        table.setGridColor(borderColor);
        table.setSelectionBackground(new Color(205, 225, 247));
        table.setSelectionForeground(navy);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBackground(navy);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBackground(navy);
        renderer.setForeground(Color.WHITE);
        renderer.setFont(new Font("SansSerif", Font.BOLD, 14));
        renderer.setOpaque(true);
        header.setDefaultRenderer(renderer);
    }

    private void refreshAll() {
        loadBooks();
        loadMembers();
        loadLoans();
        loadStats();
    }

    private void loadBooks() {
        bookModel.setRowCount(0);
        String sql = "SELECT book_id, title, author, total_copies, " +
                "available_copies FROM books ORDER BY book_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                bookModel.addRow(new Object[]{
                        rs.getInt("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("total_copies"),
                        rs.getInt("available_copies")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
        loadStats();
    }

    private void addBook() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField copies = new JTextField("1");

        if (!form("Add Book",
                new String[]{"Title", "Author", "Total Copies"},
                new JComponent[]{title, author, copies})) {
            return;
        }

        String t = title.getText().trim();
        String a = author.getText().trim();
        int count;

        try {
            count = Integer.parseInt(copies.getText().trim());
            if (t.isEmpty() || a.isEmpty() || count < 1) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            message("Enter a title, author, and a positive number of copies.");
            return;
        }

        String sql = "INSERT INTO books " +
                "(title, author, total_copies, available_copies) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t);
            ps.setString(2, a);
            ps.setInt(3, count);
            ps.setInt(4, count);
            ps.executeUpdate();

            message("Book added successfully.");
            refreshAll();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void updateBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            message("Select a book to update.");
            return;
        }

        int id = (int) bookModel.getValueAt(row, 0);
        String oldTitle = bookModel.getValueAt(row, 1).toString();
        String oldAuthor = bookModel.getValueAt(row, 2).toString();
        int oldTotal = (int) bookModel.getValueAt(row, 3);
        int oldAvailable = (int) bookModel.getValueAt(row, 4);

        JTextField title = new JTextField(oldTitle);
        JTextField author = new JTextField(oldAuthor);
        JTextField total = new JTextField(String.valueOf(oldTotal));

        if (!form("Update Book",
                new String[]{"Title", "Author", "Total Copies"},
                new JComponent[]{title, author, total})) {
            return;
        }

        String t = title.getText().trim();
        String a = author.getText().trim();
        int newTotal;

        try {
            newTotal = Integer.parseInt(total.getText().trim());
            int issued = oldTotal - oldAvailable;

            if (t.isEmpty() || a.isEmpty() ||
                    newTotal < 1 || newTotal < issued) {
                message("Total copies cannot be less than copies already issued.");
                return;
            }
        } catch (NumberFormatException e) {
            message("Enter a valid total-copy number.");
            return;
        }

        String sql = "UPDATE books SET title=?, author=?, total_copies=?, " +
                "available_copies=? WHERE book_id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, t);
            ps.setString(2, a);
            ps.setInt(3, newTotal);
            ps.setInt(4, newTotal - (oldTotal - oldAvailable));
            ps.setInt(5, id);
            ps.executeUpdate();

            message("Book updated successfully.");
            refreshAll();
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void deleteBook() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            message("Select a book to delete.");
            return;
        }

        int id = (int) bookModel.getValueAt(row, 0);
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete this book? Books with loan history cannot be deleted.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps =
                     con.prepareStatement("DELETE FROM books WHERE book_id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            message("Book deleted successfully.");
            refreshAll();
        } catch (SQLException e) {
            message("Could not delete this book. It may have loan records.");
        }
    }

    private void searchBook() {
        String keyword = JOptionPane.showInputDialog(
                this, "Enter title or author to search:");
        if (keyword == null || keyword.trim().isEmpty()) return;

        bookModel.setRowCount(0);
        String sql = "SELECT book_id, title, author, total_copies, " +
                "available_copies FROM books " +
                "WHERE title LIKE ? OR author LIKE ? ORDER BY book_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String pattern = "%" + keyword.trim() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookModel.addRow(new Object[]{
                            rs.getInt("book_id"), rs.getString("title"),
                            rs.getString("author"), rs.getInt("total_copies"),
                            rs.getInt("available_copies")
                    });
                }
            }
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void loadMembers() {
        memberModel.setRowCount(0);
        String sql = "SELECT member_id, name, email FROM members ORDER BY member_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                memberModel.addRow(new Object[]{
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
        loadStats();
    }

    private void addMember() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();

        if (!form("Register Member",
                new String[]{"Full Name", "Email"},
                new JComponent[]{name, email})) {
            return;
        }

        String n = name.getText().trim();
        String em = email.getText().trim();

        if (n.isEmpty() || !em.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            message("Enter a name and a valid email address.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO members (name, email) VALUES (?, ?)")) {
            ps.setString(1, n);
            ps.setString(2, em);
            ps.executeUpdate();

            message("Member registered successfully.");
            refreshAll();
        } catch (SQLIntegrityConstraintViolationException e) {
            message("This email is already registered.");
        } catch (SQLException e) {
            showError(e);
        }
    }

    private void issueBook() {
        JTextField bookId = new JTextField();
        JTextField memberId = new JTextField();

        if (!form("Issue Book",
                new String[]{"Book ID", "Member ID"},
                new JComponent[]{bookId, memberId})) {
            return;
        }

        int b, m;
        try {
            b = Integer.parseInt(bookId.getText().trim());
            m = Integer.parseInt(memberId.getText().trim());
            if (b <= 0 || m <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            message("Book ID and Member ID must be positive numbers.");
            return;
        }

        String checkBook = "SELECT available_copies FROM books " +
                "WHERE book_id=? FOR UPDATE";

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int available;
                try (PreparedStatement ps = con.prepareStatement(checkBook)) {
                    ps.setInt(1, b);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new Exception("Book ID not found.");
                        available = rs.getInt(1);
                    }
                }

                if (available <= 0) throw new Exception("No copies are available.");

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT member_id FROM members WHERE member_id=?")) {
                    ps.setInt(1, m);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new Exception("Member ID not found.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies=available_copies-1 " +
                                "WHERE book_id=? AND available_copies>0")) {
                    ps.setInt(1, b);
                    if (ps.executeUpdate() != 1) {
                        throw new Exception("No copies are available.");
                    }
                }

                LocalDate today = LocalDate.now();
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO loans " +
                                "(book_id, member_id, issue_date, due_date) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, b);
                    ps.setInt(2, m);
                    ps.setDate(3, Date.valueOf(today));
                    ps.setDate(4, Date.valueOf(today.plusDays(14)));
                    ps.executeUpdate();
                }

                con.commit();
                message("Book issued successfully. Due date: " +
                        today.plusDays(14));
            } catch (Exception e) {
                con.rollback();
                message(e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError(e);
        }

        refreshAll();
    }

    private void returnBook() {
        int row = loanTable.getSelectedRow();
        if (row < 0) {
            message("Select an active loan to return.");
            return;
        }

        int loanId = (int) loanModel.getValueAt(row, 0);
        int answer = JOptionPane.showConfirmDialog(this,
                "Confirm return of loan ID " + loanId + "?",
                "Return Book", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) return;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                int bookId;

                try (PreparedStatement ps = con.prepareStatement(
                        "SELECT book_id FROM loans " +
                                "WHERE loan_id=? AND return_date IS NULL FOR UPDATE")) {
                    ps.setInt(1, loanId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) throw new Exception("This loan is no longer active.");
                        bookId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE loans SET return_date=? WHERE loan_id=? " +
                                "AND return_date IS NULL")) {
                    ps.setDate(1, Date.valueOf(LocalDate.now()));
                    ps.setInt(2, loanId);
                    if (ps.executeUpdate() != 1) {
                        throw new Exception("Could not complete the return.");
                    }
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "UPDATE books SET available_copies=available_copies+1 " +
                                "WHERE book_id=? AND available_copies<total_copies")) {
                    ps.setInt(1, bookId);
                    if (ps.executeUpdate() != 1) {
                        throw new Exception("Could not update book availability.");
                    }
                }

                con.commit();
                message("Book returned successfully.");
            } catch (Exception e) {
                con.rollback();
                message(e.getMessage());
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            showError(e);
        }

        refreshAll();
    }

    private void loadLoans() {
        loanModel.setRowCount(0);

        String sql = "SELECT l.loan_id, b.title, m.name, " +
                "l.issue_date, l.due_date FROM loans l " +
                "JOIN books b ON l.book_id=b.book_id " +
                "JOIN members m ON l.member_id=m.member_id " +
                "WHERE l.return_date IS NULL ORDER BY l.loan_id";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                loanModel.addRow(new Object[]{
                        rs.getInt("loan_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getDate("issue_date"),
                        rs.getDate("due_date")
                });
            }
        } catch (SQLException e) {
            showError(e);
        }
        loadStats();
    }

    private void loadStats() {
        try (Connection con = DBConnection.getConnection()) {
            booksLabel.setText(String.valueOf(
                    scalar(con, "SELECT COUNT(*) FROM books")));
            membersLabel.setText(String.valueOf(
                    scalar(con, "SELECT COUNT(*) FROM members")));
            loansLabel.setText(String.valueOf(
                    scalar(con, "SELECT COUNT(*) FROM loans WHERE return_date IS NULL")));
        } catch (SQLException e) {
            showError(e);
        }
    }

    private int scalar(Connection con, String sql) throws SQLException {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private boolean form(String title, String[] labels, JComponent[] fields) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 10, 10));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setForeground(textColor);
            panel.add(label);
            fields[i].setFont(new Font("SansSerif", Font.PLAIN, 14));
            panel.add(fields[i]);
        }

        return JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private void message(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    private void showError(SQLException e) {
        e.printStackTrace();
        message("Database error: " + e.getMessage());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Use the default Swing appearance if unavailable.
            }

            new LibraryUI().setVisible(true);
        });
    }
}

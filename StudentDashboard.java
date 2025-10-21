import java.awt.*;
import javax.swing.*;
//import java.awt.event.*;
import java.sql.*;
//import java.net.URL;
//import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

public class StudentDashboard {
    // Removed hardcoded loggedInUSN

    StudentDashboard(String loggedInUSN) {
        JFrame jfrm = new JFrame("Student Dashboard");
        jfrm.setSize(700, 600);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);

        // Main tabbed pane
        JTabbedPane jtp = new JTabbedPane();

        // Add tabs
        jtp.addTab("Search Books", new SearchBooks());
        jtp.addTab("Profile", new Profile(loggedInUSN));

        jfrm.add(jtp);
        jfrm.setVisible(true);
    }

    public static void main(String args[]) {
        // For testing, you can pass any USN dynamically
        SwingUtilities.invokeLater(() -> new StudentDashboard("NNM24IS188"));
    }
}

// --------------------- SEARCH BOOKS PANEL ------------------------
class SearchBooks extends JPanel {
    SearchBooks() {
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel jb1 = new JLabel("Book ID:");
        JTextField jt1 = new JTextField();
        JLabel jb2 = new JLabel("Book Author:");
        JTextField jt2 = new JTextField();
        JLabel jb3 = new JLabel("");
        formPanel.add(jb1); formPanel.add(jt1);
        formPanel.add(jb2); formPanel.add(jt2);
        formPanel.add(jb3);
        add(formPanel, BorderLayout.NORTH);

        JPanel btnpanel = new JPanel();
        JButton searchbtn = new JButton("Search");
        btnpanel.add(searchbtn);
        add(btnpanel, BorderLayout.CENTER);

        searchbtn.addActionListener(ae -> {
            try {
                int id = Integer.parseInt(jt1.getText());
                String author = jt2.getText();
                boolean success = showbooks(id, author);
                jb3.setText(success ? "Fetched successfully" : "No data found");
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Enter a valid numeric Book ID");
            }
        });
    }

    boolean showbooks(int id, String author) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String checkqty = "SELECT * FROM booksnew WHERE bookid = ?";
            PreparedStatement psmt1 = conn.prepareStatement(checkqty);
            psmt1.setInt(1, id);
            ResultSet rs = psmt1.executeQuery();

            if (rs.next()) {
                String title = rs.getString("bookname");
                int qty = rs.getInt("bookquantity");

                JOptionPane.showMessageDialog(
                        this,
                        "Book Found:\n\nID: " + id +
                                "\nAuthor: " + author +
                                "\nTitle: " + title +
                                "\nQuantity: " + qty,
                        "Book Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
                conn.close();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "No book found with that ID.", "Not Found", JOptionPane.WARNING_MESSAGE);
                conn.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            return false;
        }
    }
}

// --------------------- PROFILE PANEL ------------------------
class Profile extends JPanel {
    JLabel nameLabel, usnLabel, branchLabel, secLabel, yearLabel, emailLabel, contactLabel, imageLabel;
    JTable borrowedTable;
    DefaultTableModel model;

    public Profile(String loggedInUSN) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204));
        titlePanel.setPreferredSize(new Dimension(700, 60));
        JLabel title = new JLabel("Student Profile", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // Top panel: Image + Info
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(160, 160));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        imagePanel.add(imageLabel);

        JPanel infoPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameLabel = new JLabel(); usnLabel = new JLabel(); branchLabel = new JLabel();
        secLabel = new JLabel(); yearLabel = new JLabel(); emailLabel = new JLabel(); contactLabel = new JLabel();

        JLabel[] labels = {nameLabel, usnLabel, branchLabel, secLabel, yearLabel, emailLabel, contactLabel};
        for (JLabel label : labels) { label.setFont(infoFont); infoPanel.add(label); }

        topPanel.add(imagePanel, BorderLayout.WEST);
        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Borrowed books table
        String[] columns = {"Book ID", "Book Name", "Quantity", "Issued Date", "Return Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        borrowedTable = new JTable(model);
        borrowedTable.setRowHeight(25);
        borrowedTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        borrowedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        borrowedTable.setFillsViewportHeight(true);

        JScrollPane tableScroll = new JScrollPane(borrowedTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Borrowed Books"));
        add(tableScroll, BorderLayout.CENTER);

        // Load info
        loadStudentDetails(loggedInUSN);
        loadBorrowedBooks(loggedInUSN);
    }

    void loadStudentDetails(String usn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String query = "SELECT * FROM students WHERE studentusn = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("studentname"));
                usnLabel.setText("USN: " + rs.getString("studentusn"));
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    void loadBorrowedBooks(String usn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String query = "SELECT ib.bookid, b.bookname, ib.bookquantity, ib.issuedate, ib.returndate, " +
                    "CASE WHEN ib.returndate IS NULL THEN 'Issued' ELSE 'Returned' END AS status " +
                    "FROM issuedbooks ib JOIN books b ON ib.bookid=b.bookid WHERE ib.studentusn=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usn);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bookid"),
                        rs.getString("bookname"),
                        rs.getInt("bookquantity"),
                        rs.getDate("issuedate"),
                        rs.getDate("returndate"),
                        rs.getString("status")
                };
                model.addRow(row);
            }
            conn.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}

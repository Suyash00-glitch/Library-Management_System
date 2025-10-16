import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class StudentDashboard {
    // Static variable to store logged-in USN (temporary hardcoded for demo)
    public static String loggedInUSN = "NNM24IS188";

    StudentDashboard() {
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
        SwingUtilities.invokeLater(() -> new StudentDashboard());
    }
}

// --------------------- SEARCH BOOKS PANEL ------------------------
class SearchBooks extends JPanel {
    SearchBooks() {
        setLayout(new BorderLayout(10, 10));

        // Top form
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JLabel jb1 = new JLabel("Book ID:");
        JTextField jt1 = new JTextField();
        JLabel jb2 = new JLabel("Book Author:");
        JTextField jt2 = new JTextField();
        JLabel jb3 = new JLabel("");
        formPanel.add(jb1);
        formPanel.add(jt1);
        formPanel.add(jb2);
        formPanel.add(jt2);
        formPanel.add(jb3);
        add(formPanel, BorderLayout.NORTH);

        // Search button panel
        JPanel btnpanel = new JPanel();
        JButton searchbtn = new JButton("Search");
        btnpanel.add(searchbtn);
        add(btnpanel, BorderLayout.CENTER);

        // Search action
        searchbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    int id = Integer.parseInt(jt1.getText());
                    String author = jt2.getText();
                    boolean success = showbooks(id, author);
                    jb3.setText(success ? "Fetched successfully" : "No data found");
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Enter a valid numeric Book ID");
                }
            }
        });
    }

    // Fetch book details
    boolean showbooks(int id, String author) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

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

    public Profile(String loggedInUSN) {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);

        // ---------- BLUE TITLE BAR ----------
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0, 102, 204)); // Deep blue
        titlePanel.setPreferredSize(new Dimension(700, 60));
        JLabel title = new JLabel("Student Profile", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titlePanel.add(title);
        add(titlePanel, BorderLayout.NORTH);

        // ---------- Image Panel ----------
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imageLabel = new JLabel("Loading...", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(160, 160));
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2, true));
        imagePanel.add(imageLabel);
        add(imagePanel, BorderLayout.WEST);

        // ---------- Info Panel ----------
        JPanel infoPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        Font infoFont = new Font("Segoe UI", Font.PLAIN, 16);
        nameLabel = new JLabel();
        usnLabel = new JLabel();
        branchLabel = new JLabel();
        secLabel = new JLabel();
        yearLabel = new JLabel();
        emailLabel = new JLabel();
        contactLabel = new JLabel();

        JLabel[] labels = {nameLabel, usnLabel, branchLabel, secLabel, yearLabel, emailLabel, contactLabel};
        for (JLabel label : labels) {
            label.setFont(infoFont);
            infoPanel.add(label);
        }

        add(infoPanel, BorderLayout.CENTER);

        // Load profile info from database
        loadStudentDetails(loggedInUSN);
    }

    // ---------- Loads all student details and image ----------
    void loadStudentDetails(String usn) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String query = "SELECT * FROM studentinfo WHERE usn = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, usn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameLabel.setText("Name: " + rs.getString("name"));
                usnLabel.setText("USN: " + rs.getString("usn"));
                branchLabel.setText("Branch: " + rs.getString("branch"));
                secLabel.setText("Section: " + rs.getString("sec"));
                yearLabel.setText("Year: " + rs.getString("year"));
                emailLabel.setText("Email: " + rs.getString("email"));
                contactLabel.setText("Contact: " + rs.getString("contact"));

                // ✅ Load image from S3 URL (your URL works)
                String imageUrl = rs.getString("imageurl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    try {
                        URL url = new URL(imageUrl);
                        Image img = ImageIO.read(url);
                        if (img != null) {
                            Image scaled = img.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                            imageLabel.setIcon(new ImageIcon(scaled));
                            imageLabel.setText(null);
                        } else {
                            imageLabel.setText("Invalid Image");
                        }
                    } catch (Exception e) {
                        imageLabel.setText("Error loading image");
                        e.printStackTrace();
                    }
                } else {
                    imageLabel.setText("No Image");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

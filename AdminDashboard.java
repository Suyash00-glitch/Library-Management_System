import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
//import java.awt.event.*;

public class AdminDashboard {
    AdminDashboard() {
        JFrame jfrm = new JFrame("Admin Dashboard");
        jfrm.setSize(700, 600);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);

        // Set a modern look and feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        JTabbedPane jtp = new JTabbedPane();
        jtp.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jtp.setBackground(new Color(245, 245, 255));
        jtp.setForeground(new Color(40, 40, 80));

        jtp.addTab("Manage Books", new Managebooks());
        jtp.addTab("Manage Students", new ManageStudents());
        jfrm.add(jtp);

        jfrm.getContentPane().setBackground(new Color(230, 240, 255));
        jfrm.setVisible(true);
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new AdminDashboard());
    }
}


class Managebooks extends JPanel {
    Managebooks() {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 240, 255));
        JTabbedPane bookopr = new JTabbedPane();
        bookopr.setFont(new Font("Segoe UI", Font.BOLD, 15));
        bookopr.setBackground(new Color(245, 245, 255));
        bookopr.setForeground(new Color(40, 40, 80));
        bookopr.addTab("Add Books", new AddBook());
        bookopr.addTab("Delete Books", new DeleteBook());
        bookopr.addTab("View Books", new ViewBook());
        bookopr.addTab("Issue Book", new IssueBook());
        bookopr.addTab("Return Book", new ReturnBook());
        add(bookopr, BorderLayout.CENTER);
    }
}

class AddBook extends JPanel {
    AddBook() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 250, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
            "Add New Book",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lb1 = new JLabel("Book Name:");
        lb1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField name = new JTextField(15);
        name.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        name.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lb2 = new JLabel("Book Number:");
        lb2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField number = new JTextField(15);
        number.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        number.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lb3 = new JLabel("Book Quantity:");
        lb3.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField quantity = new JTextField(15);
        quantity.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        quantity.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lb4 = new JLabel("");
        lb4.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lb4.setForeground(new Color(0, 120, 0));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lb1, gbc);
        gbc.gridx = 1;
        formPanel.add(name, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lb2, gbc);
        gbc.gridx = 1;
        formPanel.add(number, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(lb3, gbc);
        gbc.gridx = 1;
        formPanel.add(quantity, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(lb4, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnpanel.setBackground(new Color(245, 250, 255));
        JButton addbtn = new JButton("Add Book");
        addbtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        addbtn.setBackground(new Color(100, 150, 255));
        addbtn.setForeground(Color.WHITE);
        addbtn.setFocusPainted(false);
        addbtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnpanel.add(addbtn);
        add(btnpanel, BorderLayout.SOUTH);

        addbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String bname = name.getText().trim();
                String idStr = number.getText().trim();
                String qtyStr = quantity.getText().trim();
                if (bname.isEmpty() || idStr.isEmpty() || qtyStr.isEmpty()) {
                    lb4.setText("Please fill all fields.");
                    lb4.setForeground(Color.RED);
                    return;
                }
                try {
                    int id = Integer.parseInt(idStr);
                    int qty = Integer.parseInt(qtyStr);
                    boolean success = savetodb(bname, id, qty);
                    if (success) {
                        lb4.setText("Book added successfully!");
                        lb4.setForeground(new Color(0, 120, 0));
                        name.setText(""); number.setText(""); quantity.setText("");
                    } else {
                        lb4.setText("Failed to add book.");
                        lb4.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lb4.setText("Book Number and Quantity must be numbers.");
                    lb4.setForeground(Color.RED);
                }
            }
        });
    }

    boolean savetodb(String name, int id, int qty) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query = "INSERT INTO books(bookname,bookid,bookquantity) VALUES (?,?,?)";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, name);
            psmt.setInt(2, id);
            psmt.setInt(3, qty);
            int rows = psmt.executeUpdate();
            conn.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
class DeleteBook extends JPanel {
    DeleteBook() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 250, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 120, 120), 2, true),
            "Delete Book",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(160, 40, 40)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lb2 = new JLabel("Book Number:");
        lb2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField number = new JTextField(15);
        number.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        number.setBorder(BorderFactory.createLineBorder(new Color(255, 180, 180), 1));

        JLabel lb3 = new JLabel("");
        lb3.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lb3.setForeground(new Color(160, 40, 40));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lb2, gbc);
        gbc.gridx = 1;
        formPanel.add(number, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        formPanel.add(lb3, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(245, 250, 255));
        JButton deletebtn = new JButton("Delete Book");
        deletebtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        deletebtn.setBackground(new Color(255, 120, 120));
        deletebtn.setForeground(Color.WHITE);
        deletebtn.setFocusPainted(false);
        deletebtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnPanel.add(deletebtn);
        add(btnPanel, BorderLayout.SOUTH);

        deletebtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String idStr = number.getText().trim();
                if (idStr.isEmpty()) {
                    lb3.setText("Please enter Book Number.");
                    lb3.setForeground(Color.RED);
                    return;
                }
                try {
                    int id = Integer.parseInt(idStr);
                    boolean success = deletefromdb(id);
                    if (success) {
                        lb3.setText("Book deleted successfully!");
                        lb3.setForeground(new Color(0, 120, 0));
                        number.setText("");
                    } else {
                        lb3.setText("Book not found or could not be deleted.");
                        lb3.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lb3.setText("Book Number must be a number.");
                    lb3.setForeground(Color.RED);
                }
            }
        });
    }

    boolean deletefromdb(int id) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query = "DELETE FROM books WHERE bookid = ?";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setInt(1, id);
            int rows = psmt.executeUpdate();
            conn.close();
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

class ViewBook extends JPanel {
    DefaultTableModel model;
    JScrollPane scrollpane;

    ViewBook() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 250, 255));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(245, 250, 255));
        JButton jb = new JButton("Refresh");
        jb.setFont(new Font("Segoe UI", Font.BOLD, 15));
        jb.setBackground(new Color(100, 150, 255));
        jb.setForeground(Color.WHITE);
        jb.setFocusPainted(false);
        jb.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnPanel.add(jb);
        add(btnPanel, BorderLayout.SOUTH);

        String[] columnNames = {"Book Name", "Book ID", "Book Quantity"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setGridColor(new Color(200, 220, 255));
        table.setShowGrid(true);

        scrollpane = new JScrollPane(table);
        scrollpane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
            "Books List",
            0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));
        add(scrollpane, BorderLayout.CENTER);

        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                loadbooks();
            }
        });

        // Load books initially
        loadbooks();
    }

    void loadbooks() {
        try {
            model.setRowCount(0);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query = "SELECT * FROM books";
            PreparedStatement psmt = conn.prepareStatement(query);
            ResultSet rs = psmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("bookname");
                int id = rs.getInt("bookid");
                int qty = rs.getInt("bookquantity");
                model.addRow(new Object[]{name, id, qty});
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
    
class IssueBook extends JPanel {

    DefaultTableModel model;

    IssueBook() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 250, 255));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(235, 245, 255));
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
                "Issue Book",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lbUsn = new JLabel("Student USN:");
        lbUsn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField tfUsn = new JTextField(15);
        tfUsn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfUsn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lbBookId = new JLabel("Book ID:");
        lbBookId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField tfBookId = new JTextField(15);
        tfBookId.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfBookId.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lbMsg = new JLabel("");
        lbMsg.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbMsg.setForeground(new Color(0, 120, 0));

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(lbUsn, gbc);
        gbc.gridx = 1;
        formPanel.add(tfUsn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(lbBookId, gbc);
        gbc.gridx = 1;
        formPanel.add(tfBookId, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(lbMsg, gbc);

        add(formPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(new Color(235, 245, 255));
        JButton issueBtn = new JButton("Issue Book");
        issueBtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        issueBtn.setBackground(new Color(100, 150, 255));
        issueBtn.setForeground(Color.WHITE);
        issueBtn.setFocusPainted(false);
        issueBtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnPanel.add(issueBtn);
        add(btnPanel, BorderLayout.SOUTH);

        String[] columns = {"ID", "Student USN", "Book ID", "Issue Date", "Return Date", "Status"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.setRowHeight(28);
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setGridColor(new Color(200, 220, 255));
        table.setShowGrid(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
                "Issued Books",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));
        add(scrollPane, BorderLayout.CENTER);

        loadIssuedBooks();

        issueBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String usn = tfUsn.getText().trim();
                String bookIdStr = tfBookId.getText().trim();
                if (usn.isEmpty() || bookIdStr.isEmpty()) {
                    lbMsg.setText("Please fill all fields.");
                    lbMsg.setForeground(Color.RED);
                    return;
                }
                try {
                    int bookId = Integer.parseInt(bookIdStr);
                    boolean success = issuebookdb(usn, bookId);
                    if (success) {
                        lbMsg.setText("Book issued successfully!");
                        lbMsg.setForeground(new Color(0, 120, 0));
                        tfUsn.setText(""); tfBookId.setText("");
                        loadIssuedBooks();
                    } else {
                        lbMsg.setText("Book not issued.");
                        lbMsg.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lbMsg.setText("Book ID must be a number.");
                    lbMsg.setForeground(Color.RED);
                }
            }
        });
    }

   boolean issuebookdb(String usn, int bookId) {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

        // 1. Check book availability
        String checkBookQty = "SELECT bookquantity FROM books WHERE bookid=?";
        PreparedStatement ps1 = conn.prepareStatement(checkBookQty);
        ps1.setInt(1, bookId);
        ResultSet rsBook = ps1.executeQuery();
        if (!rsBook.next()) {
            JOptionPane.showMessageDialog(this, "Book ID not found!");
            conn.close();
            return false;
        }
        int availableQty = rsBook.getInt("bookquantity");
        if (availableQty <= 0) {
            JOptionPane.showMessageDialog(this, "Cannot issue. Book is out of stock!");
            conn.close();
            return false;
        }

        // 2. Check total books already issued to this student
        String checkStudentTotal = "SELECT IFNULL(SUM(bookquantity),0) AS totalBooks FROM issuedbooks WHERE studentusn=? AND returndate IS NULL";
        PreparedStatement ps2 = conn.prepareStatement(checkStudentTotal);
        ps2.setString(1, usn);
        ResultSet rsTotal = ps2.executeQuery();
        int totalBooks = 0;
        if (rsTotal.next()) {
            totalBooks = rsTotal.getInt("totalBooks");
        }
        if (totalBooks + 1 > 3) {  // +1 because we are issuing 1 book
            JOptionPane.showMessageDialog(this, "Cannot issue. Student already has " + totalBooks + " books issued. Max allowed is 3.");
            conn.close();
            return false;
        }

        // 3. Issue book
        String issueBook = "INSERT INTO issuedbooks (studentusn, bookid, bookquantity, issuedate) VALUES (?, ?, 1, CURDATE())";
        PreparedStatement ps3 = conn.prepareStatement(issueBook);
        ps3.setString(1, usn);
        ps3.setInt(2, bookId);
        int rows = ps3.executeUpdate();

        // 4. Update book quantity
        String updateQty = "UPDATE books SET bookquantity = bookquantity - 1 WHERE bookid=?";
        PreparedStatement ps4 = conn.prepareStatement(updateQty);
        ps4.setInt(1, bookId);
        ps4.executeUpdate();

        conn.close();
        return rows > 0;

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        return false;
    }
}


    void loadIssuedBooks() {
        model.setRowCount(0);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String query = "SELECT id, studentusn, bookid, issuedate, returndate, " +
                    "CASE WHEN returndate IS NULL THEN 'Issued' ELSE 'Returned' END AS status " +
                    "FROM issuedbooks";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String usn = rs.getString("studentusn");
                int bookId = rs.getInt("bookid");
                Date issueDate = rs.getDate("issuedate");
                Date returnDate = rs.getDate("returndate");
                String status = rs.getString("status");
                model.addRow(new Object[]{id, usn, bookId, issueDate, returnDate, status});
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ReturnBook extends JPanel {
    ReturnBook() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 250, 255));

        JPanel formpanel = new JPanel(new GridBagLayout());
        formpanel.setBackground(new Color(235, 245, 255));
        formpanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 150, 255), 2, true),
                "Return Book",
                0, 0, new Font("Segoe UI", Font.BOLD, 16), new Color(40, 80, 160)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lb1 = new JLabel("Student USN:");
        lb1.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField tfusn = new JTextField(15);
        tfusn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfusn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lb2 = new JLabel("Book ID:");
        lb2.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JTextField tfbid = new JTextField(15);
        tfbid.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tfbid.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 255), 1));

        JLabel lb4 = new JLabel("");
        lb4.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0; gbc.gridy = 0;
        formpanel.add(lb1, gbc);
        gbc.gridx = 1;
        formpanel.add(tfusn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formpanel.add(lb2, gbc);
        gbc.gridx = 1;
        formpanel.add(tfbid, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formpanel.add(lb4, gbc);

        add(formpanel, BorderLayout.NORTH);

        JPanel btnpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnpanel.setBackground(new Color(235, 245, 255));
        JButton returnbtn = new JButton("Return Book");
        returnbtn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        returnbtn.setBackground(new Color(100, 150, 255));
        returnbtn.setForeground(Color.WHITE);
        returnbtn.setFocusPainted(false);
        returnbtn.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        btnpanel.add(returnbtn);
        add(btnpanel, BorderLayout.SOUTH);

        returnbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String usn = tfusn.getText().trim();
                String bookIdStr = tfbid.getText().trim();
                if (usn.isEmpty() || bookIdStr.isEmpty()) {
                    lb4.setText("Please fill all fields.");
                    lb4.setForeground(Color.RED);
                    return;
                }
                try {
                    int bookId = Integer.parseInt(bookIdStr);
                    boolean success = updatedb(usn, bookId);
                    if (success) {
                        lb4.setText("Book returned successfully!");
                        lb4.setForeground(new Color(0, 120, 0));
                        tfusn.setText(""); tfbid.setText("");
                    } else {
                        lb4.setText("Book not returned. Please check details.");
                        lb4.setForeground(Color.RED);
                    }
                } catch (NumberFormatException ex) {
                    lb4.setText("Book ID must be a number.");
                    lb4.setForeground(Color.RED);
                }
            }
        });
    }

    boolean updatedb(String usn, int bookid) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query = "DELETE FROM issuedbooks WHERE studentusn=? AND bookid=?";
            PreparedStatement ps1 = conn.prepareStatement(query);
            ps1.setString(1, usn);
            ps1.setInt(2, bookid);
            int rs1 = ps1.executeUpdate();

            String updateBookQty = "UPDATE books SET bookquantity = bookquantity + 1 WHERE bookid = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateBookQty);
            ps2.setInt(1, bookid);
            int rs2 = ps2.executeUpdate();
            
            conn.close();
            return (rs1 > 0 && rs2 > 0);

        } catch (Exception e) {
            e.printStackTrace(); 
            return false;
        }
    }
}


class ManageStudents extends JPanel{
    DefaultTableModel model;
    ManageStudents(){

        setLayout(new BorderLayout(10, 10));
        JPanel formpanel=new JPanel( new GridLayout(3,2,5,5));
        formpanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel jb1=new JLabel("Select Year");
        formpanel.add(jb1);
        JComboBox<String> jbc1=new JComboBox<>();
        jbc1.addItem("1st");
        jbc1.addItem("2nd");
        jbc1.addItem("3rd");
        jbc1.addItem("4th");
        formpanel.add(jbc1);
        
        JLabel jb2=new JLabel("Select Branch");
        formpanel.add(jb2);
        JComboBox<String> jbc2=new JComboBox<>();
        jbc2.addItem("ISE");
        jbc2.addItem("CSE");
        jbc2.addItem("ECE");
        jbc2.addItem("CIVIL");
        formpanel.add(jbc2);
        
        
        JLabel jb3=new JLabel("Select Section");
        formpanel.add(jb3);
        JComboBox<String> jbc3=new JComboBox<>();
        jbc3.addItem("A");
        jbc3.addItem("B");
        jbc3.addItem("C");
        jbc3.addItem("D");
        formpanel.add(jbc3);


        add(formpanel,BorderLayout.NORTH);
        JButton searchbtn = new JButton("Search");
        JPanel btnpanel =new JPanel();
        btnpanel.add(searchbtn);
        add(btnpanel,BorderLayout.SOUTH);
        

        String[] columns={"USN","Name","Year","Branch","Section"};
        
        model= new DefaultTableModel(columns,0);
        JTable studenttable=new JTable(model);
        studenttable.setDefaultEditor(Object.class, null);
        JScrollPane scrollpane=new JScrollPane(studenttable); 
        scrollpane.setBorder(BorderFactory.createTitledBorder("List of Students"));
        add(scrollpane,BorderLayout.CENTER);
        scrollpane.setVisible(true);
        searchbtn.addActionListener(new ActionListener(){
        public void actionPerformed (ActionEvent ae){
            String year=(String)jbc1.getSelectedItem();
            String branch=(String)jbc2.getSelectedItem();
            String sec=(String)jbc3.getSelectedItem();   
            loadstudentlist(year,branch,sec);
            }    

        });

    studenttable.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = studenttable.rowAtPoint(e.getPoint());
            int col = studenttable.columnAtPoint(e.getPoint());
            if (row != -1) {
                // Open profile when the Name column is clicked (column index 1) or on double-click anywhere
                if ( (col == 1 && e.getClickCount() >= 1) || e.getClickCount() == 2) {
                    String usn = String.valueOf(model.getValueAt(row, 0)); 
                    showStudentProfile(usn);
                }
            }
        }
    });

}

void loadstudentlist(String year,String branch,String sec){
model.setRowCount(0);
try{
    Connection conn = DriverManager.getConnection(
    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
    System.out.println("Connected");
    String query = "SELECT usn, name, year,branch, sec FROM studentinfo WHERE branch=? AND year=? AND sec=?";
    PreparedStatement psmt = conn.prepareStatement(query);
    psmt.setString(1, branch);
    psmt.setString(2, year);
    psmt.setString(3, sec);
    ResultSet rs = psmt.executeQuery();
    while(rs.next()){
        model.addRow(new Object[]{rs.getString("usn"), rs.getString("name"), rs.getString("year"), rs.getString("branch"), rs.getString("sec")});
    }
    conn.close();
}
catch(Exception e){
    e.printStackTrace();
    JOptionPane.showMessageDialog(this, "Error loading student list:\n" + e.getMessage());
}


}

private void showStudentProfile(String usn) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Student Profile - " + usn);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(this);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        frame.add(root);

        // Top panel with photo and details
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // Photo panel (left)
        JLabel photoLabel = new JLabel("No Photo", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(160,160));
        topPanel.add(photoLabel, BorderLayout.WEST);

        // Details panel (right)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        JLabel nameLbl = new JLabel();
        JLabel idLbl = new JLabel();
        JLabel usnLbl = new JLabel("USN: " + usn);
        JLabel branchLbl = new JLabel();
        JLabel yearLbl = new JLabel();
        JLabel secLbl = new JLabel();
        JLabel emailLbl = new JLabel();
        JLabel contactLbl = new JLabel();

        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        detailsPanel.add(nameLbl);
        detailsPanel.add(idLbl);
        detailsPanel.add(usnLbl);
        detailsPanel.add(branchLbl);
        detailsPanel.add(yearLbl);
        detailsPanel.add(secLbl);
        detailsPanel.add(emailLbl);
        detailsPanel.add(contactLbl);

        topPanel.add(detailsPanel, BorderLayout.CENTER);

        root.add(topPanel, BorderLayout.NORTH);

        // Borrowed books table below
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Book Name", "Book ID", "Issue Date", "Return Date"}, 0
        );
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Borrowed Books"));
        root.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);

        // Load student data and photo in background (same as before)
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468")) {

                String studentQuery = "SELECT * FROM studentinfo WHERE usn=?";
                PreparedStatement psmt = conn.prepareStatement(studentQuery);
                psmt.setString(1, usn);
                ResultSet rs = psmt.executeQuery();

                if (rs.next()) {
                    final String name = rs.getString("name");
                    final String email = rs.getString("email");
                    final String contact = rs.getString("contact");
                    final String branch = rs.getString("branch");
                    final String year = rs.getString("year");
                    final String sec = rs.getString("sec");

                    SwingUtilities.invokeLater(() -> {
                        nameLbl.setText("Name: " + name);
                        branchLbl.setText("Branch: " + branch);
                        yearLbl.setText("Year: " + year);
                        secLbl.setText("Section: " + sec);
                        emailLbl.setText("Email: " + email);
                        contactLbl.setText("Contact: " + contact);
                    });

                    // Load photo
                    String imageUrl = rs.getString("imageurl");
                    if (imageUrl == null || imageUrl.isEmpty()) {
                        imageUrl = "https://university-student-photos.s3.ap-south-1.amazonaws.com/049/" 
                                    + java.net.URLEncoder.encode("student_photos/" + usn + ".JPG", "UTF-8");
                    }
                    final String finalImageUrl = imageUrl;
                    SwingUtilities.invokeLater(() -> {
                        try {
                            ImageIcon icon = new ImageIcon(new java.net.URL(finalImageUrl));
                            Image img = icon.getImage().getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                            photoLabel.setIcon(new ImageIcon(img));
                            photoLabel.setText("");
                        } catch (Exception ex) { photoLabel.setText("No Photo"); }
                    });

                    // Load borrowed books
                    PreparedStatement psBooks = conn.prepareStatement(
                        "SELECT b.bookname, i.bookid, i.issuedate, i.returndate " +
                        "FROM issuedbooks i JOIN books b ON i.bookid=b.bookid " +
                        "WHERE i.studentusn=?"
                    );
                    psBooks.setString(1, usn);
                    ResultSet rsBooks = psBooks.executeQuery();
                    SwingUtilities.invokeLater(() -> model.setRowCount(0));
                    while (rsBooks.next()) {
                        Object[] row = {
                            rsBooks.getString("bookname"),
                            rsBooks.getInt("bookid"),
                            rsBooks.getDate("issuedate"),
                            rsBooks.getDate("returndate")
                        };
                        SwingUtilities.invokeLater(() -> model.addRow(row));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    });
}
}
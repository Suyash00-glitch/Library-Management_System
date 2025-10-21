import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;
import java.sql.ResultSet;

class SwingDemo {

    SwingDemo() {
        JFrame jfrm = new JFrame("Login Page");
        jfrm.setSize(300, 200);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);

        JLabel jlab = new JLabel("Enter USN:");
        JLabel jlab1 = new JLabel("Enter Password:");
        JLabel jlab2 = new JLabel(" "); // For messages

        JTextField jtext = new JTextField(15);
        JPasswordField jtext1 = new JPasswordField(15);

        // Role selection dropdown
        JLabel roleLabel = new JLabel("Select Role:");
        String[] roles = {"Student", "Admin"};
        JComboBox<String> roleSelect = new JComboBox<>(roles);

        JButton jb = new JButton("Submit");

        JPanel panel = new JPanel();
        panel.add(jlab);
        panel.add(jtext);
        panel.add(jlab1);
        panel.add(jtext1);
        panel.add(roleLabel);
        panel.add(roleSelect);
        panel.add(jb);
        panel.add(jlab2);

        jfrm.add(panel);
        jfrm.setVisible(true);

        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String usn = jtext.getText();
                String pass = new String(jtext1.getPassword());
                String role = (String) roleSelect.getSelectedItem();

                boolean success = false;
                if (role.equals("Admin")) {
                    success = checkAdminLogin(usn, pass);
                    if (success) {
                        jlab2.setText("Login Successful! (Admin)");
                        new AdminDashboard(); // Assumes AdminDashboard() exists
                        jfrm.dispose();
                    }
                } else {
                    success = checkStudentLogin(usn, pass);
                    if (success) {
                        jlab2.setText("Login Successful! (Student)");
                        new StudentDashboard(usn);
                        jfrm.dispose();
                    }
                }

                if (!success) {
                    jlab2.setText("Invalid USN/Password for selected role");
                }
            }
        });
    }

    // ---------------- Student login ----------------
    boolean checkStudentLogin(String usn, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468"
            );

            String query = "SELECT * FROM students WHERE studentusn=? AND password=?";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, usn);
            psmt.setString(2, pass);

            ResultSet rs = psmt.executeQuery();
            boolean valid = rs.next();
            conn.close();
            return valid;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- Admin login ----------------
    boolean checkAdminLogin(String username, String pass) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468"
            );

            String query = "SELECT * FROM admins WHERE adminid=? AND password=?";
            PreparedStatement psmt = conn.prepareStatement(query);
            psmt.setString(1, username);
            psmt.setString(2, pass);

            ResultSet rs = psmt.executeQuery();
            boolean valid = rs.next();
            conn.close();
            return valid;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

public class logingui {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SwingDemo());
    }
}

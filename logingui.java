import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;

class SwingDemo {

    SwingDemo() {
        JFrame jfrm = new JFrame("Login page");
        jfrm.setSize(275, 150); 
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlab = new JLabel("Enter USN");
        JLabel jlab1 = new JLabel("Enter Password");
        JLabel jlab2 = new JLabel(" "); 

        JTextField jtext = new JTextField(15);
        JPasswordField jtext1 = new JPasswordField(15);

        JButton jb = new JButton("Submit");

        JPanel panel = new JPanel();

        panel.add(jlab);
        panel.add(jtext);
        panel.add(jlab1);
        panel.add(jtext1);
        panel.add(jb);
        panel.add(jlab2);

        jfrm.add(panel);
        jfrm.setVisible(true);

        jb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String usn = jtext.getText();
                String pass = new String(jtext1.getPassword());

                boolean success = checkLogin(usn, pass);
                if (success) {
                    jlab2.setText("Login Successful!");
                    StudentDashboard.loggedInUSN = usn; new StudentDashboard();
                } else {
                    jlab2.setText("Invalid USN or Password");
                }
            }
        });
    }

    boolean checkLogin(String usn, String pass) {
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
}

public class logingui {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SwingDemo();
            }
        });
    }
}

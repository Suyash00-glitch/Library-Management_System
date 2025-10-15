import java.awt.*;
import javax.swing.*;
//import javax.swing.border.Border;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;



class StudentDashboard{
    public static String loggedInUSN;
StudentDashboard(){
    JFrame jfrm = new JFrame("Student Dashboard");
        jfrm.setSize(500, 500);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);
        JTabbedPane jtp= new JTabbedPane();
        jtp.addTab("Search Books",new SearchBooks());
        jtp.addTab("Profile",new Profile());
        jfrm.add(jtp);
        jfrm.setVisible(true);
}
    public static void main(String args[]){
            SwingUtilities.invokeLater(() -> new StudentDashboard());
        }
    
}
class SearchBooks extends JPanel{
    SearchBooks(){
        setLayout(new BorderLayout(10, 10));
        JPanel formPanel = new JPanel(new GridLayout(3,2,5,5));
        JLabel jb1=new JLabel("Book id");
        JTextField jt1=new JTextField();
        JLabel jb2=new JLabel("Book Author");
        JTextField jt2=new JTextField();
        JLabel jb3=new JLabel("");
        formPanel.add(jb1);
        formPanel.add(jt1);
        formPanel.add(jb2);
        formPanel.add(jt2);
        formPanel.add(jb3);
        add(formPanel,BorderLayout.NORTH);
        JPanel btnpanel=new JPanel();
        JButton searchbtn=new JButton("Search");
        btnpanel.add(searchbtn);
        add(btnpanel,BorderLayout.CENTER);
        

        searchbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                int id=Integer.parseInt(jt1.getText());
                String author=jt2.getText();
                boolean success= showbooks(id,author);
                if (success){
                    jb3.setText("Fetched");
                }
                else{
                    jb3.setText("Cant Fetch");
                }
            
            }
        });
    }

    boolean showbooks(int id,String author){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            
            String checkqty="SELECT * from booksnew where bookid=?";
            PreparedStatement psmt1=conn.prepareStatement(checkqty);
            psmt1.setInt(1,id);
            ResultSet rs=psmt1.executeQuery();
            if (rs.next()) {
                String title = rs.getString("bookname");
                int qty = rs.getInt("bookquantity");

                JOptionPane.showMessageDialog(
                        this,
                        "Book Found:\n\nID: " + id +
                                "\nAuthor: " + author +
                                "\nTitle:"+title+
                                "\nQuantity: " +qty,
                        "Book Details",
                        JOptionPane.INFORMATION_MESSAGE
                );
                conn.close();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "No book found with that ID and Author.", "Not Found", JOptionPane.WARNING_MESSAGE);
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
class Profile extends JPanel {

    Profile() {  
        setLayout(new BorderLayout(10, 10));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5)); 
        JLabel imageLabel = new JLabel(); 

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468"
            );

            String query = "SELECT * FROM studentinfo WHERE usn = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, StudentDashboard.loggedInUSN);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                infoPanel.add(new JLabel("Name:"));
                infoPanel.add(new JLabel(rs.getString("name")));

                infoPanel.add(new JLabel("USN:"));
                infoPanel.add(new JLabel(rs.getString("usn")));

                infoPanel.add(new JLabel("Branch:"));
                infoPanel.add(new JLabel(rs.getString("branch")));

                infoPanel.add(new JLabel("Section:"));
                infoPanel.add(new JLabel(rs.getString("sec")));

                infoPanel.add(new JLabel("Year:"));
                infoPanel.add(new JLabel(rs.getString("year")));

                infoPanel.add(new JLabel("Email:"));
                infoPanel.add(new JLabel(rs.getString("email")));

                infoPanel.add(new JLabel("Contact:"));
                infoPanel.add(new JLabel(rs.getString("contact")));

            
                String imageUrl = rs.getString("imageurl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    ImageIcon icon = new ImageIcon(imageUrl);
                    
                    Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(img));
                }
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.WARNING_MESSAGE);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }

        add(imageLabel, BorderLayout.NORTH); 
        add(infoPanel, BorderLayout.CENTER);
    }
}

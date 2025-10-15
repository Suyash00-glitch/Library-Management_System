import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;



class StudentDashboard{
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
class Profile extends JPanel{
    Profile(){

    }

}


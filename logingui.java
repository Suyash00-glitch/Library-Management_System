import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
class SwingDemo{

SwingDemo(){
JFrame jfrm=new JFrame("Login page");
jfrm.setSize(275,100);
jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
JLabel jlab0=new JLabel("Enter Name");
JLabel jlab=new JLabel("Enter Usn");
JLabel jlab1=new JLabel("Enter Password");
JLabel jlab2=new JLabel(" "); 
JTextField jtext0=new JTextField(15);
JTextField jtext=new JTextField(15);
JTextField jtext1=new JTextField(15);
JButton jb=new JButton("Submit");

JPanel panel=new JPanel();
panel.add(jlab0);
panel.add(jtext0);
panel.add(jlab);
panel.add(jtext);
panel.add(jlab1);

panel.add(jtext1);
panel.add(jb);
panel.add(jlab2);
jfrm.add(panel);
jfrm.setVisible(true);
jb.addActionListener(new ActionListener(){
  public void actionPerformed(ActionEvent ae){
  String name=jtext0.getText();
  String usn=jtext.getText();
  String pass=jtext1.getText();
  
        boolean success = savetodb(name,usn, pass);
        if (success) {
            jlab2.setText("User saved to db");
        } else {
            jlab2.setText("User not saved to db");
        }
    
  }
});

}




boolean savetodb(String name,String usn,String pass){
      try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            System.out.println("Connected");
            String query="INSERT INTO students(studentname,studentusn,password) VALUES (?,?,?)";
            PreparedStatement psmt=conn.prepareStatement(query);
            psmt.setString(1,name);
            psmt.setString(2,usn);
            psmt.setString(3,pass);
            int rows=psmt.executeUpdate();
            conn.close();
            return rows>0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

public class logingui{
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
           public void run(){
            new SwingDemo();
           }

        });
    }
    
}


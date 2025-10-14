import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.sql.PreparedStatement;
//import java.sql.Connection;
//import java.sql.DriverManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.JTable;
//import javax.swing.border.Border;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
//import java.sql.Date;
//import java.sql.ResultSet;
import java.sql.*;


public class AdminDashboard {
    AdminDashboard(){
        JFrame jfrm = new JFrame("Admin Dashboard");
        jfrm.setSize(500, 500);
        jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jfrm.setLocationRelativeTo(null);


        JTabbedPane jtp=new JTabbedPane();
        jtp.addTab("Manage Books",new Managebooks());
        jtp.addTab("Manage students",new ManageStudents());
        jfrm.add(jtp);
        jfrm.setVisible(true);
    }
        public static void main(String args[]){
            SwingUtilities.invokeLater(() -> new AdminDashboard());
        }
    }
class Managebooks extends JPanel{
    Managebooks(){
    setLayout(new BorderLayout());
    JTabbedPane bookopr=new JTabbedPane();
    bookopr.addTab("Add books",new AddBook());
    bookopr.addTab("Delete books",new DeleteBook());
    bookopr.addTab("View Books",new ViewBook());
    bookopr.addTab("Issue Book",new IssueBook());
    bookopr.addTab("Return Book",new ReturnBook());
    add(bookopr,BorderLayout.CENTER);
    }
}
class AddBook extends JPanel{
    AddBook(){
    
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4,2,5,5));
        JLabel lb1 = new JLabel("Book Name:");
JTextField name = new JTextField(10);

JLabel lb2 = new JLabel("Book Number:");
JTextField number = new JTextField(10);
JLabel lb3=new JLabel("Book Quantity");
JTextField quantity=new JTextField(10);
JLabel lb4=new JLabel("");
formPanel.add(lb1);
formPanel.add(name);
formPanel.add(lb2);
formPanel.add(number);
formPanel.add(lb3);
formPanel.add(quantity);
formPanel.add(lb4);
add(formPanel, BorderLayout.CENTER);
        JPanel btnpanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addbtn = new JButton("Add Book");
        btnpanel.add(addbtn);
        add(btnpanel, BorderLayout.SOUTH);
        addbtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae){
                String bname=name.getText();
                int id=Integer.parseInt(number.getText());
                int qty=Integer.parseInt(quantity.getText());
                boolean success=savetodb(bname,id,qty);
                if(success){
                    lb4.setText("Successfull");
                }
                else{
                    lb4.setText("Unsucessful");
                }
            }
        });
    }
    boolean savetodb(String name,int id,int qty){
            try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query="INSERT INTO books(bookname,bookid,bookquantity) VALUES (?,?,?)";
            PreparedStatement psmt=conn.prepareStatement(query);
            psmt.setString(1,name);
            psmt.setInt(2,id);
            psmt.setInt(3,qty);
            int rows=psmt.executeUpdate();
            conn.close();
            return rows>0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
class DeleteBook extends JPanel{
    DeleteBook(){
        setLayout(new BorderLayout(10, 10));
    JPanel formpanel=new JPanel(new GridLayout(1,2,5,5));
    JLabel lb2=new JLabel("Book Number");
    JTextField number=new JTextField(10);
    JLabel lb3=new JLabel("");

        formpanel.add(lb2);
    formpanel.add(number);
    formpanel.add(lb3);
    add(formpanel,BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deletebtn = new JButton("Delete Button");
        btnPanel.add(deletebtn);
        add(btnPanel, BorderLayout.SOUTH);
        deletebtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    int id=Integer.parseInt(number.getText());
                    boolean success=deletefromdb(id);
                    if(success){
                        lb3.setText("Deleted");
                    }else{
                        lb3.setText("Not Deleted");
                    }

                }




        });

    }
    boolean deletefromdb(int id){
            try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query="DELETE FROM books WHERE bookid =?";
            PreparedStatement psmt=conn.prepareStatement(query);
            psmt.setInt(1,id);
            int rows=psmt.executeUpdate();
            conn.close();
            return rows>0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}

class ViewBook extends JPanel{
    DefaultTableModel model; 
    JScrollPane scrollpane;
    ViewBook(){
        
        setLayout(new BorderLayout(10, 10));
    JPanel btnpanel=new JPanel();
        JButton jb=new JButton("Refresh");
        btnpanel.add(jb);
        add(btnpanel,BorderLayout.SOUTH);

        String[] columNames={"Book Name","Book ID","Book Quantity"};
        model=new DefaultTableModel(columNames,0);
        JTable table=new JTable(model);
        scrollpane=new JScrollPane(table);
        add(scrollpane,BorderLayout.CENTER);

        jb.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ae){
                loadbooks();

            }




        });
    }
    void loadbooks(){
        try{
            model.setRowCount(0);
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            String query="SELECT * FROM books";
            PreparedStatement psmt=conn.prepareStatement(query);
            ResultSet rs=psmt.executeQuery();
            
            while (rs.next()) {
        
                String name = rs.getString("bookname");
                int id = rs.getInt("bookid");
                int qty = rs.getInt("bookquantity");
                model.addRow(new Object[]{name, id, qty});
            }
            
            conn.close();



        }

        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
        
    }
    }
    
class IssueBook extends JPanel{

    DefaultTableModel model;
    IssueBook(){
    setLayout(new BorderLayout(10, 10));
    JPanel formpanel=new JPanel(new GridLayout(1,2,5,5));
    JLabel jb1=new JLabel("Student usn");
    JTextField jt1=new JTextField(10);
    JLabel jb2=new JLabel("Book Id");
    JTextField jt2=new JTextField(10);
    JButton issuebtn=new JButton("Issue Book");
    JLabel jb3=new JLabel("");
    formpanel.add(jb1);
    formpanel.add(jt1); 
    formpanel.add(jb2); 
    formpanel.add(jt2); 
    formpanel.add(jb3);
    add(formpanel,BorderLayout.NORTH);
    JPanel btnpanel = new JPanel();
    btnpanel.add(issuebtn);
    add(btnpanel,BorderLayout.SOUTH);
    
        String[] columns = {"ID", "Student USN", "Book ID", "Issue Date", "Return Date", "Status"};
        model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Issued Books"));
        add(scrollPane, BorderLayout.CENTER);

        loadIssuedBooks();


    issuebtn.addActionListener(new ActionListener(){
    public void actionPerformed (ActionEvent ae){
        String usn=jt1.getText();
        int bookidd=Integer.parseInt(jt2.getText());
        boolean success=issuebookdb(usn,bookidd);
        if(success){
            jb3.setText("Book issued");
            loadIssuedBooks();
        }
        else{
            jb3.setText("Not issued");
        }

}
    });
}
boolean issuebookdb(String usn,int bookidd){
    try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");
            
            String checkqty="SELECT bookquantity FROM books WHERE bookid=?";
            PreparedStatement psmt1=conn.prepareStatement(checkqty);
            psmt1.setInt(1,bookidd);
            ResultSet rs=psmt1.executeQuery();
            if(rs.next()){
                    int qty=rs.getInt("bookquantity");
                    if(qty<=0){
                        JOptionPane.showMessageDialog(this, "Cannot issue. Book is out of stock!");
                        return false;
                    }
            }
            String issuebook="INSERT INTO issuedbooks (studentusn,bookid,issuedate) VALUES (?,?,CURDATE())";
            PreparedStatement psmt2=conn.prepareStatement(issuebook);
            psmt2.setString(1,usn);
            psmt2.setInt(2,bookidd);
            int rows=psmt2.executeUpdate();
    

            String updateqty="UPDATE books SET bookquantity=bookquantity-1 WHERE bookid=?";
            PreparedStatement psmt3=conn.prepareStatement(updateqty);
            psmt3.setInt(1,bookidd);
            psmt3.executeUpdate();

    conn.close();
    return rows>0;
            
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


class ReturnBook extends JPanel{

    ReturnBook(){
        setLayout(new BorderLayout(10, 10));
        JPanel formpanel=new JPanel(new GridLayout(1,2,5,5));
        JLabel jb1=new JLabel("Student usn");
        JTextField jt1=new JTextField("20");
        JLabel jb2=new JLabel("Book id");
        JTextField jt2=new JTextField("20");
        JLabel jb3=new JLabel(""); 
        formpanel.add(jb1);
        formpanel.add(jt1);
        formpanel.add(jb2);
        formpanel.add(jt2);
        formpanel.add(jb3);
        add(formpanel,BorderLayout.NORTH);
        JPanel btnpanel=new JPanel();
        JButton submitbtn=new JButton("Submit");
        btnpanel.add(submitbtn);
        add(btnpanel,BorderLayout.CENTER);

        submitbtn.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent ae){
                    String usn=jt1.getText();
                    int bookid=Integer.parseInt(jt2.getText());
                    boolean success=updatedb(usn,bookid);
                    if(success){
                        jb3.setText("Updated");
                    }else{
                        jb3.setText("Not Updated");
                    }

                };
    });

    }

    boolean updatedb(String usn,int bookid){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468");

            String query = "DELETE FROM issuedbooks WHERE studentusn=? AND bookid=?";  
            PreparedStatement ps1 = conn.prepareStatement(query);
            ps1.setString(1, usn);
            ps1.setInt(2, bookid);
            int rs1=ps1.executeUpdate();
            


            String updateBookQty = "UPDATE books SET bookquantity = bookquantity + 1 WHERE bookid = ?";
            PreparedStatement ps2 = conn.prepareStatement(updateBookQty);
            ps2.setInt(1, bookid);
            int rs2=ps2.executeUpdate();
            conn.close();
            if (rs1>0 && rs2>0){
                return true;
            }
            return false;

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
        if (e.getClickCount() == 2) { 
            int row = studenttable.getSelectedRow();
            if (row != -1) {
                String usn = (String) model.getValueAt(row, 0); 
                showStudentDetails(usn);
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


private void showStudentDetails(String usn) {
    try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/librarysys", "root", "mybag468")) {

        String studentQuery = "SELECT * FROM studentinfo WHERE usn=?";
        PreparedStatement psmt = conn.prepareStatement(studentQuery);
        psmt.setString(1, usn);
        ResultSet rs = psmt.executeQuery();

        if (!rs.next()) return;

        String name = rs.getString("name");
        String branch = rs.getString("branch");
        String year = rs.getString("year");
        String sec = rs.getString("sec");
        String email = rs.getString("email");
        String contact = rs.getString("contact");
        String imageUrl = rs.getString("imageurl");

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                ImageIcon icon = new ImageIcon(new java.net.URL(imageUrl));
                Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                infoPanel.add(new JLabel(new ImageIcon(img)));
                infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            } catch (Exception ex) {
                System.out.println("Failed to load image: " + ex.getMessage());
            }
        }

        infoPanel.add(new JLabel("USN: " + usn));
        infoPanel.add(new JLabel("Name: " + name));
        infoPanel.add(new JLabel("Branch: " + branch));
        infoPanel.add(new JLabel("Year: " + year));
        infoPanel.add(new JLabel("Section: " + sec));

        if (email != null && !email.isEmpty()) {
            JLabel emailLabel = new JLabel("Email: " + email);
            infoPanel.add(emailLabel);
        }

        infoPanel.add(new JLabel("Contact: " + contact));
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        String[] bookColumns = {"Book ID", "Book Name", "Issue Date", "Status"};
        DefaultTableModel bookModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable bookTable = new JTable(bookModel);

        String borrowQuery = "SELECT i.bookid, b.bookname, i.issuedate, " +
                             "CASE WHEN i.returndate IS NULL THEN 'Issued' ELSE 'Returned' END AS status " +
                             "FROM issuedbooks i JOIN books b ON i.bookid = b.bookid " +
                             "WHERE i.studentusn=?";
        PreparedStatement psmt2 = conn.prepareStatement(borrowQuery);
        psmt2.setString(1, usn);
        ResultSet rsBooks = psmt2.executeQuery();

        while (rsBooks.next()) {
            bookModel.addRow(new Object[]{
                    rsBooks.getInt("bookid"),
                    rsBooks.getString("bookname"),
                    rsBooks.getDate("issuedate"),
                    rsBooks.getString("status")
            });
        }

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        infoPanel.add(scrollPane);

        JOptionPane.showMessageDialog(this, infoPanel, "Student Details", JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load student details: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}
 
package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.print.*;
import javax.swing.table.*;
import java.util.Vector;


public class AdminFrame extends JFrame {

    private JTable studentTable;
    private JScrollPane scrollPane;
    private JPanel manageStudentPanel;
    private JPanel studentDetailsPanel;
    private int selectedStudentId = -1;  

    public AdminFrame() {
        setTitle("Admin Dashboard");
        setSize(1200, 700);  
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JButton manageStudentButton = new JButton("Manage Students");
        JButton manageStudentReportsButton = new JButton("View Student Reports");
        JButton assignTeacherButton = new JButton("Assign Teachers");
        JButton logoutButton = new JButton("Logout");
        JButton refreshButton = new JButton("Refresh Data"); 
        JButton manageAssessmentsButton = new JButton("Manage Assessments");

      
        logoutButton.addActionListener(e -> logOut());

   
        manageStudentButton.addActionListener(e -> showManageStudentPanel());
        manageStudentReportsButton.addActionListener(e -> showStudentReports());
        assignTeacherButton.addActionListener(e -> assignTeacherButtonAction());

       
        refreshButton.addActionListener(e -> loadStudentsData("All"));  

      
        manageAssessmentsButton.addActionListener(e -> openAssessmentModule());

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 10));  
        buttonPanel.add(manageStudentButton);
        buttonPanel.add(manageStudentReportsButton);
        buttonPanel.add(assignTeacherButton);
        buttonPanel.add(manageAssessmentsButton);  
        buttonPanel.add(logoutButton);
        buttonPanel.add(refreshButton);

    
        add(welcomeLabel, BorderLayout.NORTH); 
        add(buttonPanel, BorderLayout.WEST);  

   
        setVisible(true);
    }
   
    
    private void logOut() {
        int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to log out?", "Confirm Logout", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose(); 
        }
    }

    private void showManageStudentPanel() {
     
        if (manageStudentPanel != null) {
            remove(manageStudentPanel); 
        }

        manageStudentPanel = new JPanel();
        manageStudentPanel.setLayout(new BorderLayout());

      
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridLayout(2, 2));

        JLabel classLabel = new JLabel("Class:");
        JComboBox<String> classComboBox = new JComboBox<>(new String[]{"All", "6", "7", "8"});
        filterPanel.add(classLabel);
        filterPanel.add(classComboBox);

        JButton filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> loadStudentsData((String) classComboBox.getSelectedItem()));

        filterPanel.add(new JLabel()); 
        filterPanel.add(filterButton);

        
        manageStudentPanel.add(filterPanel, BorderLayout.NORTH);

     
        loadStudentsData("All");

        
        studentDetailsPanel = new JPanel();
        studentDetailsPanel.setLayout(new BorderLayout());

        JLabel detailsLabel = new JLabel("Select a student to view details and manage actions", SwingConstants.CENTER);
        detailsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        studentDetailsPanel.add(detailsLabel, BorderLayout.CENTER);

        
        add(manageStudentPanel, BorderLayout.CENTER);
        
        add(studentDetailsPanel, BorderLayout.SOUTH);  
        revalidate();
        repaint();
    }

   private void loadStudentsData(String classFilter) {
    
    if (scrollPane != null) {
        remove(scrollPane); 
    }

  
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement()) {

        String query = "SELECT * FROM users WHERE role = 'student'";
        if (!classFilter.equals("All")) {
            query += " AND class = '" + classFilter + "'";
        }

        ResultSet rs = stmt.executeQuery(query);

        // Creating column names
        Vector<String> columnNames = new Vector<>();
        columnNames.add("ID");
        columnNames.add("Username");
        columnNames.add("First Name");
        columnNames.add("Last Name");
        columnNames.add("Gender");
        columnNames.add("Bio");
        columnNames.add("Class");
        columnNames.add("Parent Name");
        columnNames.add("Location");

        // Creating data rows
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("id"));
            row.add(rs.getString("username"));
            row.add(rs.getString("firstName"));
            row.add(rs.getString("lastName"));
            row.add(rs.getString("gender"));
            row.add(rs.getString("bio"));
            row.add(rs.getString("class"));
            row.add(rs.getString("parentName"));
            row.add(rs.getString("location"));

            // Adding row to data
            data.add(row);
        }

        // Creating the table
        studentTable = new JTable(data, columnNames);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = studentTable.getSelectedRow();
                if (row != -1) {
                    selectedStudentId = (int) studentTable.getValueAt(row, 0);
                    showStudentDetails(selectedStudentId);
                }
            }
        });

        scrollPane = new JScrollPane(studentTable);
        manageStudentPanel.add(scrollPane, BorderLayout.CENTER);

    
        revalidate();
        repaint();
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading students data: " + e.getMessage());
    }
}


    private void showStudentDetails(int studentId) {
 
    if (studentId != -1) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("firstName") + " " + rs.getString("lastName");
                String studentClass = rs.getString("class");
                String studentGender = rs.getString("gender");
                String studentBio = rs.getString("bio");
                String studentParentName = rs.getString("parentName");
                String studentLocation = rs.getString("location");

             
                JPanel detailsPanel = new JPanel();
                detailsPanel.setLayout(new GridLayout(7, 2));
                detailsPanel.add(new JLabel("Name:"));
                detailsPanel.add(new JLabel(studentName));
                detailsPanel.add(new JLabel("Class:"));
                detailsPanel.add(new JLabel(studentClass));
                detailsPanel.add(new JLabel("Gender:"));
                detailsPanel.add(new JLabel(studentGender));
                detailsPanel.add(new JLabel("Bio:"));
                detailsPanel.add(new JLabel(studentBio));
                detailsPanel.add(new JLabel("Parent Name:"));
                detailsPanel.add(new JLabel(studentParentName));
                detailsPanel.add(new JLabel("Location:"));
                detailsPanel.add(new JLabel(studentLocation));

               
                JButton editButton = new JButton("Edit");
                editButton.addActionListener(e -> editStudentDetails(studentId)); 

                JButton deleteButton = new JButton("Delete");
                deleteButton.addActionListener(e -> deleteStudent(studentId)); 

               
                detailsPanel.add(editButton);
                detailsPanel.add(deleteButton);

                studentDetailsPanel.removeAll();
                studentDetailsPanel.add(detailsPanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage());
        }
    }
}

private void editStudentDetails(int studentId) {
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?")) {
        stmt.setInt(1, studentId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()){
            String firstName = rs.getString("firstName");
            String lastName = rs.getString("lastName");
            String studentClass = rs.getString("class");
            String gender = rs.getString("gender");
            String bio = rs.getString("bio");
            String parentName = rs.getString("parentName");
            String location = rs.getString("location");
            JDialog editDialog = new JDialog(this, "Edit Student Details", true);
            editDialog.setSize(400, 300);
            editDialog.setLayout(new GridLayout(8, 2));
            JTextField firstNameField = new JTextField(firstName);
            JTextField lastNameField = new JTextField(lastName);
            JTextField classField = new JTextField(studentClass);
            JTextField genderField = new JTextField(gender);
            JTextField bioField = new JTextField(bio);
            JTextField parentNameField = new JTextField(parentName);
            JTextField locationField = new JTextField(location);

            JButton saveButton = new JButton("Save Changes");
            saveButton.addActionListener(e -> {
                try {
                    String updateQuery = "UPDATE users SET firstName = ?, lastName = ?, class = ?, gender = ?, bio = ?, parentName = ?, location = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setString(1, firstNameField.getText());
                    updateStmt.setString(2, lastNameField.getText());
                    updateStmt.setString(3, classField.getText());
                    updateStmt.setString(4, genderField.getText());
                    updateStmt.setString(5, bioField.getText());
                    updateStmt.setString(6, parentNameField.getText());
                    updateStmt.setString(7, locationField.getText());
                    updateStmt.setInt(8, studentId);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        JOptionPane.showMessageDialog(editDialog, "Student details updated successfully.");
                        editDialog.dispose(); 
                    } else {
                        JOptionPane.showMessageDialog(editDialog, "Error updating student details.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(editDialog, "Error saving changes.");
                }
            });

           
            editDialog.add(new JLabel("First Name:"));
            editDialog.add(firstNameField);
            editDialog.add(new JLabel("Last Name:"));
            editDialog.add(lastNameField);
            editDialog.add(new JLabel("Class:"));
            editDialog.add(classField);
            editDialog.add(new JLabel("Gender:"));
            editDialog.add(genderField);
            editDialog.add(new JLabel("Bio:"));
            editDialog.add(bioField);
            editDialog.add(new JLabel("Parent Name:"));
            editDialog.add(parentNameField);
            editDialog.add(new JLabel("Location:"));
            editDialog.add(locationField);
            editDialog.add(new JLabel());  
            editDialog.add(saveButton);

            editDialog.setVisible(true);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching student details for editing: " + ex.getMessage());
    }
}


    private void deleteStudent(int studentId) {
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            stmt.setInt(1, studentId);
            int rowsAffected = stmt.executeUpdate();


            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");

             
                studentDetailsPanel.removeAll();
                revalidate();  
                repaint();     

                
                loadStudentsData("All");  
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete the student.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage());
        }
    }
}


  
   
    private void showStudentReports() {
  
    JPanel reportPanel = new JPanel();
    reportPanel.setLayout(new BorderLayout());

    // Creating search components
    JTextField searchField = new JTextField(20);
    JButton searchButton = new JButton("Search");
    JPanel searchPanel = new JPanel();
    searchPanel.add(new JLabel("Search by Student Name:"));
    searchPanel.add(searchField);
    searchPanel.add(searchButton);

    // Adding the search panel to the report panel
    reportPanel.add(searchPanel, BorderLayout.NORTH);

    // Table columns for the reports
    Vector<String> columnNames = new Vector<>();
    columnNames.add("Student Name");
    columnNames.add("Subject");
    columnNames.add("Score");
    columnNames.add("Remarks");

    Vector<Vector<Object>> data = new Vector<>();

    // Fetching and displaying all student reports on initial load
    fetchStudentReports("", data, columnNames, reportPanel);

    // Search action listener
    searchButton.addActionListener(e -> {
        String searchName = searchField.getText().trim();
        if (!searchName.isEmpty()) {
            fetchStudentReports(searchName, data, columnNames, reportPanel);
        } else {
            fetchStudentReports("", data, columnNames, reportPanel);
        }
    });

   
    if (manageStudentPanel != null) {
        remove(manageStudentPanel); 
    }

    // Add the report panel to the main frame
    add(reportPanel, BorderLayout.CENTER);
    revalidate();
    repaint();
}


// This method fetcheing student reports based on name or all reports
private void fetchStudentReports(String studentName, Vector<Vector<Object>> data, Vector<String> columnNames, JPanel mainPanel) {
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement()) {

        // Construct query for fetching reports based on student name
        String query = "SELECT sa.student_id, sa.subject, sa.score, sa.remarks " +
                       "FROM student_assessments sa " +
                       (studentName.isEmpty() ? "" : "WHERE sa.student_id LIKE '%" + studentName + "%'");

        ResultSet rs = stmt.executeQuery(query);

        // Clearing previous data
        data.clear();

        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            row.add(rs.getInt("student_id"));
            row.add(rs.getString("subject"));
            row.add(rs.getString("score"));
            row.add(rs.getString("remarks"));
            data.add(row);
        }

        // here we are updating the JTable with modern styles
        JTable reportTable = new JTable(new DefaultTableModel(data, columnNames)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

       
        reportTable.setRowHeight(30);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 14));
        JTableHeader header = reportTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(66, 133, 244)); 
        header.setForeground(Color.WHITE);

        // Alternating row coloring for better readability
        reportTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(240, 240, 240)); 
                } else {
                    c.setBackground(Color.WHITE); 
                }
                if (isSelected) {
                    c.setBackground(new Color(0, 123, 255)); 
                }
                return c;
            }
        });

       
        JScrollPane scrollPane = new JScrollPane(reportTable);


        JButton printButton = new JButton("Print");
        printButton.setFont(new Font("Arial", Font.BOLD, 14));
        printButton.setBackground(new Color(66, 133, 244)); 
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> printReport(reportTable));

        
        mainPanel.removeAll();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE); 
        buttonPanel.add(printButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.revalidate();
        mainPanel.repaint();

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching student reports: " + e.getMessage());
    }
}


private void printReport(JTable reportTable) {
    try {
        boolean printed = reportTable.print();
        if (!printed) {
            JOptionPane.showMessageDialog(this, "Report could not be printed.");
        }
    } catch (PrinterException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error printing report: " + e.getMessage());
    }
}


    private void openAssessmentModule() {
    new AssessmentModule();  
}


   
    private void assignTeacherButtonAction() {
    new TeacherAssignmentModule(); 
}

    public static void main(String[] args) {
        new AdminFrame();  
    }
}

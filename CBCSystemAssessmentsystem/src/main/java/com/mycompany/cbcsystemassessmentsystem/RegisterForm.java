package com.mycompany.cbcsystemassessmentsystem;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField usernameField;
    private JComboBox<String> genderComboBox;
    private JTextArea bioArea;
    private JComboBox<String> roleComboBox;
    private JPasswordField passwordField;

  
    private JTextField occupationField;
    private JTextField titleField;

   
    private JTextField classField;
    private JTextField parentNameField;
    private JTextField locationField;

    public RegisterForm() {
        setTitle("CBC System Registration");
        setSize(1200, 700); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
               
                ImageIcon backgroundImage = new ImageIcon("/home/pc/Downloads/pexels-armstrong-opulency-185550616-11941089.jpg");
                Image img = backgroundImage.getImage();
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); 
                g2d.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Set a common font for the form fields and labels
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.PLAIN, 16);

        // Create a button for login
        JButton loginButton = new JButton("Log in here");
        loginButton.setFont(buttonFont);
        loginButton.setForeground(Color.white);
        loginButton.setBackground(Color.red);
        loginButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        loginButton.setPreferredSize(new Dimension(120, 40)); // Adjusted button size

        // Add action listener for loginButton
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the login screen
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
                setVisible(false);  // Hide the registration screen
            }
        });

        // Form fields setup
        usernameField = new JTextField(15);
        usernameField.setFont(fieldFont);
        addField(panel, gbc, "Username:", usernameField, 0);

        firstNameField = new JTextField(15);
        firstNameField.setFont(fieldFont);
        addField(panel, gbc, "First Name:", firstNameField, 1);

        lastNameField = new JTextField(15);
        lastNameField.setFont(fieldFont);
        addField(panel, gbc, "Last Name:", lastNameField, 2);

        genderComboBox = new JComboBox<>(new String[]{"Select", "Male", "Female", "Other"});
        genderComboBox.setFont(fieldFont);
        addField(panel, gbc, "Gender:", genderComboBox, 3);

        bioArea = new JTextArea(3, 15);
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        bioArea.setFont(fieldFont);
        JScrollPane bioScrollPane = new JScrollPane(bioArea);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Bio:"), gbc);
        gbc.gridx = 1;
        panel.add(bioScrollPane, gbc);

        roleComboBox = new JComboBox<>(new String[]{"Select", "Teacher", "Student"});
        roleComboBox.setFont(fieldFont);
        addField(panel, gbc, "Role:", roleComboBox, 5);

        passwordField = new JPasswordField(15);
        passwordField.setFont(fieldFont);
        addField(panel, gbc, "Password:", passwordField, 6);

        // Teacher and Student-specific fields
        occupationField = new JTextField(15);
        occupationField.setFont(fieldFont);
        titleField = new JTextField(15);
        titleField.setFont(fieldFont);
        classField = new JTextField(15);
        classField.setFont(fieldFont);
        parentNameField = new JTextField(15);
        parentNameField.setFont(fieldFont);
        locationField = new JTextField(15);
        locationField.setFont(fieldFont);

        JPanel dynamicPanel = new JPanel(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(dynamicPanel, gbc);
        gbc.gridwidth = 1;

        roleComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDynamicFields(dynamicPanel);
            }
        });

        // Submit button
        JButton submitButton = new JButton("Register");
        submitButton.setFont(buttonFont);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        // Add only login button
        gbc.insets = new Insets(20, 10, 10, 10); 
        gbc.gridx = 1;
        gbc.gridy = 9;
        panel.add(loginButton, gbc);

        add(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void updateDynamicFields(JPanel dynamicPanel) {
        dynamicPanel.removeAll();
        String selectedRole = (String) roleComboBox.getSelectedItem();
        GridBagConstraints dGbc = new GridBagConstraints();
        dGbc.insets = new Insets(5, 5, 5, 5);
        dGbc.anchor = GridBagConstraints.WEST;
        dGbc.fill = GridBagConstraints.HORIZONTAL;  

        if ("Teacher".equals(selectedRole)) {
            addDynamicField(dynamicPanel, dGbc, "Occupation:", occupationField, 0);
            addDynamicField(dynamicPanel, dGbc, "Title:", titleField, 1);
        } else if ("Student".equals(selectedRole)) {
          
            classField.setPreferredSize(new Dimension(250, 30));
            parentNameField.setPreferredSize(new Dimension(250, 30));
            locationField.setPreferredSize(new Dimension(250, 30));

            addDynamicField(dynamicPanel, dGbc, "Class:", classField, 0);
            addDynamicField(dynamicPanel, dGbc, "Parent/Guardian Name:", parentNameField, 1);
            addDynamicField(dynamicPanel, dGbc, "Location:", locationField, 2);
        }

        dynamicPanel.revalidate();
        dynamicPanel.repaint();
    }

    private void addDynamicField(JPanel panel, GridBagConstraints gbc, String label, JComponent field, int yPos) {
        gbc.gridx = 0;
        gbc.gridy = yPos;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void registerUser() {
    String username = usernameField.getText();
    String firstName = firstNameField.getText();
    String lastName = lastNameField.getText();
    String gender = (String) genderComboBox.getSelectedItem();
    String bio = bioArea.getText();
    String role = (String) roleComboBox.getSelectedItem();
    String password = new String(passwordField.getPassword());

    String query = "INSERT INTO users (username, firstName, lastName, gender, bio, role, password, occupation, title, class, parentName, location) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, username);
        pstmt.setString(2, firstName);
        pstmt.setString(3, lastName);
        pstmt.setString(4, gender);
        pstmt.setString(5, bio);
        pstmt.setString(6, role);
        pstmt.setString(7, password);
        pstmt.setString(8, role.equals("Teacher") ? occupationField.getText() : null);
        pstmt.setString(9, role.equals("Teacher") ? titleField.getText() : null);
        pstmt.setString(10, role.equals("Student") ? classField.getText() : null);
        pstmt.setString(11, role.equals("Student") ? parentNameField.getText() : null);
        pstmt.setString(12, role.equals("Student") ? locationField.getText() : null);
        pstmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Registration successful!");

       
        LoginFrame loginFrame = new LoginFrame(); 
        loginFrame.setVisible(true);
        this.setVisible(false); 

        resetForm();
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
    }
}
    
    private void resetForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        usernameField.setText("");
        genderComboBox.setSelectedIndex(0);
        bioArea.setText("");
        roleComboBox.setSelectedIndex(0);
        passwordField.setText("");
        occupationField.setText("");
        titleField.setText("");
        classField.setText("");
        parentNameField.setText("");
        locationField.setText("");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterForm().setVisible(true);
            }
        });
    }
}

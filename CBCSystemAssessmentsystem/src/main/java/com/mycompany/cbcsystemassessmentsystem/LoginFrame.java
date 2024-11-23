/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.cbcsystemassessmentsystem;


import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton togglePasswordButton;
    private JButton forgotPasswordButton;
    private boolean isPasswordVisible = false;

    public LoginFrame() {
        // Frame properties
        setTitle("Login - CBC Assessment System");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(255, 255, 255);
                Color color2 = new Color(200, 200, 255);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        // Creating UI components
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        forgotPasswordButton = new JButton("Forgot Password?");
        togglePasswordButton = new JButton("👁️"); // Eye icon

        // Styling buttons and fields
        styleButton(loginButton, new Color(0, 123, 255));
        styleButton(registerButton, new Color(108, 117, 125));
        styleButton(forgotPasswordButton, Color.WHITE);
        styleTextField(usernameField);
        styleTextField(passwordField);

        // Set action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> openRegisterFrame());
        forgotPasswordButton.addActionListener(e -> handleForgotPassword());
        togglePasswordButton.addActionListener(e -> togglePasswordVisibility());

        // Layout setup
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        addComponent(mainPanel, gbc, 0, 0, new JLabel("Username:", JLabel.RIGHT));
        addComponent(mainPanel, gbc, 1, 0, usernameField);
        addComponent(mainPanel, gbc, 0, 1, new JLabel("Password:", JLabel.RIGHT));
        addComponent(mainPanel, gbc, 1, 1, passwordField);
        addComponent(mainPanel, gbc, 2, 1, togglePasswordButton); 
        addComponent(mainPanel, gbc, 1, 2, loginButton);
        addComponent(mainPanel, gbc, 1, 3, registerButton);
        addComponent(mainPanel, gbc, 1, 4, forgotPasswordButton); 

        // Add main panel to frame
        setContentPane(mainPanel);
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 170, 170), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void addComponent(JPanel panel, GridBagConstraints gbc, int x, int y, JComponent comp) {
        gbc.gridx = x;
        gbc.gridy = y;
        panel.add(comp, gbc);
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setEchoChar('•'); 
            togglePasswordButton.setText("👁️"); 
        } else {
            passwordField.setEchoChar((char) 0); 
            togglePasswordButton.setText("🙈"); 
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if ("group5".equals(username) && "admin123".equals(password)) {
            JOptionPane.showMessageDialog(this, "Admin Login Successful!");
            this.setVisible(false);
            new AdminFrame().setVisible(true);
            return;
        }

        String query = "SELECT username, role, id FROM users WHERE username = ? AND password = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                int userId = rs.getInt("id");

                if ("teacher".equals(role)) {
                    JOptionPane.showMessageDialog(this, "Teacher Login Successful!");
                    this.setVisible(false);
                    new TeacherFrame(userId).setVisible(true);
                } else if ("student".equals(role)) {
                    JOptionPane.showMessageDialog(this, "Student Login Successful!");
                    this.setVisible(false);
                    new StudentFrame(userId).setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this, "Unknown Role: " + role, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleForgotPassword() {
        String input = JOptionPane.showInputDialog(
            this, 
            "Enter your registered email or username:", 
            "Forgot Password", 
            JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                this, 
                "No input provided. Please enter your email or username.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        JOptionPane.showMessageDialog(
            this, 
            "Instructions to reset your password have been sent to: " + input, 
            "Password Recovery", 
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void openRegisterFrame() {
        new RegisterForm().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}

package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class StudentFrame extends JFrame {

    private JPanel mainPanel;
    private static int loggedInUserId = -1;

    public StudentFrame(int userId) {
        loggedInUserId = userId;
        setTitle("Student Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

      
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(5, 1, 10, 10)); 
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setBackground(new Color(50, 50, 200));
       

        JButton profileButton = new JButton("Profile");
        JButton subjectDetailsButton = new JButton("Subject Details");
        JButton showTimetableButton = new JButton("TimeTable");
        JButton logoutButton = new JButton("Log Out"); 
        
        JButton paymentButton = new JButton("Payment");
        styleNavButton(paymentButton);

        styleNavButton(profileButton);
        styleNavButton(subjectDetailsButton);
        styleNavButton(showTimetableButton);
        styleNavButton(logoutButton);

        navPanel.add(profileButton);
        navPanel.add(subjectDetailsButton);
        navPanel.add(showTimetableButton);
        navPanel.add(paymentButton);
        navPanel.add(logoutButton);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));

        JLabel label = new JLabel("Welcome to the Student Dashboard", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26));
        label.setForeground(new Color(70, 130, 180));
        mainPanel.add(label, BorderLayout.CENTER);

        JLabel footerLabel = new JLabel("Explore your dashboard", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        footerLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(footerLabel, BorderLayout.SOUTH);

        add(navPanel, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

      
        profileButton.addActionListener(e -> showProfilePanel());
        subjectDetailsButton.addActionListener(e -> showSubjectDetailsPanel());
        showTimetableButton.addActionListener(e -> showTimetablePanel());
      
        paymentButton.addActionListener(e -> showPaymentPanel());

      
        logoutButton.addActionListener(e -> logOut());
    }

    private void styleNavButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void logOut() {
        int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to log out?", "Confirm Logout", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            // Navigating back to LoginFrame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose(); 
        }
    }

    
    private void showProfilePanel() {
        mainPanel.removeAll();

        JLabel profileIcon = new JLabel(new ImageIcon("/home/pc/Downloads/1077114.png"));  
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);

        // Profile info panel
        JPanel profileInfoPanel = new JPanel();
        profileInfoPanel.setLayout(new GridLayout(7, 2, 10, 20)); 
        profileInfoPanel.setBackground(new Color(255, 255, 255)); 
        profileInfoPanel.setBorder(new EmptyBorder(10, 50, 50, 50)); 

        // Retrieving data and populate profile info
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE id = ?");
            statement.setInt(1, loggedInUserId);  
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                addProfileLabel(profileInfoPanel, "Username:", resultSet.getString("username"));
                addProfileLabel(profileInfoPanel, "First Name:", resultSet.getString("firstName"));
                addProfileLabel(profileInfoPanel, "Last Name:", resultSet.getString("lastName"));
                addProfileLabel(profileInfoPanel, "Gender:", resultSet.getString("gender"));
                addProfileLabel(profileInfoPanel, "Class:", resultSet.getString("class"));
                addProfileLabel(profileInfoPanel, "Location:", resultSet.getString("location"));
            } else {
                profileInfoPanel.add(new JLabel("No profile data found."));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load profile data.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel profileLabel = new JLabel("Student Profile", SwingConstants.CENTER);
        profileLabel.setFont(new Font("Arial", Font.BOLD, 24));
        profileLabel.setForeground(new Color(60, 80, 180));

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BorderLayout(10, 10));
        profilePanel.setBackground(new Color(240, 248, 255));  
        profilePanel.add(profileLabel, BorderLayout.NORTH);
        profilePanel.add(profileIcon, BorderLayout.CENTER);
        profilePanel.add(profileInfoPanel, BorderLayout.SOUTH);

        mainPanel.add(profilePanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void addProfileLabel(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(80, 80, 80));

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Arial", Font.BOLD, 16));
        value.setForeground(new Color(50, 50, 150));

        panel.add(label);
        panel.add(value);
    }

    private void showSubjectDetailsPanel() {
    mainPanel.removeAll();

    
    JLabel subjectDetailsLabel = new JLabel("Subject Performance and Remarks", SwingConstants.CENTER);
    subjectDetailsLabel.setFont(new Font("Arial", Font.BOLD, 24));
    subjectDetailsLabel.setForeground(new Color(60, 80, 180));
    mainPanel.add(subjectDetailsLabel, BorderLayout.NORTH);

  
    JPanel detailsPanel = new JPanel();
    detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS)); 
    detailsPanel.setBackground(new Color(255, 255, 255));  

    try {
        
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM student_assessments WHERE student_id = ?");
        statement.setInt(1, loggedInUserId);
        ResultSet resultSet = statement.executeQuery();

      
        while (resultSet.next()) {
            String subject = resultSet.getString("subject");
            int score = resultSet.getInt("score");
            String remarks = resultSet.getString("remarks");

            // Panel for each subject's performance and remarks
            JPanel subjectPanel = new JPanel();
            subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));  
            subjectPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));  
            subjectPanel.setBackground(Color.WHITE);
            subjectPanel.setPreferredSize(new Dimension(800, 150)); 
            subjectPanel.setMaximumSize(new Dimension(800, 150));  

            // Subject Name
            JLabel subjectLabel = new JLabel("Subject: " + subject);
            subjectLabel.setFont(new Font("Arial", Font.BOLD, 18));
            subjectLabel.setForeground(new Color(60, 60, 60));

            // Score
            JLabel scoreLabel = new JLabel("Score: " + score);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            scoreLabel.setForeground(new Color(50, 150, 50));  

            // Remarks
            JLabel remarksLabel = new JLabel("<html><b>Remarks:</b> " + remarks + "</html>");
            remarksLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            remarksLabel.setForeground(new Color(100, 100, 100));  

            // Add the components to the subject panel
            subjectPanel.add(subjectLabel);
            subjectPanel.add(scoreLabel);
            subjectPanel.add(remarksLabel);

            // Add each subject panel to the detailsPanel
            detailsPanel.add(subjectPanel);
            detailsPanel.add(Box.createVerticalStrut(10)); 
        }

        resultSet.close();
        statement.close();
        connection.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load subject details.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    JScrollPane scrollPane = new JScrollPane(detailsPanel);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setPreferredSize(new Dimension(800, 400));  

    mainPanel.add(scrollPane, BorderLayout.CENTER);
    mainPanel.revalidate();
    mainPanel.repaint();
}


    private void addSubjectDetail(JPanel panel, String labelText, String valueText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(80, 80, 80));

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Arial", Font.BOLD, 16));
        value.setForeground(new Color(50, 50, 150));

        panel.add(label);
        panel.add(value);
    }
    
    private void showTimetablePanel() {
    mainPanel.removeAll();

    JLabel timetableLabel = new JLabel("Your Timetable", SwingConstants.CENTER);
    timetableLabel.setFont(new Font("Arial", Font.BOLD, 24));
    timetableLabel.setForeground(new Color(60, 80, 180));

    // Creating a grid panel with 7 columns for each day of the week
    JPanel timetablePanel = new JPanel();
    timetablePanel.setLayout(new GridLayout(6, 6, 5, 5)); 
    timetablePanel.setBackground(new Color(240, 248, 255)); 

    // Days of the week (columns headers)
    String[] daysOfWeek = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    for (String day : daysOfWeek) {
        JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
        dayLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dayLabel.setBackground(new Color(200, 220, 255)); 
        dayLabel.setOpaque(true);
        timetablePanel.add(dayLabel);
    }

    // Time slots (rows headers)
    String[] timeSlots = {"8:00 AM", "10:00 AM", "12:00 PM", "2:00 PM", "4:00 PM"};
    try {
        Connection connection = DatabaseConnection.getConnection();
        PreparedStatement statement = connection.prepareStatement("SELECT subject, day, time FROM teacher_subject_assignments WHERE teacher_id = ?");
        statement.setInt(1, loggedInUserId);  
        ResultSet resultSet = statement.executeQuery();

        // Initialize a timetable grid with empty strings (no subject initially)
        String[][] timetable = new String[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                timetable[i][j] = ""; 
            }
        }

        // Fill in the timetable with subjects based on day and time
        while (resultSet.next()) {
            String subjectName = resultSet.getString("subject");
            String day = resultSet.getString("day");
            String time = resultSet.getString("time");

            int dayIndex = getDayIndex(day);  
            int timeIndex = getTimeIndex(time); 

            if (dayIndex != -1 && timeIndex != -1) {
                timetable[timeIndex][dayIndex] = subjectName;
            }
        }

        resultSet.close();
        statement.close();
        connection.close();

        // Add time slots and their corresponding subjects to the grid
        for (int i = 0; i < timeSlots.length; i++) {
            JLabel timeLabel = new JLabel(timeSlots[i], SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.BOLD, 14));
            timeLabel.setBackground(new Color(240, 240, 240)); 
            timeLabel.setOpaque(true);
            timetablePanel.add(timeLabel);

            for (int j = 1; j <= 5; j++) { 
                JLabel subjectLabel = new JLabel(timetable[i][j - 1], SwingConstants.CENTER);
                subjectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                if (!timetable[i][j - 1].isEmpty()) {
                    subjectLabel.setBackground(new Color(255, 255, 204)); 
                } else {
                    subjectLabel.setBackground(new Color(255, 255, 255)); 
                }
                subjectLabel.setOpaque(true);
                timetablePanel.add(subjectLabel);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load timetable.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main panel that holds the timetable label and grid
    JPanel mainTimetablePanel = new JPanel();
    mainTimetablePanel.setLayout(new BorderLayout());
    mainTimetablePanel.setBackground(new Color(240, 248, 255));  
    mainTimetablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
    mainTimetablePanel.add(timetableLabel, BorderLayout.NORTH);
    mainTimetablePanel.add(new JScrollPane(timetablePanel), BorderLayout.CENTER);

    mainPanel.add(mainTimetablePanel, BorderLayout.CENTER);
    mainPanel.revalidate();
    mainPanel.repaint();
}

//  methods to convert day and time to index
private int getDayIndex(String day) {
    switch (day) {
        case "Monday":
            return 1;
        case "Tuesday":
            return 2;
        case "Wednesday":
            return 3;
        case "Thursday":
            return 4;
        case "Friday":
            return 5;
        default:
            return -1;  
    }
}

private int getTimeIndex(String time) {
    switch (time) {
        case "08:00 AM":
            return 0;
        case "10:00 AM":
            return 1;
        case "12:00 PM":
            return 2;
        case "02:00 PM":
            return 3;
        case "04:00 PM":
            return 4;
        default:
            return -1;  
    }
}

    
   private void showPaymentPanel() {
    mainPanel.removeAll();

    // Title for the payment section
    JLabel paymentLabel = new JLabel("Fee Payment Section", SwingConstants.CENTER);
    paymentLabel.setFont(new Font("Arial", Font.BOLD, 24));
    paymentLabel.setForeground(new Color(60, 80, 180));
    mainPanel.add(paymentLabel, BorderLayout.NORTH);

    // Panel for payment actions
    JPanel paymentPanel = new JPanel(new BorderLayout());
    paymentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
    paymentPanel.setBackground(Color.WHITE);

    // Outstanding Fees and Payment Section
    JPanel paymentDetailsPanel = new JPanel();
    paymentDetailsPanel.setLayout(new GridLayout(5, 2, 10, 10));
    paymentDetailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    paymentDetailsPanel.setBackground(Color.WHITE);

    // Labels for fee and payment
    JLabel firstTermLabel = new JLabel("First Term Fee:");
    firstTermLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JLabel firstTermValue = new JLabel();
    firstTermValue.setFont(new Font("Arial", Font.BOLD, 18));
    firstTermValue.setForeground(new Color(200, 0, 0));

    JLabel secondTermLabel = new JLabel("Second Term Fee:");
    secondTermLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JLabel secondTermValue = new JLabel();
    secondTermValue.setFont(new Font("Arial", Font.BOLD, 18));
    secondTermValue.setForeground(new Color(200, 0, 0));

    JLabel thirdTermLabel = new JLabel("Third Term Fee:");
    thirdTermLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JLabel thirdTermValue = new JLabel();
    thirdTermValue.setFont(new Font("Arial", Font.BOLD, 18));
    thirdTermValue.setForeground(new Color(200, 0, 0));

    JLabel paymentAmountLabel = new JLabel("Enter Payment Amount:");
    paymentAmountLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JTextField paymentAmountField = new JTextField();

    JLabel selectTermLabel = new JLabel("Select Term to Pay For:");
    selectTermLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    JComboBox<String> termComboBox = new JComboBox<>(new String[] { "First Term", "Second Term", "Third Term" });

    // Fetching term fees from the database
    try (Connection connection = DatabaseConnection.getConnection()) {
        PreparedStatement statement = connection.prepareStatement(
            "SELECT first_term_fee, second_term_fee, third_term_fee FROM term_fees WHERE student_id = ?");
        statement.setInt(1, loggedInUserId); 
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            firstTermValue.setText("KES " + resultSet.getDouble("first_term_fee"));
            secondTermValue.setText("KES " + resultSet.getDouble("second_term_fee"));
            thirdTermValue.setText("KES " + resultSet.getDouble("third_term_fee"));
        } else {
            firstTermValue.setText("No fees found.");
            secondTermValue.setText("No fees found.");
            thirdTermValue.setText("No fees found.");
        }

        resultSet.close();
        statement.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load fee details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Adding to payment details panel
    paymentDetailsPanel.add(firstTermLabel);
    paymentDetailsPanel.add(firstTermValue);
    paymentDetailsPanel.add(secondTermLabel);
    paymentDetailsPanel.add(secondTermValue);
    paymentDetailsPanel.add(thirdTermLabel);
    paymentDetailsPanel.add(thirdTermValue);
    paymentDetailsPanel.add(selectTermLabel);
    paymentDetailsPanel.add(termComboBox);
    paymentDetailsPanel.add(paymentAmountLabel);
    paymentDetailsPanel.add(paymentAmountField);

    // Paying button
    JButton payButton = new JButton("Pay Now");
    styleNavButton(payButton);
    paymentDetailsPanel.add(new JLabel()); 
    paymentDetailsPanel.add(payButton);

    paymentPanel.add(paymentDetailsPanel, BorderLayout.CENTER);

    // Payment history section
    JPanel historyPanel = new JPanel();
    historyPanel.setLayout(new BoxLayout(historyPanel, BoxLayout.Y_AXIS));
    historyPanel.setBorder(BorderFactory.createTitledBorder("Payment History"));
    historyPanel.setBackground(Color.WHITE);

    try (Connection connection = DatabaseConnection.getConnection()) {
        PreparedStatement statement = connection.prepareStatement("SELECT amount, payment_date FROM payments WHERE student_id = ?");
        statement.setInt(1, loggedInUserId);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            JLabel historyLabel = new JLabel(
                "KES " + resultSet.getDouble("amount") + " - " + resultSet.getString("payment_date")
            );
            historyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            historyPanel.add(historyLabel);
        }

        resultSet.close();
        statement.close();
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load payment history.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    JScrollPane historyScrollPane = new JScrollPane(historyPanel);
    historyScrollPane.setPreferredSize(new Dimension(800, 200));
    paymentPanel.add(historyScrollPane, BorderLayout.SOUTH);

    mainPanel.add(paymentPanel, BorderLayout.CENTER);
    mainPanel.revalidate();
    mainPanel.repaint();

    // Paying button action
    payButton.addActionListener(e -> {
        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(paymentAmountField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (paymentAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Payment amount must be positive.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determining which term the user is paying for
        String selectedTerm = (String) termComboBox.getSelectedItem();
        double outstandingFee = 0;
        if ("First Term".equals(selectedTerm)) {
            outstandingFee = getOutstandingFee("first_term_fee");
        } else if ("Second Term".equals(selectedTerm)) {
            outstandingFee = getOutstandingFee("second_term_fee");
        } else if ("Third Term".equals(selectedTerm)) {
            outstandingFee = getOutstandingFee("third_term_fee");
        }

        if (outstandingFee == 0) {
            JOptionPane.showMessageDialog(this, "No outstanding fees for the selected term.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Checking if the user has enough fee to pay
        if (paymentAmount > outstandingFee) {
            JOptionPane.showMessageDialog(this, "Payment exceeds the outstanding fee. Please adjust the amount.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

  
        try (Connection connection = DatabaseConnection.getConnection()) {
            
            PreparedStatement updateFeesStatement = connection.prepareStatement(
                "UPDATE term_fees SET " + selectedTerm.toLowerCase().replace(" ", "_") + " = " + selectedTerm.toLowerCase().replace(" ", "_") + " - ? WHERE student_id = ?"
            );
            updateFeesStatement.setDouble(1, paymentAmount);
            updateFeesStatement.setInt(2, loggedInUserId);
            updateFeesStatement.executeUpdate();

        
            PreparedStatement insertPaymentStatement = connection.prepareStatement(
                "INSERT INTO payments (student_id, amount, payment_date) VALUES (?, ?, NOW())"
            );
            insertPaymentStatement.setInt(1, loggedInUserId);
            insertPaymentStatement.setDouble(2, paymentAmount);
            insertPaymentStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Payment successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            updateFeesStatement.close();
            insertPaymentStatement.close();

            // Refresh payment panel
            showPaymentPanel();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Payment failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });
}

private double getOutstandingFee(String termColumn) {
    double outstandingFee = 0;
    try (Connection connection = DatabaseConnection.getConnection()) {
        PreparedStatement statement = connection.prepareStatement(
            "SELECT " + termColumn + " FROM term_fees WHERE student_id = ?");
        statement.setInt(1, loggedInUserId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            outstandingFee = resultSet.getDouble(termColumn);
        }
        resultSet.close();
        statement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return outstandingFee;
}



    public static void main(String[] args) {
     
        int userId = 1;
        SwingUtilities.invokeLater(() -> {
            StudentFrame studentFrame = new StudentFrame(userId);
            studentFrame.setVisible(true);
        });
    }
}

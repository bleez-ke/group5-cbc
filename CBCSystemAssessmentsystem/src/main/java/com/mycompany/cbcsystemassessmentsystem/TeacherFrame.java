package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.io.*;
import org.json.*;
import java.net.*;

public class TeacherFrame extends JFrame {

    private static int loggedInUserId = -1;  
    private JPanel mainPanel;

    public TeacherFrame(int userId) {
        loggedInUserId = userId;  
        setTitle("Teacher Dashboard");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Navigation panel setup
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(4, 1, 10, 10)); 
        navPanel.setPreferredSize(new Dimension(200, 0));
        navPanel.setBackground(new Color(50, 50, 200));

        JButton profileButton = new JButton("Profile");
        JButton subjectAssignedButton = new JButton("Subject Assigned");
        JButton addAssessmentButton = new JButton("Add Assessment");
        JButton logoutButton = new JButton("Log Out"); 

        styleNavButton(profileButton);
        styleNavButton(subjectAssignedButton);
        styleNavButton(addAssessmentButton);

        navPanel.add(profileButton);
        navPanel.add(subjectAssignedButton);
        navPanel.add(addAssessmentButton);
        navPanel.add(logoutButton);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));  

        JLabel label = new JLabel("Welcome to the Teacher Dashboard", SwingConstants.CENTER);
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
        subjectAssignedButton.addActionListener(e -> showSubjectAssignedPanel());
        addAssessmentButton.addActionListener(e -> showAddAssessmentPanel());
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
            // Navigate back to LoginFrame
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose(); 
        }
    }

    private void showProfilePanel() {
        mainPanel.removeAll();

        JLabel profileIcon = new JLabel(new ImageIcon("/home/pc/Downloads/1077114.png"));  
        profileIcon.setHorizontalAlignment(SwingConstants.CENTER);

        // Profile info panel with no unnecessary bottom padding
        JPanel profileInfoPanel = new JPanel();
        profileInfoPanel.setLayout(new GridLayout(7, 2, 10, 20)); 
        profileInfoPanel.setBackground(new Color(255, 255, 255));  
        profileInfoPanel.setBorder(new EmptyBorder(10, 50, 50, 50)); 

        // Retrieve data and populate profile info
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
                addProfileLabel(profileInfoPanel, "Occupation:", resultSet.getString("occupation"));
                addProfileLabel(profileInfoPanel, "Title:", resultSet.getString("title"));
                addProfileLabel(profileInfoPanel, "Bio:", resultSet.getString("bio"));
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

        JLabel profileLabel = new JLabel("Teacher Profile", SwingConstants.CENTER);
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

    private void showSubjectAssignedPanel() {
        mainPanel.removeAll();

        JLabel subjectAssignedLabel = new JLabel("Subjects Assigned to You", SwingConstants.CENTER);
        subjectAssignedLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subjectAssignedLabel.setForeground(new Color(60, 80, 180));

        JPanel subjectListPanel = new JPanel();
        subjectListPanel.setLayout(new BoxLayout(subjectListPanel, BoxLayout.Y_AXIS));
        subjectListPanel.setBackground(new Color(245, 245, 245));  

        // Fetching subjects, day, and time assigned to the teacher from the database
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT subject, day, time FROM teacher_subject_assignments WHERE teacher_id = ?");
            statement.setInt(1, loggedInUserId);  
            ResultSet resultSet = statement.executeQuery();

            boolean foundSubjects = false;  
            while (resultSet.next()) {
                String subjectName = resultSet.getString("subject");
                String day = resultSet.getString("day");
                String time = resultSet.getString("time");

                // Creating a panel for each subject
                JPanel subjectPanel = new JPanel();
                subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));
                subjectPanel.setBackground(new Color(255, 255, 255));  
                subjectPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1)); 
                subjectPanel.setPreferredSize(new Dimension(350, 100));
                subjectPanel.setMaximumSize(new Dimension(350, 100));
                subjectPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                subjectPanel.setLayout(new BoxLayout(subjectPanel, BoxLayout.Y_AXIS));

                // Adding subject label
                JLabel subjectLabel = new JLabel("Subject: " + subjectName);
                subjectLabel.setFont(new Font("Arial", Font.BOLD, 16));
                subjectLabel.setForeground(new Color(0, 102, 204)); 
                subjectLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Adding day label
                JLabel dayLabel = new JLabel("Day: " + day);
                dayLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                dayLabel.setForeground(new Color(80, 80, 80)); 
                dayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Adding time label
                JLabel timeLabel = new JLabel("Time: " + time);
                timeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                timeLabel.setForeground(new Color(80, 80, 80)); 
                timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Adding labels to the panel
                subjectPanel.add(subjectLabel);
                subjectPanel.add(dayLabel);
                subjectPanel.add(timeLabel);

                // Adding the subject panel to the main subject list panel
                subjectListPanel.add(subjectPanel);  
                foundSubjects = true;
            }

            if (!foundSubjects) {
                JLabel noSubjectsLabel = new JLabel("No subjects assigned to you.");
                noSubjectsLabel.setFont(new Font("Arial", Font.ITALIC, 16));
                noSubjectsLabel.setForeground(new Color(150, 150, 150));
                subjectListPanel.add(noSubjectsLabel);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load subjects.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel subjectPanel = new JPanel();
        subjectPanel.setLayout(new BorderLayout());
        subjectPanel.add(subjectAssignedLabel, BorderLayout.NORTH);
        subjectPanel.add(subjectListPanel, BorderLayout.CENTER);

        mainPanel.add(subjectPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showAddAssessmentPanel() {
    // Clear the main panel and set up a new layout
    mainPanel.removeAll();

    // Title for Add Assessment section
    JLabel addAssessmentTitle = new JLabel("Generate Assessment", SwingConstants.CENTER);
    addAssessmentTitle.setFont(new Font("Arial", Font.BOLD, 24));
    addAssessmentTitle.setForeground(new Color(60, 80, 180));

    // Form to input assessment criteria
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new GridLayout(4, 2, 10, 20));
    formPanel.setBackground(new Color(255, 255, 255));
    formPanel.setBorder(new EmptyBorder(10, 50, 50, 50));

    // Labels and text fields for the form
    JLabel subjectLabel = new JLabel("Subject:");
    JTextField subjectField = new JTextField();
    JLabel numOfQuestionsLabel = new JLabel("Number of Questions:");
    JTextField numOfQuestionsField = new JTextField();
    JLabel difficultyLabel = new JLabel("Difficulty Level:");
    String[] difficultyOptions = {"Easy", "Medium", "Hard"};
    JComboBox<String> difficultyComboBox = new JComboBox<>(difficultyOptions);
    JLabel topicLabel = new JLabel("Topic/Focus Area:");
    JTextField topicField = new JTextField();

    formPanel.add(subjectLabel);
    formPanel.add(subjectField);
    formPanel.add(numOfQuestionsLabel);
    formPanel.add(numOfQuestionsField);
    formPanel.add(difficultyLabel);
    formPanel.add(difficultyComboBox);
    formPanel.add(topicLabel);
    formPanel.add(topicField);

    // Button to generate assessment
    JButton generateButton = new JButton("Generate Assessment");
    generateButton.setFont(new Font("Arial", Font.BOLD, 16));
    generateButton.setBackground(new Color(70, 130, 180));
    generateButton.setForeground(Color.WHITE);
    generateButton.setFocusPainted(false);
    generateButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Output panel for the generated questions
    JTextArea outputArea = new JTextArea();
    outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
    outputArea.setLineWrap(true);
    outputArea.setWrapStyleWord(true);
    outputArea.setEditable(false);
    JScrollPane outputScrollPane = new JScrollPane(outputArea);
    outputScrollPane.setPreferredSize(new Dimension(600, 300));

    // Adding the title and form panel to the main panel
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BorderLayout());
    contentPanel.setBackground(new Color(240, 248, 255));
    contentPanel.add(addAssessmentTitle, BorderLayout.NORTH);
    contentPanel.add(formPanel, BorderLayout.CENTER);
    contentPanel.add(generateButton, BorderLayout.SOUTH);

    mainPanel.add(contentPanel, BorderLayout.CENTER);
    mainPanel.add(outputScrollPane, BorderLayout.SOUTH);

    // Action listener for generating the assessment
    generateButton.addActionListener(e -> {
        String subject = subjectField.getText().trim();
        String numOfQuestions = numOfQuestionsField.getText().trim();
        String difficulty = (String) difficultyComboBox.getSelectedItem();
        String topic = topicField.getText().trim();

        if (subject.isEmpty() || numOfQuestions.isEmpty() || topic.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Convert the input to integer
            int numQuestions = Integer.parseInt(numOfQuestions);

            // Call the AI function to generate the assessment
            generateAssessment(subject, numQuestions, difficulty, topic, outputArea);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the number of questions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    mainPanel.revalidate();
    mainPanel.repaint();
}

// AI Integration: Using OpenAI API 
private void generateAssessment(String subject, int numQuestions, String difficulty, String topic, JTextArea outputArea) {
   
    String prompt = "Generate a " + difficulty + " assessment on the topic of " + topic + 
                    " for the subject " + subject + ". Include " + numQuestions + " questions.";

    // Call an external API for AI-based assessment generation
    String aiApiResponse = getAssessmentFromAI(prompt);

  
    outputArea.setText(aiApiResponse);
}


private String getAssessmentFromAI(String prompt) {
    try {
        // Fetch the API key securely from the environment variable
        String apiKey = System.getenv("AIzaSyDIvRPMNYpdyNB03O83CIEBE_WTX2pKuM4");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new Exception("API Key is missing");
        }

        // API URL for OpenAI 
        URL url = new URL("https://api.openai.com/v1/completions");

        // Setting up the connection with the appropriate headers
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);  
        connection.setDoOutput(true);

        // requesting body (JSON format)
        JSONObject body = new JSONObject();
        body.put("model", "text-davinci-003"); 
        body.put("prompt", prompt);
        body.put("max_tokens", 500);  

        // Writing request data to the output stream
        OutputStream os = connection.getOutputStream();
        os.write(body.toString().getBytes());
        os.flush();
        os.close();

        //response from the AI service
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //return the assessment text from the API response
        JSONObject responseJson = new JSONObject(response.toString());
        return responseJson.getJSONArray("choices").getJSONObject(0).getString("text").trim();

    } catch (Exception e) {
        e.printStackTrace();
        return "Error generating assessment.";
    }
  }
}

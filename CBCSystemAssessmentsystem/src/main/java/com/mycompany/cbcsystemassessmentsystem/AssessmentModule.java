package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JCalendar;
import java.time.LocalTime;
import javax.swing.SpinnerDateModel;
import java.util.Date;

public class AssessmentModule extends JFrame {

    private JTextField assessmentNameField;
    private JTextArea assessmentDescriptionArea;
    private JButton addButton, editButton, deleteButton;
    private JTable assessmentsTable;
    private DefaultTableModel tableModel;
    private JCalendar calendar;
    private JSpinner timeSpinner;

    public AssessmentModule() {
       
        setTitle("Assessment Module");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                clearFields();  
                dispose();  
            }
        });

       
        setLayout(new BorderLayout(20, 20));

       
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.setBackground(new Color(240, 240, 240));

        JLabel assessmentNameLabel = new JLabel("Assessment Name:");
        JLabel assessmentDescriptionLabel = new JLabel("Description:");
        JLabel assessmentDateLabel = new JLabel("Assessment Date:");
        JLabel assessmentTimeLabel = new JLabel("Assessment Time:");

        assessmentNameField = new JTextField(20);
        assessmentDescriptionArea = new JTextArea(5, 20);
        assessmentDescriptionArea.setLineWrap(true);
        assessmentDescriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(assessmentDescriptionArea);

        addButton = new JButton("Add Assessment");
        addButton.setBackground(new Color(53, 145, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        
        editButton = new JButton("Edit Assessment");
        editButton.setBackground(new Color(255, 165, 0));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);

        deleteButton = new JButton("Delete Assessment");
        deleteButton.setBackground(new Color(255, 69, 0));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(assessmentNameLabel, gbc);
        
        gbc.gridx = 1;
        formPanel.add(assessmentNameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(assessmentDescriptionLabel, gbc);

        gbc.gridx = 1;
        formPanel.add(descriptionScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(assessmentDateLabel, gbc);

        calendar = new JCalendar();
        gbc.gridx = 1;
        formPanel.add(calendar, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(assessmentTimeLabel, gbc);

        SpinnerDateModel model = new SpinnerDateModel();
        timeSpinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
        timeSpinner.setEditor(editor);
        gbc.gridx = 1;
        formPanel.add(timeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        gbc.gridy = 5;
        formPanel.add(editButton, gbc);

        
        String[] columnNames = {"Assessment Name", "Description", "Date", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        assessmentsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(assessmentsTable);

       
        add(formPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

       
        loadAssessments();

        
        addButton.addActionListener(e -> addAssessment());
        editButton.addActionListener(e -> editAssessment());
        deleteButton.addActionListener(e -> deleteAssessment());

        
        setVisible(true);
    }

    
    private void clearFields() {
        assessmentNameField.setText("");
        assessmentDescriptionArea.setText("");
    }

    // Method to load assessments from the database
    private void loadAssessments() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            String query = "SELECT * FROM assessments";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String name = rs.getString("assessmentName");
                String description = rs.getString("assessmentDescription");
                Date date = rs.getDate("assessmentDate");
                Time time = rs.getTime("assessmentTime");

                tableModel.addRow(new Object[]{name, description, date, time});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assessments", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAssessment() {
        String name = assessmentNameField.getText();
        String description = assessmentDescriptionArea.getText();
        Date date = calendar.getDate();
        LocalTime time = ((Date) timeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO assessments (assessmentName, assessmentDescription, assessmentDate, assessmentTime) VALUES (?, ?, ?, ?)")) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setTime(4, java.sql.Time.valueOf(time));

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Assessment added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                loadAssessments();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding assessment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding assessment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAssessment() {
        int selectedRow = assessmentsTable.getSelectedRow();

        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String description = (String) tableModel.getValueAt(selectedRow, 1);

            assessmentNameField.setText(name);
            assessmentDescriptionArea.setText(description);

            // Add button to Update
            addButton.setText("Update Assessment");
            addButton.removeActionListener(addButton.getActionListeners()[0]);
            addButton.addActionListener(e -> updateAssessment(selectedRow));
        } else {
            JOptionPane.showMessageDialog(this, "Please select an assessment to edit", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateAssessment(int selectedRow) {
        String name = assessmentNameField.getText();
        String description = assessmentDescriptionArea.getText();
        Date date = calendar.getDate();
        LocalTime time = ((Date) timeSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();

        if (name.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int assessmentId = selectedRow + 1;  
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE assessments SET assessmentName = ?, assessmentDescription = ?, assessmentDate = ?, assessmentTime = ? WHERE id = ?")) {

            ps.setString(1, name);
            ps.setString(2, description);
            ps.setDate(3, new java.sql.Date(date.getTime()));
            ps.setTime(4, java.sql.Time.valueOf(time));
            ps.setInt(5, assessmentId);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Assessment updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                loadAssessments();
                addButton.setText("Add Assessment");
                addButton.removeActionListener(addButton.getActionListeners()[0]);
                addButton.addActionListener(e -> addAssessment());
            } else {
                JOptionPane.showMessageDialog(this, "Error updating assessment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating assessment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAssessment() {
        int selectedRow = assessmentsTable.getSelectedRow();

        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);

            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete assessment: " + name + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                int assessmentId = selectedRow + 1;  

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM assessments WHERE id = ?")) {

                    ps.setInt(1, assessmentId);

                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Assessment deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        tableModel.setRowCount(0);
                        loadAssessments();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error deleting assessment", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting assessment", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an assessment to delete", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AssessmentModule::new);
    }
}

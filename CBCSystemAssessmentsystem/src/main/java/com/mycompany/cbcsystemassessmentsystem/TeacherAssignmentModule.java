package com.mycompany.cbcsystemassessmentsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class TeacherAssignmentModule extends JFrame {

    private JComboBox<String> teacherComboBox;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> timeComboBox;
    private JButton assignButton, editButton, deleteButton;
    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private Map<String, Integer> teacherIdMap;
    private AdminFrame adminFrame;

    public TeacherAssignmentModule() {
        setTitle("Teacher Assignment Module");
        setSize(800, 700);
        setLocationRelativeTo(null);

        teacherIdMap = new HashMap<>();
        setLayout(new BorderLayout(20, 20));

        // Panel for assignment form
        JPanel assignPanel = new JPanel();
        assignPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel teacherLabel = new JLabel("Select Teacher:");
        JLabel subjectLabel = new JLabel("Select Subject:");
        JLabel dayLabel = new JLabel("Select Day:");
        JLabel timeLabel = new JLabel("Select Time:");

        teacherComboBox = new JComboBox<>();
        loadTeachers();

        subjectComboBox = new JComboBox<>(new String[]{"Maths", "English", "Kiswahili"});

        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});

        timeComboBox = new JComboBox<>(new String[]{"08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM"});

        assignButton = new JButton("Assign Teacher");
        assignButton.addActionListener(e -> assignTeacherToSubject());

        editButton = new JButton("Edit Assignment");
        editButton.addActionListener(e -> editAssignment());

        deleteButton = new JButton("Delete Assignment");
        deleteButton.addActionListener(e -> deleteAssignment());

        // Add components to the form panel
        assignPanel.add(teacherLabel);
        assignPanel.add(teacherComboBox);
        assignPanel.add(subjectLabel);
        assignPanel.add(subjectComboBox);
        assignPanel.add(dayLabel);
        assignPanel.add(dayComboBox);
        assignPanel.add(timeLabel);
        assignPanel.add(timeComboBox);

        // Action panel with Edit and Delete buttons
        JPanel actionPanel = new JPanel();
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        // Add panels to the main layout
        add(assignPanel, BorderLayout.NORTH);
        add(actionPanel, BorderLayout.SOUTH);

        // Table to display assigned teachers
        String[] columnNames = {"Teacher Name", "Subject", "Day", "Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        teacherTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(teacherTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load the assigned teachers into the table
        loadAssignedTeachers();

        adminFrame = new AdminFrame();

        // Close behavior
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!adminFrame.isVisible()) {
                    adminFrame.setVisible(true);
                }
                dispose();
            }
        });

        setVisible(true);
    }

    private void loadTeachers() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String query = "SELECT id, firstName, lastName FROM users WHERE role = 'teacher'";
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int teacherId = rs.getInt("id");
                String teacherName = rs.getString("firstName") + " " + rs.getString("lastName");
                teacherComboBox.addItem(teacherName);
                teacherIdMap.put(teacherName, teacherId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading teachers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAssignedTeachers() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String query = "SELECT t.firstName, t.lastName, tsa.subject, tsa.day, tsa.time " +
                           "FROM users t " +
                           "JOIN teacher_subject_assignments tsa ON t.id = tsa.teacher_id " +
                           "WHERE t.role = 'teacher'";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String teacherName = rs.getString("firstName") + " " + rs.getString("lastName");
                String subject = rs.getString("subject");
                String day = rs.getString("day");
                String time = rs.getString("time");
                tableModel.addRow(new Object[]{teacherName, subject, day, time});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading assigned teachers", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void assignTeacherToSubject() {
        String selectedTeacher = (String) teacherComboBox.getSelectedItem();
        String selectedSubject = (String) subjectComboBox.getSelectedItem();
        String selectedDay = (String) dayComboBox.getSelectedItem();
        String selectedTime = (String) timeComboBox.getSelectedItem();

        if (selectedTeacher != null && selectedSubject != null && selectedDay != null && selectedTime != null) {
            int teacherId = teacherIdMap.get(selectedTeacher);
            assignTeacherInDatabase(teacherId, selectedSubject, selectedDay, selectedTime);
        } else {
            JOptionPane.showMessageDialog(this, "Please select teacher, subject, day, and time", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void assignTeacherInDatabase(int teacherId, String subject, String day, String time) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO teacher_subject_assignments (teacher_id, subject, day, time) VALUES (?, ?, ?, ?)")) {

            ps.setInt(1, teacherId);
            ps.setString(2, subject);
            ps.setString(3, day);
            ps.setString(4, time);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Teacher assigned successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                loadAssignedTeachers();
            } else {
                JOptionPane.showMessageDialog(this, "Error assigning teacher to subject", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error assigning teacher to subject", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editAssignment() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow != -1) {
            String teacherName = (String) teacherTable.getValueAt(selectedRow, 0);
            String subject = (String) teacherTable.getValueAt(selectedRow, 1);
            String day = (String) teacherTable.getValueAt(selectedRow, 2);
            String time = (String) teacherTable.getValueAt(selectedRow, 3);

            int teacherId = teacherIdMap.get(teacherName);

            String newSubject = (String) subjectComboBox.getSelectedItem();
            String newDay = (String) dayComboBox.getSelectedItem();
            String newTime = (String) timeComboBox.getSelectedItem();
            if (newSubject != null && newDay != null && newTime != null) {
                updateAssignmentInDatabase(teacherId, subject, day, time, newSubject, newDay, newTime);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to edit", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateAssignmentInDatabase(int teacherId, String oldSubject, String oldDay, String oldTime, String newSubject, String newDay, String newTime) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE teacher_subject_assignments SET subject = ?, day = ?, time = ? WHERE teacher_id = ? AND subject = ? AND day = ? AND time = ?")) {

            ps.setString(1, newSubject);
            ps.setString(2, newDay);
            ps.setString(3, newTime);
            ps.setInt(4, teacherId);
            ps.setString(5, oldSubject);
            ps.setString(6, oldDay);
            ps.setString(7, oldTime);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Assignment updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                loadAssignedTeachers();
            } else {
                JOptionPane.showMessageDialog(this, "Error updating assignment", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating assignment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAssignment() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow != -1) {
            String teacherName = (String) teacherTable.getValueAt(selectedRow, 0);
            String subject = (String) teacherTable.getValueAt(selectedRow, 1);
            String day = (String) teacherTable.getValueAt(selectedRow, 2);
            String time = (String) teacherTable.getValueAt(selectedRow, 3);

            int teacherId = teacherIdMap.get(teacherName);
            deleteAssignmentFromDatabase(teacherId, subject, day, time);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row to delete", "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteAssignmentFromDatabase(int teacherId, String subject, String day, String time) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM teacher_subject_assignments WHERE teacher_id = ? AND subject = ? AND day = ? AND time = ?")) {

            ps.setInt(1, teacherId);
            ps.setString(2, subject);
            ps.setString(3, day);
            ps.setString(4, time);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Assignment deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                tableModel.setRowCount(0);
                loadAssignedTeachers();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting assignment", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting assignment", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

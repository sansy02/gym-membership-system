package gym.gui;

import gym.MainFrame;
import gym.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AttendancePanel extends JPanel {

    private MainFrame mainFrame;

    private JComboBox<String> memberComboBox;

    private JButton checkInButton;
    private JButton refreshButton;
    private JButton backButton;

    private DefaultTableModel tableModel;
    private JTable attendanceTable;

    public AttendancePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));

        JLabel titleLabel = new JLabel("Check-in / Attendance");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        backButton = new JButton("Back to Dashboard");
        backButton.setFont(new Font("Arial", Font.PLAIN, 13));
        backButton.setBackground(new Color(108, 117, 125));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> mainFrame.showPanel(MainFrame.DASHBOARD));

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(backButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        inputPanel.setPreferredSize(new Dimension(320, 250));

        JLabel formTitle = new JLabel("Member Check-in");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(formTitle);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel instructionLabel = new JLabel(
                "<html>Select a member and click <b>Check In</b> to record<br>"
                        + "their gym attendance for today.</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(108, 117, 125));
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(instructionLabel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel memberLabel = new JLabel("Select Member:");
        memberLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        memberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(memberLabel);

        memberComboBox = new JComboBox<>();
        memberComboBox.setMaximumSize(new Dimension(280, 32));
        memberComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        memberComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadMembersIntoComboBox();
        inputPanel.add(memberComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setBackground(Color.WHITE);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        checkInButton = new JButton("Check In");
        checkInButton.setFont(new Font("Arial", Font.BOLD, 14));
        checkInButton.setBackground(new Color(255, 193, 7));
        checkInButton.setForeground(new Color(33, 37, 41));
        checkInButton.setFocusPainted(false);
        checkInButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkInButton.addActionListener(e -> performCheckIn());

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadAttendance());

        buttonRow.add(checkInButton);
        buttonRow.add(refreshButton);
        inputPanel.add(buttonRow);

        add(inputPanel, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel("Check-in History");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Member Name", "Check-in Time"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        attendanceTable = new JTable(tableModel);
        attendanceTable.setRowHeight(28);
        attendanceTable.setFont(new Font("Arial", Font.PLAIN, 13));
        attendanceTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        attendanceTable.getTableHeader().setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        loadAttendance();
    }

    private void loadMembersIntoComboBox() {
        memberComboBox.removeAllItems();
        String sql = "SELECT member_id, name FROM members ORDER BY member_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String display = rs.getInt("member_id") + " - " + rs.getString("name");
                memberComboBox.addItem(display);
            }
        } catch (SQLException ex) {
            System.err.println("Error loading members: " + ex.getMessage());
        }
    }

    private int getSelectedMemberId() {
        String selected = (String) memberComboBox.getSelectedItem();
        if (selected != null && selected.contains(" - ")) {
            return Integer.parseInt(selected.split(" - ")[0]);
        }
        return -1;
    }

    private void performCheckIn() {
        int memberId = getSelectedMemberId();
        if (memberId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO attendance (member_id, check_in_time) VALUES (?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Check-in recorded successfully!\nTime: " + new java.util.Date(),
                    "Check-in Success", JOptionPane.INFORMATION_MESSAGE);
            loadAttendance();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error recording check-in: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadAttendance() {
        tableModel.setRowCount(0);
        String sql = "SELECT a.attendance_id, m.name, a.check_in_time "
                + "FROM attendance a "
                + "JOIN members m ON a.member_id = m.member_id "
                + "ORDER BY a.check_in_time DESC "
                + "LIMIT 200";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("attendance_id"),
                        rs.getString("name"),
                        rs.getTimestamp("check_in_time")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading attendance: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

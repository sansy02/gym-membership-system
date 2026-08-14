package gym.gui;

import gym.MainFrame;
import gym.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MemberPanel extends JPanel {

    private MainFrame mainFrame;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> planComboBox;

    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton backButton;

    private DefaultTableModel tableModel;
    private JTable memberTable;

    private int selectedMemberId = -1;

    public MemberPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));

        JLabel titleLabel = new JLabel("Member Management");
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
        inputPanel.setPreferredSize(new Dimension(320, 350));

        JLabel formTitle = new JLabel("Member Information");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(formTitle);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        addInputField(inputPanel, "Name:");
        nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(280, 32));
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(nameField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addInputField(inputPanel, "Email:");
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(280, 32));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(emailField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addInputField(inputPanel, "Phone:");
        phoneField = new JTextField();
        phoneField.setMaximumSize(new Dimension(280, 32));
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(phoneField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        addInputField(inputPanel, "Membership Plan:");
        planComboBox = new JComboBox<>();
        planComboBox.setMaximumSize(new Dimension(280, 32));
        planComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        planComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        loadPlansIntoComboBox();
        inputPanel.add(planComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setBackground(Color.WHITE);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        addButton = createButton("Add", new Color(40, 167, 69));
        updateButton = createButton("Update", new Color(0, 123, 255));
        deleteButton = createButton("Delete", new Color(220, 53, 69));
        clearButton = createButton("Clear", new Color(108, 117, 125));

        addButton.addActionListener(e -> addMember());
        updateButton.addActionListener(e -> updateMember());
        deleteButton.addActionListener(e -> deleteMember());
        clearButton.addActionListener(e -> clearFields());

        buttonRow.add(addButton);
        buttonRow.add(updateButton);
        buttonRow.add(deleteButton);
        buttonRow.add(clearButton);
        inputPanel.add(buttonRow);

        add(inputPanel, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel("Member List");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Email", "Phone", "Plan", "Join Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(28);
        memberTable.setFont(new Font("Arial", Font.PLAIN, 13));
        memberTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        memberTable.getTableHeader().setBackground(new Color(248, 249, 250));
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        memberTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = memberTable.getSelectedRow();
                if (row >= 0) {
                    selectedMemberId = (int) tableModel.getValueAt(row, 0);
                    nameField.setText((String) tableModel.getValueAt(row, 1));
                    emailField.setText((String) tableModel.getValueAt(row, 2));
                    phoneField.setText((String) tableModel.getValueAt(row, 3));
                    String planName = (String) tableModel.getValueAt(row, 4);
                    planComboBox.setSelectedItem(planName);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(memberTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        loadMembers();
    }

    private void addInputField(JPanel panel, String label) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(new Color(73, 80, 87));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadPlansIntoComboBox() {
        planComboBox.removeAllItems();
        String sql = "SELECT plan_name FROM membership_plans ORDER BY plan_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                planComboBox.addItem(rs.getString("plan_name"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading plans: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getPlanIdByName(String planName) {
        String sql = "SELECT plan_id FROM membership_plans WHERE plan_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, planName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("plan_id");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting plan ID: " + ex.getMessage());
        }
        return 1;
    }

    private void addMember() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String planName = (String) planComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = getPlanIdByName(planName);
        String sql = "INSERT INTO members (name, email, phone, plan_id, join_date) VALUES (?, ?, ?, ?, CURDATE())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, planId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Member added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding member: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String planName = (String) planComboBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int planId = getPlanIdByName(planName);
        String sql = "UPDATE members SET name=?, email=?, phone=?, plan_id=? WHERE member_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, planId);
            stmt.setInt(5, selectedMemberId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Member updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            selectedMemberId = -1;
            loadMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating member: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMember() {
        if (selectedMemberId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this member?\n"
                        + "This will also delete their payments and attendance records.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement stmt1 = conn.prepareStatement(
                    "DELETE FROM attendance WHERE member_id=?")) {
                stmt1.setInt(1, selectedMemberId);
                stmt1.executeUpdate();
            }
            try (PreparedStatement stmt2 = conn.prepareStatement(
                    "DELETE FROM payments WHERE member_id=?")) {
                stmt2.setInt(1, selectedMemberId);
                stmt2.executeUpdate();
            }
            try (PreparedStatement stmt3 = conn.prepareStatement(
                    "DELETE FROM members WHERE member_id=?")) {
                stmt3.setInt(1, selectedMemberId);
                stmt3.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Member deleted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            selectedMemberId = -1;
            loadMembers();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error deleting member: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadMembers() {
        tableModel.setRowCount(0);
        String sql = "SELECT m.member_id, m.name, m.email, m.phone, "
                + "p.plan_name, m.join_date "
                + "FROM members m "
                + "JOIN membership_plans p ON m.plan_id = p.plan_id "
                + "ORDER BY m.member_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("member_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("plan_name"),
                        rs.getDate("join_date")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading members: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        if (planComboBox.getItemCount() > 0) {
            planComboBox.setSelectedIndex(0);
        }
        selectedMemberId = -1;
        memberTable.clearSelection();
        nameField.requestFocus();
    }
}

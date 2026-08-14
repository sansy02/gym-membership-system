package gym.gui;

import gym.MainFrame;
import gym.database.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class PaymentPanel extends JPanel {

    private MainFrame mainFrame;

    private JComboBox<String> memberComboBox;
    private JTextField amountField;
    private JComboBox<String> methodComboBox;

    private JButton addButton;
    private JButton refreshButton;
    private JButton backButton;

    private DefaultTableModel tableModel;
    private JTable paymentTable;

    public PaymentPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(240, 242, 245));

        JLabel titleLabel = new JLabel("Payment Management");
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
        inputPanel.setPreferredSize(new Dimension(320, 300));

        JLabel formTitle = new JLabel("Record New Payment");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(formTitle);
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
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel amountLabel = new JLabel("Amount ($):");
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(amountLabel);

        amountField = new JTextField();
        amountField.setMaximumSize(new Dimension(280, 32));
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(amountField);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(methodLabel);

        methodComboBox = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Online Transfer"});
        methodComboBox.setMaximumSize(new Dimension(280, 32));
        methodComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        methodComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        inputPanel.add(methodComboBox);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttonRow.setBackground(Color.WHITE);
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        addButton = new JButton("Record Payment");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setBackground(new Color(40, 167, 69));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addPayment());

        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> loadPayments());

        buttonRow.add(addButton);
        buttonRow.add(refreshButton);
        inputPanel.add(buttonRow);

        add(inputPanel, BorderLayout.WEST);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel tableTitle = new JLabel("Payment History");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 14));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"ID", "Member Name", "Amount", "Date", "Method"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(28);
        paymentTable.setFont(new Font("Arial", Font.PLAIN, 13));
        paymentTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        paymentTable.getTableHeader().setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        loadPayments();
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

    private void addPayment() {
        int memberId = getSelectedMemberId();
        if (memberId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a member.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String amountText = amountField.getText().trim();
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an amount.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid positive amount.",
                    "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String method = (String) methodComboBox.getSelectedItem();
        String sql = "INSERT INTO payments (member_id, amount, payment_date, payment_method) VALUES (?, ?, CURDATE(), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setDouble(2, amount);
            stmt.setString(3, method);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Payment recorded successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            amountField.setText("");
            loadPayments();
            loadMembersIntoComboBox();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error recording payment: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadPayments() {
        tableModel.setRowCount(0);
        String sql = "SELECT p.payment_id, m.name, p.amount, "
                + "p.payment_date, p.payment_method "
                + "FROM payments p "
                + "JOIN members m ON p.member_id = m.member_id "
                + "ORDER BY p.payment_date DESC, p.payment_id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("payment_id"),
                        rs.getString("name"),
                        "$" + String.format("%.2f", rs.getDouble("amount")),
                        rs.getDate("payment_date"),
                        rs.getString("payment_method")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading payments: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

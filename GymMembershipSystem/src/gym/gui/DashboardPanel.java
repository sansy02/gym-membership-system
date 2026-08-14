package gym.gui;

import gym.MainFrame;
import gym.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DashboardPanel extends JPanel {

    private MainFrame mainFrame;

    private JLabel totalMembersLabel;
    private JLabel totalPaymentsLabel;
    private JLabel todayCheckinsLabel;

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 37, 41));
        headerPanel.setPreferredSize(new Dimension(800, 80));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        JLabel headerTitle = new JLabel("Dashboard");
        headerTitle.setFont(new Font("Arial", Font.BOLD, 24));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> mainFrame.showPanel(MainFrame.LOGIN));
        headerPanel.add(logoutButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBackground(new Color(240, 242, 245));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        totalMembersLabel = createStatCard(statsPanel, "Total Members", "0",
                new Color(40, 167, 69));
        totalPaymentsLabel = createStatCard(statsPanel, "Total Payments",
                "$0", new Color(0, 123, 255));
        todayCheckinsLabel = createStatCard(statsPanel, "Today's Check-ins",
                "0", new Color(255, 193, 7));

        add(statsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 60, 40, 60));

        addNavButton(buttonPanel, "Member Management",
                "Register, update, and manage gym members",
                new Color(40, 167, 69), MainFrame.MEMBER);

        addNavButton(buttonPanel, "Payment Management",
                "Record payments and view payment history",
                new Color(0, 123, 255), MainFrame.PAYMENT);

        addNavButton(buttonPanel, "Check-in / Attendance",
                "Record member check-ins and view attendance",
                new Color(255, 193, 7), MainFrame.ATTENDANCE);

        addNavButton(buttonPanel, "Back to Login",
                "Return to the login screen",
                new Color(108, 117, 125), MainFrame.LOGIN);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createStatCard(JPanel parent, String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(108, 117, 125));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(new Color(33, 37, 41));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(valueLabel);

        parent.add(card);
        return valueLabel;
    }

    private void addNavButton(JPanel parent, String title, String description,
                              Color color, String targetPanel) {
        JPanel btnPanel = new JPanel(new BorderLayout(10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(4, 50));
        btnPanel.add(colorBar, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(new Color(33, 37, 41));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(new Color(108, 117, 125));

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        textPanel.add(descLabel);
        btnPanel.add(textPanel, BorderLayout.CENTER);

        btnPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mainFrame.showPanel(targetPanel);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnPanel.setBackground(new Color(248, 249, 250));
                textPanel.setBackground(new Color(248, 249, 250));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnPanel.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
            }
        });

        parent.add(btnPanel);
    }

    public void refreshStats() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM members");
            if (rs.next()) {
                totalMembersLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

            rs = stmt.executeQuery("SELECT COALESCE(SUM(amount), 0) FROM payments");
            if (rs.next()) {
                totalPaymentsLabel.setText("$" + String.format("%.0f", rs.getDouble(1)));
            }
            rs.close();

            rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM attendance WHERE DATE(check_in_time) = CURDATE()");
            if (rs.next()) {
                todayCheckinsLabel.setText(String.valueOf(rs.getInt(1)));
            }
            rs.close();

        } catch (SQLException ex) {
            System.err.println("Error refreshing dashboard stats: " + ex.getMessage());
        }
    }
}

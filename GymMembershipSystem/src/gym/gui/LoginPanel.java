package gym.gui;

import gym.MainFrame;
import gym.database.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPanel extends JPanel {

    private MainFrame mainFrame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setupUI();
    }

    private void setupUI() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240, 242, 245));

        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBackground(Color.WHITE);
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 50, 30, 50)
        ));
        loginBox.setMaximumSize(new Dimension(380, 350));
        loginBox.setPreferredSize(new Dimension(380, 350));

        JLabel titleLabel = new JLabel("Gym Membership System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Staff Login");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(titleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 5)));
        loginBox.add(subtitleLabel);
        loginBox.add(Box.createRigidArea(new Dimension(0, 30)));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBox.add(userLabel);

        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(280, 35));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBox.add(usernameField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBox.add(passLabel);

        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(280, 35));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBox.add(passwordField);
        loginBox.add(Box.createRigidArea(new Dimension(0, 25)));

        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(280, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        loginBox.add(loginButton);

        add(loginBox);

        passwordField.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM staff WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String staffName = rs.getString("name");
                String role = rs.getString("role");
                JOptionPane.showMessageDialog(this,
                        "Welcome, " + staffName + " (" + role + ")!",
                        "Login Successful", JOptionPane.INFORMATION_MESSAGE);

                usernameField.setText("");
                passwordField.setText("");

                mainFrame.showPanel(MainFrame.DASHBOARD);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password. Please try again.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

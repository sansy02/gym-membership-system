package gym;

import gym.database.DBConnection;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set system look and feel.");
        }

        System.out.println("Testing database connection...");
        if (DBConnection.testConnection()) {
            System.out.println("Database connection successful!");
        } else {
            System.out.println("WARNING: Database connection failed.");
            System.out.println("Please ensure MySQL is running and the gym_db database exists.");
            System.out.println("Run sql/setup.sql to create the database.");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

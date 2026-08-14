package gym;

import gym.database.DBConnection;
import gym.server.MiniHttpServer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        if (DBConnection.testConnection()) {
            System.out.println("Database connection successful!\n");
        } else {
            System.out.println("WARNING: Database connection failed.");
            System.out.println("The web page will open, but data operations will not work.");
            System.out.println("Please ensure MySQL is running and gym_db exists (run sql/setup.sql).\n");
        }

        Path webRoot = findWebRoot();

        try {
            MiniHttpServer server = new MiniHttpServer(PORT, webRoot);
            server.start();
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Path findWebRoot() {
        Path[] candidates = {
                Paths.get("web"),
                Paths.get("..", "web"),
                Paths.get("GymWebSystem", "web")
        };
        for (Path p : candidates) {
            if (Files.exists(p) && Files.isDirectory(p)) {
                System.out.println("Web root found: " + p.toAbsolutePath());
                return p.toAbsolutePath();
            }
        }
        System.out.println("WARNING: web folder not found, using current directory.");
        return Paths.get(".").toAbsolutePath();
    }
}

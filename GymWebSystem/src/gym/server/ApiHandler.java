package gym.server;

import gym.database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

public class ApiHandler {

    public String handle(String method, String path,
                         Map<String, String> queryParams, String body) {
        try {
            switch (path) {
                case "/api/login":
                    return handleLogin(body);
                case "/api/stats":
                    return handleStats();
                case "/api/plans":
                    return handleGetPlans();
                case "/api/members":
                    return handleMembers(method, queryParams, body);
                case "/api/payments":
                    return handlePayments(method, body);
                case "/api/attendance":
                    return handleAttendance(method, body);
                default:
                    return error("Unknown API endpoint: " + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return error("Server error: " + e.getMessage());
        }
    }

    private String handleLogin(String body) throws Exception {
        Map<String, Object> req = JsonUtils.parseObject(body);
        String username = JsonUtils.getString(req, "username", "");
        String password = JsonUtils.getString(req, "password", "");

        if (username.isEmpty() || password.isEmpty()) {
            return error("Username and password are required");
        }

        String sql = "SELECT name, role FROM staff WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return JsonUtils.buildObject(
                        "success", "true",
                        "name", "\"" + JsonUtils.escape(rs.getString("name")) + "\"",
                        "role", "\"" + JsonUtils.escape(rs.getString("role")) + "\"");
            }
            return JsonUtils.buildObject(
                    "success", "false",
                    "message", "\"Invalid username or password\"");
        }
    }

    private String handleStats() throws Exception {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            int totalMembers = 0;
            double totalPayments = 0;
            int todayCheckins = 0;

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM members");
            if (rs.next()) {
                totalMembers = rs.getInt(1);
            }
            rs.close();

            rs = stmt.executeQuery("SELECT COALESCE(SUM(amount), 0) FROM payments");
            if (rs.next()) {
                totalPayments = rs.getDouble(1);
            }
            rs.close();

            rs = stmt.executeQuery(
                    "SELECT COUNT(*) FROM attendance WHERE DATE(check_in_time) = CURDATE()");
            if (rs.next()) {
                todayCheckins = rs.getInt(1);
            }
            rs.close();

            return JsonUtils.buildObject(
                    "totalMembers", String.valueOf(totalMembers),
                    "totalPayments", String.format("%.2f", totalPayments),
                    "todayCheckins", String.valueOf(todayCheckins));
        }
    }

    private String handleGetPlans() throws Exception {
        String sql = "SELECT plan_id, plan_name, price, duration_months, description "
                + "FROM membership_plans ORDER BY plan_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return JsonUtils.resultSetToJson(rs);
        }
    }

    private String handleMembers(String method, Map<String, String> queryParams,
                                 String body) throws Exception {
        switch (method) {
            case "GET":
                return handleGetMembers();
            case "POST":
                return handleAddMember(body);
            case "PUT":
                return handleUpdateMember(body);
            case "DELETE":
                return handleDeleteMember(queryParams);
            default:
                return error("Unsupported method: " + method);
        }
    }

    private String handleGetMembers() throws Exception {
        String sql = "SELECT m.member_id, m.name, m.email, m.phone, "
                + "m.plan_id, p.plan_name, m.join_date "
                + "FROM members m "
                + "JOIN membership_plans p ON m.plan_id = p.plan_id "
                + "ORDER BY m.member_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return JsonUtils.resultSetToJson(rs);
        }
    }

    private String handleAddMember(String body) throws Exception {
        Map<String, Object> req = JsonUtils.parseObject(body);
        String name = JsonUtils.getString(req, "name", "");
        String email = JsonUtils.getString(req, "email", "");
        String phone = JsonUtils.getString(req, "phone", "");
        int planId = JsonUtils.getInt(req, "plan_id", 1);

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            return error("Name, email and phone are required");
        }

        String sql = "INSERT INTO members (name, email, phone, plan_id, join_date) "
                + "VALUES (?, ?, ?, ?, CURDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, planId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return success("Member added successfully");
            }
            return error("Failed to add member");
        }
    }

    private String handleUpdateMember(String body) throws Exception {
        Map<String, Object> req = JsonUtils.parseObject(body);
        int memberId = JsonUtils.getInt(req, "member_id", -1);
        String name = JsonUtils.getString(req, "name", "");
        String email = JsonUtils.getString(req, "email", "");
        String phone = JsonUtils.getString(req, "phone", "");
        int planId = JsonUtils.getInt(req, "plan_id", 1);

        if (memberId <= 0) {
            return error("Invalid member ID");
        }
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            return error("Name, email and phone are required");
        }

        String sql = "UPDATE members SET name=?, email=?, phone=?, plan_id=? "
                + "WHERE member_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, planId);
            stmt.setInt(5, memberId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return success("Member updated successfully");
            }
            return error("Member not found");
        }
    }

    private String handleDeleteMember(Map<String, String> queryParams) throws Exception {
        int memberId;
        try {
            memberId = Integer.parseInt(queryParams.getOrDefault("member_id", "-1"));
        } catch (NumberFormatException e) {
            return error("Invalid member ID");
        }
        if (memberId <= 0) {
            return error("Invalid member ID");
        }

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement s1 = conn.prepareStatement(
                    "DELETE FROM attendance WHERE member_id=?")) {
                s1.setInt(1, memberId);
                s1.executeUpdate();
            }
            try (PreparedStatement s2 = conn.prepareStatement(
                    "DELETE FROM payments WHERE member_id=?")) {
                s2.setInt(1, memberId);
                s2.executeUpdate();
            }
            try (PreparedStatement s3 = conn.prepareStatement(
                    "DELETE FROM members WHERE member_id=?")) {
                s3.setInt(1, memberId);
                int rows = s3.executeUpdate();
                if (rows > 0) {
                    return success("Member deleted successfully");
                }
                return error("Member not found");
            }
        }
    }

    private String handlePayments(String method, String body) throws Exception {
        switch (method) {
            case "GET":
                return handleGetPayments();
            case "POST":
                return handleAddPayment(body);
            default:
                return error("Unsupported method: " + method);
        }
    }

    private String handleGetPayments() throws Exception {
        String sql = "SELECT p.payment_id, p.member_id, m.name AS member_name, "
                + "p.amount, p.payment_date, p.payment_method "
                + "FROM payments p "
                + "JOIN members m ON p.member_id = m.member_id "
                + "ORDER BY p.payment_date DESC, p.payment_id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return JsonUtils.resultSetToJson(rs);
        }
    }

    private String handleAddPayment(String body) throws Exception {
        Map<String, Object> req = JsonUtils.parseObject(body);
        int memberId = JsonUtils.getInt(req, "member_id", -1);
        double amount = JsonUtils.getDouble(req, "amount", -1);
        String method = JsonUtils.getString(req, "payment_method", "Cash");

        if (memberId <= 0) {
            return error("Please select a member");
        }
        if (amount <= 0) {
            return error("Amount must be positive");
        }

        String sql = "INSERT INTO payments (member_id, amount, payment_date, payment_method) "
                + "VALUES (?, ?, CURDATE(), ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.setDouble(2, amount);
            stmt.setString(3, method);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return success("Payment recorded successfully");
            }
            return error("Failed to record payment");
        }
    }

    private String handleAttendance(String method, String body) throws Exception {
        switch (method) {
            case "GET":
                return handleGetAttendance();
            case "POST":
                return handleAddAttendance(body);
            default:
                return error("Unsupported method: " + method);
        }
    }

    private String handleGetAttendance() throws Exception {
        String sql = "SELECT a.attendance_id, a.member_id, m.name AS member_name, "
                + "a.check_in_time "
                + "FROM attendance a "
                + "JOIN members m ON a.member_id = m.member_id "
                + "ORDER BY a.check_in_time DESC "
                + "LIMIT 200";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return JsonUtils.resultSetToJson(rs);
        }
    }

    private String handleAddAttendance(String body) throws Exception {
        Map<String, Object> req = JsonUtils.parseObject(body);
        int memberId = JsonUtils.getInt(req, "member_id", -1);

        if (memberId <= 0) {
            return error("Please select a member");
        }

        String sql = "INSERT INTO attendance (member_id, check_in_time) VALUES (?, NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                return success("Check-in recorded successfully");
            }
            return error("Failed to record check-in");
        }
    }

    private String success(String message) {
        return JsonUtils.buildObject(
                "success", "true",
                "message", "\"" + JsonUtils.escape(message) + "\"");
    }

    private String error(String message) {
        return JsonUtils.buildObject(
                "success", "false",
                "message", "\"" + JsonUtils.escape(message) + "\"");
    }
}

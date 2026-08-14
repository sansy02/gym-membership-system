package gym.model;

import java.time.LocalDateTime;

public class Attendance {

    private int attendanceId;
    private int memberId;
    private LocalDateTime checkInTime;

    public Attendance() {
        this.checkInTime = LocalDateTime.now();
    }

    public Attendance(int attendanceId, int memberId, LocalDateTime checkInTime) {
        this.attendanceId = attendanceId;
        this.memberId = memberId;
        this.checkInTime = checkInTime;
    }

    public Attendance(int memberId) {
        this.memberId = memberId;
        this.checkInTime = LocalDateTime.now();
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public void displayInfo() {
        System.out.println("=== Attendance Info ===");
        System.out.println("Attendance ID: " + attendanceId);
        System.out.println("Member ID: " + memberId);
        System.out.println("Check-in Time: " + checkInTime);
    }
}

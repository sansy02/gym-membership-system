package gym;

import gym.gui.*;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static final String LOGIN = "LOGIN";
    public static final String DASHBOARD = "DASHBOARD";
    public static final String MEMBER = "MEMBER";
    public static final String PAYMENT = "PAYMENT";
    public static final String ATTENDANCE = "ATTENDANCE";

    private LoginPanel loginPanel;
    private DashboardPanel dashboardPanel;
    private MemberPanel memberPanel;
    private PaymentPanel paymentPanel;
    private AttendancePanel attendancePanel;

    public MainFrame() {
        setTitle("Gym Membership Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new LoginPanel(this);
        dashboardPanel = new DashboardPanel(this);
        memberPanel = new MemberPanel(this);
        paymentPanel = new PaymentPanel(this);
        attendancePanel = new AttendancePanel(this);

        mainPanel.add(loginPanel, LOGIN);
        mainPanel.add(dashboardPanel, DASHBOARD);
        mainPanel.add(memberPanel, MEMBER);
        mainPanel.add(paymentPanel, PAYMENT);
        mainPanel.add(attendancePanel, ATTENDANCE);

        add(mainPanel);

        showPanel(LOGIN);
    }

    public void showPanel(String panelName) {
        if (panelName.equals(DASHBOARD)) {
            dashboardPanel.refreshStats();
        }
        if (panelName.equals(MEMBER)) {
            memberPanel.loadMembers();
        }
        if (panelName.equals(PAYMENT)) {
            paymentPanel.loadPayments();
        }
        if (panelName.equals(ATTENDANCE)) {
            attendancePanel.loadAttendance();
        }
        cardLayout.show(mainPanel, panelName);
    }
}

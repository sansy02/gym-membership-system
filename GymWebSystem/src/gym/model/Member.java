package gym.model;

import java.time.LocalDate;

public class Member extends Person implements IPayable {

    private MembershipPlan plan;
    private LocalDate joinDate;

    public Member() {
        super();
        this.joinDate = LocalDate.now();
    }

    public Member(int id, String name, String email, String phone,
                  MembershipPlan plan, LocalDate joinDate) {
        super(id, name, email, phone);
        this.plan = plan;
        this.joinDate = joinDate;
    }

    public Member(String name, String email, String phone,
                  MembershipPlan plan) {
        super(name, email, phone);
        this.plan = plan;
        this.joinDate = LocalDate.now();
    }

    public MembershipPlan getPlan() {
        return plan;
    }

    public void setPlan(MembershipPlan plan) {
        this.plan = plan;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    @Override
    public String getRole() {
        return "Member";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Member Info ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Join Date: " + joinDate);
        if (plan != null) {
            System.out.println("Plan: " + plan.getPlanDetails());
        }
    }

    @Override
    public double calculatePayment() {
        if (plan != null) {
            return plan.getPrice();
        }
        return 0.0;
    }

    @Override
    public String getPaymentDescription() {
        if (plan != null) {
            return "Membership fee for " + getName() + " - " + plan.getPlanDetails();
        }
        return "Membership fee for " + getName() + " - No plan assigned";
    }
}

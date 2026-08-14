package gym.model;

import java.time.LocalDate;

public class Payment {

    private int paymentId;
    private int memberId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Payment() {
        this.paymentDate = LocalDate.now();
    }

    public Payment(int paymentId, int memberId, double amount,
                   LocalDate paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.memberId = memberId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public Payment(int memberId, double amount, String paymentMethod) {
        this.memberId = memberId;
        this.amount = amount;
        this.paymentDate = LocalDate.now();
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void displayInfo() {
        System.out.println("=== Payment Info ===");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Member ID: " + memberId);
        System.out.println("Amount: $" + amount);
        System.out.println("Date: " + paymentDate);
        System.out.println("Method: " + paymentMethod);
    }
}

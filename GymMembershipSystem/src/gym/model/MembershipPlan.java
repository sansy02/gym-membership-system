package gym.model;

public abstract class MembershipPlan {

    private int planId;
    private String planName;
    private double price;
    private int durationMonths;
    private String description;

    public MembershipPlan() {
    }

    public MembershipPlan(int planId, String planName, double price,
                          int durationMonths, String description) {
        this.planId = planId;
        this.planName = planName;
        this.price = price;
        this.durationMonths = durationMonths;
        this.description = description;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public abstract String getPlanDetails();

    public abstract String getFeatures();
}

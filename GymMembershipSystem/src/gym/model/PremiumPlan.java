package gym.model;

public class PremiumPlan extends MembershipPlan {

    public PremiumPlan() {
        super();
    }

    public PremiumPlan(int planId, String planName, double price,
                       int durationMonths, String description) {
        super(planId, planName, price, durationMonths, description);
    }

    @Override
    public String getPlanDetails() {
        return "Premium Plan ($" + getPrice() + "/month)";
    }

    @Override
    public String getFeatures() {
        return "All Basic features + Group classes, Pool access, "
                + "1 Personal trainer session/week";
    }
}

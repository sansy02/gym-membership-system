package gym.model;

public class VIPPlan extends MembershipPlan {

    public VIPPlan() {
        super();
    }

    public VIPPlan(int planId, String planName, double price,
                   int durationMonths, String description) {
        super(planId, planName, price, durationMonths, description);
    }

    @Override
    public String getPlanDetails() {
        return "VIP Plan ($" + getPrice() + "/month)";
    }

    @Override
    public String getFeatures() {
        return "All Premium features + Unlimited private training, "
                + "Sauna & Spa, 24/7 access, VIP lounge, Free parking";
    }
}

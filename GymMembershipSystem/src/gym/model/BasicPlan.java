package gym.model;

public class BasicPlan extends MembershipPlan {

    public BasicPlan() {
        super();
    }

    public BasicPlan(int planId, String planName, double price,
                     int durationMonths, String description) {
        super(planId, planName, price, durationMonths, description);
    }

    @Override
    public String getPlanDetails() {
        return "Basic Plan ($" + getPrice() + "/month)";
    }

    @Override
    public String getFeatures() {
        return "Standard gym equipment, Locker room access, "
                + "Open hours: 6AM-10PM";
    }
}

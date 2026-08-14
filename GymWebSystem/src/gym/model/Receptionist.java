package gym.model;

public class Receptionist extends Staff {

    private String shift;

    public Receptionist() {
        super();
    }

    public Receptionist(int id, String name, String email, String phone,
                        double salary, String shift) {
        super(id, name, email, phone, salary);
        this.shift = shift;
    }

    public Receptionist(String name, String email, String phone,
                        double salary, String shift) {
        super(name, email, phone, salary);
        this.shift = shift;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public String getRole() {
        return "Receptionist";
    }

    @Override
    public String getDepartment() {
        return "Front Desk / Administration";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Receptionist Info ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Role: " + getRole());
        System.out.println("Department: " + getDepartment());
        System.out.println("Shift: " + shift);
        System.out.println("Salary: $" + getSalary());
    }
}

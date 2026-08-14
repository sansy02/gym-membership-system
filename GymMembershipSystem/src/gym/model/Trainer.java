package gym.model;

public class Trainer extends Staff {

    private String specialization;

    public Trainer() {
        super();
    }

    public Trainer(int id, String name, String email, String phone,
                   double salary, String specialization) {
        super(id, name, email, phone, salary);
        this.specialization = specialization;
    }

    public Trainer(String name, String email, String phone,
                   double salary, String specialization) {
        super(name, email, phone, salary);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getRole() {
        return "Trainer";
    }

    @Override
    public String getDepartment() {
        return "Fitness Department";
    }

    @Override
    public void displayInfo() {
        System.out.println("=== Trainer Info ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Phone: " + getPhone());
        System.out.println("Role: " + getRole());
        System.out.println("Department: " + getDepartment());
        System.out.println("Specialization: " + specialization);
        System.out.println("Salary: $" + getSalary());
    }
}

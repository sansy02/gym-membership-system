package gym.model;

public abstract class Staff extends Person {

    private double salary;
    private String username;
    private String password;

    public Staff() {
        super();
    }

    public Staff(int id, String name, String email, String phone, double salary) {
        super(id, name, email, phone);
        this.salary = salary;
    }

    public Staff(String name, String email, String phone, double salary) {
        super(name, email, phone);
        this.salary = salary;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract String getDepartment();
}

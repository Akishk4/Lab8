package BackEnd;

public class Admin extends User {
    public Admin() { super(); this.role = "admin"; }

    public Admin(String username, String email, String passwordHash) {
        super("admin", username, email, passwordHash);
    }
}
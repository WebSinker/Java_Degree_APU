package models;

public abstract class User {
    protected String id;
    protected String username;
    protected String password;
    protected String name;
    protected String contactNumber;
    protected String role;
    protected String creatorId; // Tracks who created this user
    protected double balance;   // E-Wallet balance

    public User(String id, String username, String password, String name, String contactNumber, String role, String creatorId, double balance) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.contactNumber = contactNumber;
        this.role = role;
        this.creatorId = creatorId;
        this.balance = balance;
    }

    public User(String id, String username, String password, String name, String contactNumber, String role, String creatorId) {
        this(id, username, password, name, contactNumber, role, creatorId, 0.0);
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getRole() { return role; }
    public String getCreatorId() { return creatorId; }
    public double getBalance() { return balance; }
    public String getShift() { return "N/A"; } // Generic User has no shift, overridden by Technician

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setBalance(double balance) { this.balance = balance; }

    public abstract String toCSV();
}

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
    protected String paymentPin; // 6-digit payment authentication PIN

    public User(String id, String username, String password, String name, String contactNumber, String role, String creatorId, double balance, String paymentPin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.contactNumber = contactNumber;
        this.role = role;
        this.creatorId = creatorId;
        this.balance = balance;
        this.paymentPin = paymentPin;
    }

    public User(String id, String username, String password, String name, String contactNumber, String role, String creatorId, double balance) {
        this(id, username, password, name, contactNumber, role, creatorId, balance, "123456");
    }

    public User(String id, String username, String password, String name, String contactNumber, String role, String creatorId) {
        this(id, username, password, name, contactNumber, role, creatorId, 0.0, "123456");
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getContactNumber() { return contactNumber; }
    public String getRole() { return role; }
    public String getCreatorId() { return creatorId; }
    public double getBalance() { return balance; }
    public String getPaymentPin() { return paymentPin; }
    public String getShift() { return "N/A"; } // Generic User has no shift, overridden by Technician

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setPaymentPin(String pin) { this.paymentPin = pin; }

    public abstract String toCSV();
}

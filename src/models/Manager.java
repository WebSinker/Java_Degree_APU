package models;

public class Manager extends User {
    public Manager(String id, String username, String password, String name, String contactNumber, String creatorId) {
        super(id, username, password, name, contactNumber, "Manager", creatorId);
    }

    @Override
    public String toCSV() {
        return String.join(",", id, username, password, name, contactNumber, role, "N/A", creatorId, String.valueOf(balance), paymentPin);
    }
}

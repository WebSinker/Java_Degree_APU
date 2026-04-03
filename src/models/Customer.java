package models;

public class Customer extends User {
    public Customer(String id, String username, String password, String name, String contactNumber, String creatorId) {
        super(id, username, password, name, contactNumber, "Customer", creatorId);
    }

    @Override
    public String toCSV() {
        return String.join(",", id, username, password, name, contactNumber, role, "N/A", creatorId, String.valueOf(balance));
    }
}

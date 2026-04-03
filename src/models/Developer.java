package models;

public class Developer extends User {
    public Developer(String id, String username, String password, String name, String contactNumber, String creatorId) {
        super(id, username, password, name, contactNumber, "Developer", creatorId);
    }

    @Override
    public String toCSV() {
        return String.join(",", id, username, password, name, contactNumber, role, "N/A", creatorId, String.valueOf(balance));
    }
}

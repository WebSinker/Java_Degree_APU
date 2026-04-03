package models;

public class CounterStaff extends User {
    public CounterStaff(String id, String username, String password, String name, String contactNumber, String creatorId) {
        super(id, username, password, name, contactNumber, "CounterStaff", creatorId);
    }

    @Override
    public String toCSV() {
        return String.join(",", id, username, password, name, contactNumber, role, "N/A", creatorId, String.valueOf(balance));
    }
}

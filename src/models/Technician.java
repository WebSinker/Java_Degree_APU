package models;

public class Technician extends User {
    private String shift; // "Morning" (08-15) or "Night" (15-22)

    public Technician(String id, String username, String password, String name, String contactNumber, String shift, String creatorId) {
        super(id, username, password, name, contactNumber, "Technician", creatorId);
        this.shift = shift;
    }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    @Override
    public String toCSV() {
        return String.join(",", id, username, password, name, contactNumber, role, shift, creatorId, String.valueOf(balance));
    }
}

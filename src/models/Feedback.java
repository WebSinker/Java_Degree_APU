package models;

public class Feedback {
    private String id;
    private String customerId;
    private String appointmentId;
    private String technicianId;
    private String comment;
    private int rating; // 1-5
    private String date;
    private boolean isHidden;

    public Feedback(String id, String customerId, String appointmentId, String technicianId, String comment, int rating, String date, boolean isHidden) {
        this.id = id;
        this.customerId = customerId;
        this.appointmentId = appointmentId;
        this.technicianId = technicianId;
        this.comment = comment;
        this.rating = rating;
        this.date = date;
        this.isHidden = isHidden;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getAppointmentId() { return appointmentId; }
    public String getTechnicianId() { return technicianId; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }
    public String getDate() { return date; }
    public boolean isHidden() { return isHidden; }

    public void setHidden(boolean hidden) { isHidden = hidden; }
    public void setComment(String comment) { this.comment = comment; }

    public String toCSV() {
        return id + "," + customerId + "," + appointmentId + "," + technicianId + "," + comment + "," + rating + "," + date + "," + isHidden;
    }
}

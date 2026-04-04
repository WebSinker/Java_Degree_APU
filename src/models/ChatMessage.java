package models;

public class ChatMessage {
    private String id;
    private String appointmentId;
    private String senderId;
    private String message;
    private String timestamp;

    public ChatMessage(String id, String appointmentId, String senderId, String message, String timestamp) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getAppointmentId() { return appointmentId; }
    public String getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }

    public String toCSV() {
        // Escaping commas in message to prevent CSV breakdown
        String escapedMsg = message.replace(",", "[COMMA]");
        return id + "," + appointmentId + "," + senderId + "," + escapedMsg + "," + timestamp;
    }

    public static ChatMessage fromCSV(String line) {
        String[] data = line.split(",");
        if (data.length < 5) return null;
        String msg = data[3].replace("[COMMA]", ",");
        return new ChatMessage(data[0], data[1], data[2], msg, data[4]);
    }
}

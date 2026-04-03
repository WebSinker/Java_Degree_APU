package models;

public class Appointment {
    public static final String STATUS_PENDING   = "Pending";
    public static final String STATUS_COMPLETED = "Completed";

    private String id;            
    private String customerId;    
    private String technicianId;  
    private String counterStaffId;
    private String serviceType;   
    private String serviceId;     
    private String date;        
    private String timeSlot;      
    private String status;        
    private double price;         
    private boolean paid;         

    public Appointment(String id, String customerId, String technicianId,
                       String counterStaffId, String serviceType, String serviceId,
                       String date, String timeSlot, String status,
                       double price, boolean paid) {
        this.id            = id;
        this.customerId    = customerId;
        this.technicianId  = technicianId;
        this.counterStaffId= counterStaffId;
        this.serviceType   = serviceType;
        this.serviceId     = serviceId;
        this.date          = date;
        this.timeSlot      = timeSlot;
        this.status        = status;
        this.price         = price;
        this.paid          = paid;
    }

    public String getId()            { return id; }
    public String getCustomerId()    { return customerId; }
    public String getTechnicianId()  { return technicianId; }
    public String getCounterStaffId(){ return counterStaffId; }
    public String getServiceType()   { return serviceType; }
    public String getServiceId()     { return serviceId; }
    public String getDate()          { return date; }
    public String getTimeSlot()      { return timeSlot; }
    public String getStatus()        { return status; }
    public double getPrice()         { return price; }
    public boolean isPaid()          { return paid; }

    public void setStatus(String status) { this.status = status; }
    public void setPaid(boolean paid)    { this.paid = paid; }
    public void setTechnicianId(String technicianId) { this.technicianId = technicianId; }
    public void setCounterStaffId(String counterStaffId) { this.counterStaffId = counterStaffId; }
    public void setDate(String date) { this.date = date; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
    public void setPrice(double price) { this.price = price; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public String toCSV() {
        return String.join(",",
            id, customerId, technicianId, counterStaffId,
            serviceType, serviceId, date, timeSlot, status,
            String.valueOf(price), String.valueOf(paid));
    }

    public static Appointment fromCSV(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length != 11) return null;
        try {
            return new Appointment(
                p[0], p[1], p[2], p[3], p[4], p[5],
                p[6], p[7], p[8],
                Double.parseDouble(p[9]),
                Boolean.parseBoolean(p[10])
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

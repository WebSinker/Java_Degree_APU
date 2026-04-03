package models;

public class Receipt {

    private String id;           
    private String appointmentId; 
    private String customerId;    
    private String serviceType;  
    private String serviceName;   
    private double amountPaid;
    private String paymentDate;   
    private String counterStaffId;

    public Receipt(String id, String appointmentId, String customerId,
                   String serviceType, String serviceName,
                   double amountPaid, String paymentDate, String counterStaffId) {
        this.id            = id;
        this.appointmentId = appointmentId;
        this.customerId    = customerId;
        this.serviceType   = serviceType;
        this.serviceName   = serviceName;
        this.amountPaid    = amountPaid;
        this.paymentDate   = paymentDate;
        this.counterStaffId= counterStaffId;
    }

    public String getId()             { return id; }
    public String getAppointmentId()  { return appointmentId; }
    public String getCustomerId()     { return customerId; }
    public String getServiceType()    { return serviceType; }
    public String getServiceName()    { return serviceName; }
    public double getAmountPaid()     { return amountPaid; }
    public String getPaymentDate()    { return paymentDate; }
    public String getCounterStaffId() { return counterStaffId; }

    public String toCSV() {
        String safeName = serviceName.replace(",", ";");
        return String.join(",",
            id, appointmentId, customerId, serviceType, safeName,
            String.format("%.2f", amountPaid), paymentDate, counterStaffId);
    }

    public static Receipt fromCSV(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length != 8) return null;
        try {
            return new Receipt(
                p[0], p[1], p[2], p[3], p[4],
                Double.parseDouble(p[5]),
                p[6], p[7]
            );
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

package models;

public class ServiceItem {
    private String id;
    private String name;
    private String category; // "Normal" or "Major"
    private double price;

    public ServiceItem(String id, String name, String category, double price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }

    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }

    public String toCSV() {
        return id + "," + name + "," + category + "," + price;
    }
}

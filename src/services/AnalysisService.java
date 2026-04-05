package services;

import models.ServiceItem;
import models.Feedback;
import models.Receipt;
import utils.FileHandler;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AnalysisService {

    public static Map<String, Double> getMonthlyRevenueTrend() {
        List<Receipt> receipts = FileHandler.loadAllReceipts();
        // Use TreeMap to keep months sorted chronologically
        Map<String, Double> revenueByMonth = new TreeMap<>();
        
        for (Receipt r : receipts) {
            // date format: YYYY-MM-DD
            String date = r.getPaymentDate();
            if (date != null && date.length() >= 7) {
                String monthKey = date.substring(0, 7); // YYYY-MM
                revenueByMonth.put(monthKey, revenueByMonth.getOrDefault(monthKey, 0.0) + r.getAmountPaid());
            }
        }
        return revenueByMonth;
    }

    public static Map<String, Long> getServiceCategoryDistribution() {
        List<ServiceItem> items = FileHandler.loadAllServices();
        return items.stream()
                .collect(Collectors.groupingBy(ServiceItem::getCategory, Collectors.counting()));
    }

    public static Map<String, Double> getAverageRevenueByCategory() {
        List<ServiceItem> items = FileHandler.loadAllServices();
        return items.stream()
                .collect(Collectors.groupingBy(ServiceItem::getCategory, 
                        Collectors.averagingDouble(ServiceItem::getPrice)));
    }

    public static double getAverageCustomerRating() {
        List<Feedback> list = FileHandler.loadAllFeedback();
        if (list.isEmpty()) return 0.0;
        return list.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);
    }
}

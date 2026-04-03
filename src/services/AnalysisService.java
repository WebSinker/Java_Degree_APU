package services;

import models.ServiceItem;
import models.Feedback;
import utils.FileHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalysisService {

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

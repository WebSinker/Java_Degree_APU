package services;

import models.Customer;
import utils.FileHandler;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerService {

    public static List<Customer> getAllCustomers() {
        return FileHandler.loadAllUsers().stream()
                .filter(u -> u instanceof Customer)
                .map(u -> (Customer) u)
                .collect(Collectors.toList());
    }

    public static boolean saveCustomer(Customer customer) {
        return FileHandler.saveUser(customer);
    }

    public static boolean updateCustomer(Customer customer) {
        return UserService.updateUser(customer);
    }

    public static boolean deleteCustomer(String id) {
        return UserService.deleteUser(id);
    }
}

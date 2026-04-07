package utils;

import models.*;
import models.Appointment;
import models.Receipt;
import models.ServiceItem;
import models.Feedback;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler – centralized data access layer for the APU-ASC system.
 *
 * OOP Concept – Abstraction:
 *   All I/O implementation details (BufferedReader, FileWriter, CSV parsing)
 *   are hidden behind clean static methods. The rest of the application never
 *   opens a file directly; it calls these methods instead.
 *
 * OOP Concept – Encapsulation:
 *   File path constants are private; they cannot be changed accidentally by
 *   other classes.
 */
public class FileHandler {

    // ── File path constants (private – encapsulation) ────────────────────────
    private static final String USERS_FILE        = "data/users.csv";
    private static final String SERVICES_FILE     = "data/services.csv";
    private static final String FEEDBACK_FILE     = "data/feedback.csv";
    private static final String APPOINTMENTS_FILE = "data/appointments.csv";
    private static final String RECEIPTS_FILE     = "data/receipts.csv";
    private static final String CHATS_FILE        = "data/interactions.csv";

    // ── Initialisation ───────────────────────────────────────────────────────

    /**
     * Creates the data/ directory and all required CSV files on first run.
     * Safe to call multiple times (no-op if files already exist).
     */
    public static void initializeFiles() {
        try {
            File dir = new File("data");
            if (!dir.exists()) dir.mkdir();

            String[] files = {
                USERS_FILE, SERVICES_FILE, FEEDBACK_FILE,
                APPOINTMENTS_FILE, RECEIPTS_FILE, CHATS_FILE
            };
            for (String f : files) {
                File file = new File(f);
                if (!file.exists()) file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error initializing data files: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // USERS
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Appends a single new user to the users file.
     *
     * OOP – Polymorphism: user.toCSV() dispatches to the correct override
     * (Customer, Manager, Technician, CounterStaff) without instanceof checks.
     */
    public static boolean saveUser(User user) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            bw.write(user.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads every user from disk and returns them as typed model objects.
     *
     * OOP – Polymorphism: the List holds User references; each element's
     * runtime type is the correct subclass (Customer, Manager, etc.).
     */
    public static List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length < 6) continue;
                String id = data[0], username = data[1], pass = data[2],
                       name = data[3], phone = data[4], role = data[5];
                
                double balance = (data.length > 8) ? Double.parseDouble(data[8]) : 0.0;
                String pin = (data.length > 9) ? data[9] : "123456"; 
                String shift = (data.length > 6) ? data[6] : "N/A";
                String creatorId = (data.length > 7) ? data[7] : "System";

                User u = null;
                switch (role) {
                    case "Manager":      u = new Manager(id, username, pass, name, phone, creatorId);      break;
                    case "CounterStaff": u = new CounterStaff(id, username, pass, name, phone, creatorId); break;
                    case "Technician":   u = new Technician(id, username, pass, name, phone, shift, creatorId);   break;
                    case "Customer":     u = new Customer(id, username, pass, name, phone, creatorId);      break;
                    case "Developer":    u = new Developer(id, username, pass, name, phone, creatorId);    break;
                    default: break;
                }
                if (u != null) {
                    u.setBalance(balance);
                    u.setPaymentPin(pin);
                    users.add(u);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Overwrites the entire users file (used for update / delete operations).
     */
    public static void saveAllUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, false))) {
            for (User u : users) {
                bw.write(u.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving all users: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SERVICES
    // ══════════════════════════════════════════════════════════════════════════

    public static List<ServiceItem> loadAllServices() {
        List<ServiceItem> items = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(SERVICES_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length == 4) {
                    items.add(new ServiceItem(
                        data[0], data[1], data[2], Double.parseDouble(data[3])));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading services: " + e.getMessage());
        }
        return items;
    }

    public static void saveAllServices(List<ServiceItem> items) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(SERVICES_FILE, false))) {
            for (ServiceItem s : items) {
                bw.write(s.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving services: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // FEEDBACK
    // ══════════════════════════════════════════════════════════════════════════

    public static List<Feedback> loadAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FEEDBACK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] data = line.split(",");
                if (data.length >= 6) {
                    try {
                        String appId = (data.length >= 8) ? data[2] : "N/A";
                        String techId = (data.length >= 8) ? data[3] : "N/A";
                        String comment = (data.length >= 8) ? data[4] : data[2];
                        int rating = (data.length >= 8) ? Integer.parseInt(data[5]) : Integer.parseInt(data[3]);
                        String date = (data.length >= 8) ? data[6] : data[4];
                        boolean isHidden = (data.length >= 8) ? Boolean.parseBoolean(data[7]) : Boolean.parseBoolean(data[5]);
                        
                        list.add(new Feedback(
                            data[0], data[1], appId, techId,
                            comment, rating, date, isHidden));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping malformed feedback line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading feedback: " + e.getMessage());
        }
        return list;
    }

    public static void saveAllFeedback(List<Feedback> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FEEDBACK_FILE, false))) {
            for (Feedback f : list) {
                bw.write(f.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving feedback: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // APPOINTMENTS  (new – added for Customer section)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Loads all appointments from disk.
     * Uses Appointment.fromCSV() – parsing logic is encapsulated in the model.
     */
    public static List<Appointment> loadAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Appointment a = Appointment.fromCSV(line.trim());
                if (a != null) list.add(a);
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
        return list;
    }

    /** Overwrites the entire appointments file (for status / paid updates). */
    public static void saveAllAppointments(List<Appointment> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPOINTMENTS_FILE, false))) {
            for (Appointment a : list) {
                bw.write(a.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    /** Appends a single new appointment (faster than rewriting the whole file). */
    public static boolean saveAppointment(Appointment a) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPOINTMENTS_FILE, true))) {
            bw.write(a.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Convenience method: returns only appointments belonging to a specific customer.
     * Used by CustomerHistoryView and CustomerCommentView.
     */
    public static List<Appointment> loadAppointmentsByCustomer(String customerId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : loadAllAppointments()) {
            if (a.getCustomerId().equals(customerId)) result.add(a);
        }
        return result;
    }

    /**
     * Convenience method: returns only appointments assigned to a specific technician.
     * (Used by the Technician section – included here so FileHandler is complete.)
     */
    public static List<Appointment> loadAppointmentsByTechnician(String technicianId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment a : loadAllAppointments()) {
            if (a.getTechnicianId().equals(technicianId)) result.add(a);
        }
        return result;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // RECEIPTS  (new – added for Customer section)
    // ══════════════════════════════════════════════════════════════════════════

    /** Loads all receipts from disk. */
    public static List<Receipt> loadAllReceipts() {
        List<Receipt> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(RECEIPTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                Receipt r = Receipt.fromCSV(line.trim());
                if (r != null) list.add(r);
            }
        } catch (IOException e) {
            System.err.println("Error loading receipts: " + e.getMessage());
        }
        return list;
    }

    /** Overwrites the entire receipts file. */
    public static void saveAllReceipts(List<Receipt> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECEIPTS_FILE, false))) {
            for (Receipt r : list) {
                bw.write(r.toCSV());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving receipts: " + e.getMessage());
        }
    }

    /** Appends a single receipt (called by CounterStaff after payment collection). */
    public static boolean saveReceipt(Receipt r) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RECEIPTS_FILE, true))) {
            bw.write(r.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving receipt: " + e.getMessage());
            return false;
        }
    }

    /**
     * Convenience method: returns only receipts for a specific customer.
     * Used by CustomerHistoryView.
     */
    public static List<Receipt> loadReceiptsByCustomer(String customerId) {
        List<Receipt> result = new ArrayList<>();
        for (Receipt r : loadAllReceipts()) {
            if (r.getCustomerId().equals(customerId)) result.add(r);
        }
        return result;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CHATS
    // ══════════════════════════════════════════════════════════════════════════

    public static List<ChatMessage> loadChatsForAppointment(String appId) {
        List<ChatMessage> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CHATS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                ChatMessage m = ChatMessage.fromCSV(line.trim());
                if (m != null && m.getAppointmentId().equals(appId)) {
                    list.add(m);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading chats: " + e.getMessage());
        }
        return list;
    }

    public static boolean saveChatMessage(ChatMessage msg) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(CHATS_FILE, true))) {
            bw.write(msg.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.err.println("Error saving chat: " + e.getMessage());
            return false;
        }
    }
}

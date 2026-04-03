package services;

import models.Appointment;
import models.Technician;
import models.User;
import utils.FileHandler;
import java.util.List;
import java.util.stream.Collectors;

public class AppointmentService {

    public static List<Appointment> getAllAppointments() {
        return FileHandler.loadAllAppointments();
    }

    public static boolean createAppointment(Appointment appointment) {
        return FileHandler.saveAppointment(appointment);
    }

    public static boolean updateAppointment(Appointment updated) {
        List<Appointment> all = FileHandler.loadAllAppointments();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(updated.getId())) {
                all.set(i, updated);
                FileHandler.saveAllAppointments(all);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a technician is available for a given date and time slot.
     * @param techId ID of the technician.
     * @param date Date of the service.
     * @param slot Start time (e.g., "08:00").
     * @param duration In hours.
     * @return true if available.
     */
    public static boolean isTechnicianAvailable(String techId, String date, String slot, int duration) {
        // First check technician's shift
        User user = FileHandler.loadAllUsers().stream()
                .filter(u -> u.getId().equals(techId) && u instanceof Technician)
                .findFirst().orElse(null);
        
        if (user == null) return false;
        Technician tech = (Technician) user;
        
        int startHour = Integer.parseInt(slot.split(":")[0]);
        
        // Shift check: Morning (08-15), Night (15-22)
        if (tech.getShift().equalsIgnoreCase("Morning")) {
            if (startHour < 8 || startHour + duration > 15) return false;
        } else {
            if (startHour < 15 || startHour + duration > 22) return false;
        }

        // Check for conflicting appointments
        List<Appointment> apps = FileHandler.loadAppointmentsByTechnician(techId)
                .stream().filter(a -> a.getDate().equals(date))
                .collect(Collectors.toList());

        for (Appointment a : apps) {
            int aStart = Integer.parseInt(a.getTimeSlot().split(":")[0]);
            int aDur = a.getServiceType().equalsIgnoreCase("Major") ? 3 : 1;
            
            // Overlap check
            if (!(startHour + duration <= aStart || startHour >= aStart + aDur)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Assigns a technician to a pending appointment.
     */
    public static boolean assignTechnician(String appId, String techId, String counterStaffId) {
        List<Appointment> all = FileHandler.loadAllAppointments();
        for (Appointment a : all) {
            if (a.getId().equals(appId)) {
                int duration = a.getServiceType().equalsIgnoreCase("Major") ? 3 : 1;
                if (isTechnicianAvailable(techId, a.getDate(), a.getTimeSlot(), duration)) {
                    a.setTechnicianId(techId);
                    a.setCounterStaffId(counterStaffId);
                    a.setStatus(Appointment.STATUS_PENDING); // or STATUS_ASSIGNED if we had one
                    FileHandler.saveAllAppointments(all);
                    return true;
                }
            }
        }
        return false;
    }
}

# APU Automotive Service Centre (APU-ASC) Management System

A modern, robust, and secure management system designed for the APU Automotive Service Centre. Built with **JavaFX**, this application provides a sleek UI, real-time appointment scheduling, and a comprehensive **E-Wallet** payment ecosystem.

## 🚀 Key Features

### 👤 Multi-Role Architecture
- **Manager**: Oversee staff, manage users, and monitor services.
- **Counter Staff**: Customer management, appointment assignment, and payment collection.
- **Technician**: View assigned tasks and mark service completion.
- **Customer**: Profile management, real-time booking, E-Wallet top-ups, and service history.
- **Developer**: System maintenance and root-level configurations.

### 💳 E-Wallet & Payment System
- **Digital Wallet**: Secure, persistent balance for all customers.
- **Mandatory Deposit**: Automated RM 50 deposit upon booking to ensure commitment.
- **Integrated Payments**: Deduct remaining service fees directly from the wallet.
- **History & Receipts**: Automated receipt generation and storage for all transactions.

### 🛡️ Anti-Fraud Measures
- **Debt Protection**: New bookings are automatically blocked if a customer has outstanding unpaid "Completed" services.
- **Identity Enforcement**: Contact number uniqueness validation prevents account abandonment and duplication.

### 📅 Advanced Scheduling
- **Slot Management**: Precise scheduling between 08:00 AM and 10:00 PM.
- **Technician Availability**: Automated checks based on technician shifts (Morning/Night) and existing overlaps.

## 🛠️ Technology Stack
- **Language**: Java 17+
- **GUI Framework**: JavaFX (with modern CSS styling)
- **Build Tool**: Maven
- **Data Persistence**: CSV-based storage (Flat-file architecture)

## 📁 Project Structure
- `src/models`: Core data entities (User, Appointment, Receipt, etc.)
- `src/services`: Business logic (Authentication, Appointment scheduling)
- `src/ui`: JavaFX views and controllers
- `src/utils`: File handling and data persistence
- `data/`: CSV database files

## 🏁 Getting Started

### Prerequisites
- JDK 17 or higher
- Apache Maven

### Installation
1. Clone the repository.
2. Ensure `maven` is installed and configured in your PATΗ.

### Running the Application
Execute the following command in the project root:
```bash
mvn javafx:run
```

## 📄 License
This project is developed for APU Academic Purposes.

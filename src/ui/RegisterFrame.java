package ui;

import models.Customer;
import services.AuthenticationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtName;
    private JTextField txtContact;
    private JButton btnRegister;
    private JButton btnCancel;
    private JFrame parentFrame; // To return to LoginFrame

    public RegisterFrame(JFrame parent) {
        this.parentFrame = parent;
        setTitle("APU ASC - Register Customer");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Or DISPOSE_ON_CLOSE if not ending app
        setLocationRelativeTo(null);
        setResizable(false);

        // UI Setup
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        txtUsername = new JTextField();
        panel.add(txtUsername);

        panel.add(new JLabel("Password:"));
        txtPassword = new JPasswordField();
        panel.add(txtPassword);

        panel.add(new JLabel("Full Name:"));
        txtName = new JTextField();
        panel.add(txtName);

        panel.add(new JLabel("Contact Number:"));
        txtContact = new JTextField();
        panel.add(txtContact);

        btnRegister = new JButton("Register");
        btnCancel = new JButton("Cancel");

        panel.add(btnRegister);
        panel.add(btnCancel);

        add(panel);

        // Event Listeners
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegistration();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToLogin();
            }
        });
    }

    private void handleRegistration() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String name = txtName.getText().trim();
        String contact = txtContact.getText().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = "C-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Customer newCustomer = new Customer(id, username, password, name, contact, "Self");

        String result = AuthenticationService.registerUser(newCustomer);

        if ("Success".equals(result)) {
            JOptionPane.showMessageDialog(this, "Registration Successful! You may now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            returnToLogin();
        } else {
            JOptionPane.showMessageDialog(this, result, "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnToLogin() {
        parentFrame.setVisible(true);
        dispose(); // Destroy this frame
    }
}

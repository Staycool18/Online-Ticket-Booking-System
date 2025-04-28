package OTS;

import javax.swing.*;
import java.sql.*;

public class RegisterFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton registerBtn;

    public RegisterFrame() {
        setTitle("Register");
        setSize(600, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(20, 20, 80, 25);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(100, 20, 150, 25);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(20, 60, 80, 25);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(100, 60, 150, 25);
        add(passwordField);

        registerBtn = new JButton("Register");
        registerBtn.setBounds(90, 100, 100, 30);
        add(registerBtn);

        registerBtn.addActionListener(e -> register());

        setLocationRelativeTo(null); // Center the frame on screen
        setVisible(true); // Show the frame only once, after all components are added
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users(username, password) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration Successful!");
            this.dispose();
            new LoginFrame();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

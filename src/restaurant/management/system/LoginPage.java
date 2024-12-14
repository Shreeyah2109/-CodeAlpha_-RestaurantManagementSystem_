package restaurant.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    JLabel usernameLabel, passwordLabel, roleLabel;
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;
    JComboBox<String> roleComboBox;

    public LoginPage() {
        setLayout(null);
        setTitle("Login");
        setSize(400, 400);
        setLocation(400, 200);

        // Set background color
        getContentPane().setBackground(new Color(244, 247, 255));

        // Heading
        JLabel heading = new JLabel("Restaurant Manager Login");
        heading.setBounds(50, 20, 300, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(new Color(0, 122, 204));
        add(heading);

        usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(50, 100, 100, 30);
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 100, 200, 30);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(usernameField);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 150, 100, 30);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 200, 30);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createLineBorder(new Color(0, 122, 204), 2));
        add(passwordField);

        roleLabel = new JLabel("Role:");
        roleLabel.setBounds(50, 200, 100, 30);
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(roleLabel);

        // Role selection for Admin or Staff
        roleComboBox = new JComboBox<>(new String[] {"Admin", "Staff"});
        roleComboBox.setBounds(150, 200, 200, 30);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        add(roleComboBox);

        loginButton = new JButton("Login");
        loginButton.setBounds(150, 250, 100, 40);
        loginButton.setFont(new Font("Arial", Font.PLAIN, 16));
        loginButton.setBackground(new Color(0, 122, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        add(loginButton);

        setVisible(true);
    }
@Override
public void actionPerformed(ActionEvent ae) {
    if (ae.getSource() == loginButton) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        try {
            // Establish database connection
            Conn conn = new Conn();
            if (conn.c == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
                return;
            }

            // Query to check user credentials
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
            PreparedStatement pst = conn.c.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login successful.");

                // Close the login window
                setVisible(false);
                dispose();  // Dispose of the login window after successful login

                // Redirect based on the role
                if ("Admin".equals(role)) {
                    // Directly open the AddItemPage
                    AddItemPage addItemPage = new AddItemPage(null);  // Passing null for ManageItemsPage
                    addItemPage.setVisible(true);  // Make sure the page is visible
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username, password, or role.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }
}


    public static void main(String[] args) {
        new LoginPage();
    }
}

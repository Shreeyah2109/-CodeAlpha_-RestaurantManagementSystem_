package restaurant.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class AddItemPage extends JFrame {
    private JTextField itemNameField, priceField;
    private JLabel imageLabel;
    private JButton submitButton, uploadButton, nextButton;
    private File imageFile;
    private ManageItemsPage manageItemsPage;  // Reference to ManageItemsPage

    // Constructor for adding a new item
    public AddItemPage(ManageItemsPage manageItemsPage) {
        this.manageItemsPage = manageItemsPage;  // Initialize the reference
        // Frame setup
        setTitle("Add New Menu Item");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout setup (GridBagLayout)
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Heading
        JLabel heading = new JLabel("Add New Menu Item", SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setForeground(new Color(0, 122, 204));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(heading, gbc);

        // Item Name
        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(itemNameLabel, gbc);

        itemNameField = new JTextField(20);
        itemNameField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(itemNameField, gbc);

        // Price
        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(priceLabel, gbc);

        priceField = new JTextField(20);
        priceField.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(priceField, gbc);

        // Category
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(categoryLabel, gbc);

        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Drinks", "Snacks"});
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(categoryComboBox, gbc);

        // Image Selection
        JLabel imageLabelText = new JLabel("Image:");
        imageLabelText.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(imageLabelText, gbc);

        imageLabel = new JLabel("No image selected");
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        imageLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(imageLabel, gbc);

        uploadButton = new JButton("Upload Image");
        uploadButton.setFont(new Font("Arial", Font.PLAIN, 16));
        uploadButton.setBackground(new Color(0, 122, 204));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                imageFile = fileChooser.getSelectedFile();
                imageLabel.setText(imageFile.getName());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(uploadButton, gbc);

        // Submit Button
        submitButton = new JButton("Add Item");
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(0, 122, 204));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            String itemName = itemNameField.getText();
            String price = priceField.getText();
            String category = (String) categoryComboBox.getSelectedItem();

            // Validate fields
            if (itemName.isEmpty() || price.isEmpty() || imageFile == null) {
                JOptionPane.showMessageDialog(null, "Please fill all fields and upload an image.");
                return;
            }

            // Insert item into the database
            try {
                Conn conn = new Conn();
                String query = "INSERT INTO items (item_name, price, category, image) VALUES (?, ?, ?, ?)";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, itemName);
                pst.setString(2, price);
                pst.setString(3, category);
                FileInputStream fis = new FileInputStream(imageFile);
                pst.setBinaryStream(4, fis, (int) imageFile.length());
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Item added successfully!");
                if (manageItemsPage != null) {
                    manageItemsPage.loadItems();  // Refresh items table
                }
            } catch (SQLException | FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 6;
        add(submitButton, gbc);

        // Next Button
        nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.BOLD, 16));
        nextButton.setBackground(new Color(0, 153, 76));
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(e -> {
            dispose();
            new ManageItemsPage().setVisible(true);  // Open ManageItemsPage
        });
        gbc.gridx = 1;
        gbc.gridy = 7;
        add(nextButton, gbc);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddItemPage(null));
    }
}

package restaurant.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class CustomerDetailsPage extends JFrame implements ActionListener {
    
    // Labels and fields for customer details
    JLabel heading, nameLabel, phoneLabel, totalLabel;
    JTextField nameField, phoneField;
    JButton saveCustomerButton, addItemButton, removeItemButton, finalizeButton, nextButton;
    JTable cartTable;
    DefaultTableModel tableModel;
    ArrayList<MenuItem> cartItems;
    JComboBox<String> categoryDropdown;
    JPanel itemPanel;
    JSpinner quantitySpinner;

    // Define the menu items and prices by category
    private final String[][] categories = {
        {"No Item Selected", "Idli", "Pasta", "Thali", "Dosa"},
        {"Paneer Kadhai", "Lassi", "Pav Bhaji", "Chicken Biryani", "Butter Chicken"},
        {"Veg Burger", "Chicken Burger", "French Fries", "Sandwich", "Pizza"},
        {"Juice", "Soft Drink", "Lassi", "Coffee", "Tea"},
        {"Chips", "Cookies", "Samosa", "Nachos", "Popcorn"}
    };
    private final double[][] prices = {
        {0.0, 150.0, 700.0, 400.0, 200.0},
        {600.0, 100.0, 250.0, 800.0, 700.0},
        {250.0, 300.0, 150.0, 350.0, 450.0},
        {80.0, 50.0, 100.0, 60.0, 40.0},
        {50.0, 70.0, 40.0, 80.0, 60.0}
    };

    public CustomerDetailsPage() {
        setLayout(null);

        // Heading
        heading = new JLabel("Customer Order");
        heading.setBounds(150, 20, 400, 50);
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        add(heading);

        // Customer Name
        nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(50, 100, 150, 30);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(200, 100, 200, 30);
        add(nameField);

        // Phone Number
        phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(50, 150, 150, 30);
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        add(phoneLabel);

        phoneField = new JTextField();
        phoneField.setBounds(200, 150, 200, 30);
        add(phoneField);

        // Save Customer Button
        saveCustomerButton = new JButton("Save Customer");
        saveCustomerButton.setBounds(200, 200, 150, 30);
        saveCustomerButton.setBackground(Color.BLACK);
        saveCustomerButton.setForeground(Color.WHITE);
        saveCustomerButton.addActionListener(this);
        add(saveCustomerButton);

        // Category Dropdown
        categoryDropdown = new JComboBox<>(new String[]{"Select Category", "Breakfast", "Lunch", "Dinner", "Drinks", "Snacks"});
        categoryDropdown.setBounds(50, 250, 150, 30);
        categoryDropdown.addActionListener(e -> loadItems(categoryDropdown.getSelectedIndex() - 1));  // Adjust for 'Select Category'
        add(categoryDropdown);

        // Dynamic Item Panel
        itemPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        JScrollPane itemScrollPane = new JScrollPane(itemPanel);
        itemScrollPane.setBounds(50, 300, 350, 150);
        add(itemScrollPane);

        // Add Item Button
        addItemButton = new JButton("Add to Cart");
        addItemButton.setBounds(50, 470, 150, 30);
        addItemButton.setBackground(Color.BLACK);
        addItemButton.setForeground(Color.WHITE);
        addItemButton.addActionListener(this);
        add(addItemButton);

        // Cart Table
        cartItems = new ArrayList<>();
        String[] columns = {"Item", "Quantity", "Price"};
        tableModel = new DefaultTableModel(columns, 0);
        cartTable = new JTable(tableModel);
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartScrollPane.setBounds(450, 100, 400, 300);
        add(cartScrollPane);

        // Cart Action Panel
        JPanel cartActionPanel = new JPanel();
        cartActionPanel.setLayout(new BorderLayout());

        removeItemButton = new JButton("Remove Item");
        cartActionPanel.add(removeItemButton, BorderLayout.WEST);

        totalLabel = new JLabel("Total: ₹0");
        cartActionPanel.add(totalLabel, BorderLayout.EAST);

        cartActionPanel.setBounds(450, 420, 400, 50);
        add(cartActionPanel);

        // Finalize Order Button
        finalizeButton = new JButton("Finalize Order");
        finalizeButton.setBounds(200, 500, 150, 30);
        finalizeButton.setBackground(Color.RED);
        finalizeButton.setForeground(Color.WHITE);
        finalizeButton.addActionListener(e -> finalizeOrder());
        add(finalizeButton);

        // Next Button
        nextButton = new JButton("Next");
        nextButton.setBounds(400, 500, 150, 30);
        nextButton.setBackground(Color.GREEN);
        nextButton.setForeground(Color.WHITE);
        nextButton.addActionListener(e -> openCustomerListPage());
        add(nextButton);

        // Load Initial Items (No items selected)
        loadItems(-1);
        setSize(900, 600);
        setVisible(true);
        setLocation(350, 50);
        setTitle("Customer Details & Item Selection");
    }

    private void loadItems(int categoryIndex) {
        itemPanel.removeAll();

        if (categoryIndex >= 0) {
            for (int i = 0; i < categories[categoryIndex].length; i++) {
                String itemName = categories[categoryIndex][i];
                double itemPrice = prices[categoryIndex][i];
                JCheckBox itemCheckbox = new JCheckBox(itemName + " - ₹" + itemPrice);
                quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
                JPanel itemRow = new JPanel(new BorderLayout());
                itemRow.add(itemCheckbox, BorderLayout.WEST);
                itemRow.add(quantitySpinner, BorderLayout.EAST);
                itemPanel.add(itemRow);
            }
        }

        itemPanel.revalidate();
        itemPanel.repaint();
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == saveCustomerButton) {
            saveCustomerDetails();
        } else if (ae.getSource() == addItemButton) {
            addSelectedItems();
        } else if (ae.getSource() == removeItemButton) {
            removeSelectedItem();
        }
    }

    private void saveCustomerDetails() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both customer name and phone number.");
        } else {
            JOptionPane.showMessageDialog(this, "Customer details saved!");
        }
    }

    private void addSelectedItems() {
        Component[] components = itemPanel.getComponents();
        for (Component comp : components) {
            JPanel itemRow = (JPanel) comp;
            JCheckBox checkbox = (JCheckBox) itemRow.getComponent(0);
            JSpinner spinner = (JSpinner) itemRow.getComponent(1);
            if (checkbox.isSelected()) {
                String itemName = checkbox.getText().split(" - ")[0];
                int quantity = (int) spinner.getValue();
                double price = Double.parseDouble(checkbox.getText().split("₹")[1]);

                // Add item to cart
                cartItems.add(new MenuItem(itemName, quantity, price * quantity));

                // Save the item to the database
                saveItemToDatabase(itemName, quantity, price * quantity);
            }
        }
        updateCartTable();
    }

    private void saveItemToDatabase(String itemName, int quantity, double totalPrice) {
        String customerName = nameField.getText();
        String phone = phoneField.getText();

        // Validate that customer details are available
        if (customerName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both customer name and phone number.");
            return;
        }

        try {
            // Assuming you have a valid database connection 'conn'
            Conn conn = new Conn();  // Connection class should be implemented with the database connection details

            // Insert customer details into the customer table (if needed)
            String query = "INSERT INTO order_items (customer_name, phone, item_name, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.c.prepareStatement(query);
            stmt.setString(1, customerName);
            stmt.setString(2, phone);
            stmt.setString(3, itemName);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, totalPrice);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Item added to order and saved in the database!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving item to database: " + e.getMessage());
        }
    }

    private void updateCartTable() {
        double totalAmount = 0;
        for (MenuItem item : cartItems) {
            tableModel.addRow(new Object[]{item.getName(), item.getQuantity(), item.getPrice()});
            totalAmount += item.getPrice();
        }
        totalLabel.setText("Total: ₹" + totalAmount);
    }

    private void removeSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow != -1) {
            cartItems.remove(selectedRow);
            tableModel.removeRow(selectedRow);
        }
    }

    private void finalizeOrder() {
        JOptionPane.showMessageDialog(this, "Order finalized! Thank you for your purchase.");
        // Here you can also add logic to store the final order details in the database
    }

    private void openCustomerListPage() {
        // Open the CustomerListPage when Next button is clicked
        CustomerListPage customerListPage = new CustomerListPage();
        customerListPage.setVisible(true);
        this.setVisible(false);  // Hide current page
    }

    public static void main(String[] args) {
        new CustomerDetailsPage();
    }
}

class MenuItem {
    private String name;
    private int quantity;
    private double price;

    public MenuItem(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

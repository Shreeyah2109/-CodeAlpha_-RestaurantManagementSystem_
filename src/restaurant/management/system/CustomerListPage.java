package restaurant.management.system;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CustomerListPage extends JFrame {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JButton updateButton, deleteButton, searchButton, refreshButton, addButton;
    private JTextField searchField;
    private JLabel heading;

    public CustomerListPage() {
        setTitle("Customer Details List");
        setLayout(null);

        // Heading
        heading = new JLabel("Customer List");
        heading.setBounds(250, 20, 200, 30);
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        add(heading);

        // Set up the table model
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Phone", "Created At"}, 0);
        customerTable = new JTable(tableModel);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14));
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add the table to a scroll pane
        scrollPane = new JScrollPane(customerTable);
        scrollPane.setBounds(50, 80, 500, 200);
        add(scrollPane);

        // Search Field and Button
        JLabel searchLabel = new JLabel("Search by Name:");
        searchLabel.setBounds(50, 300, 150, 30);
        add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(150, 300, 200, 30);
        add(searchField);

        searchButton = new JButton("Search");
        searchButton.setBounds(370, 300, 100, 30);
        searchButton.addActionListener(e -> searchCustomer());
        add(searchButton);

        // Add, Update, Delete, Refresh Buttons
        addButton = new JButton("Add Customer");
        addButton.setBounds(50, 340,120, 30);
        addButton.addActionListener(e -> addCustomer());
        add(addButton);

        updateButton = new JButton("Update");
        updateButton.setBounds(180, 340,100, 30);
        updateButton.addActionListener(e -> updateCustomer());
        add(updateButton);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(290, 340,100, 30);
        deleteButton.addActionListener(e -> deleteCustomer());
        add(deleteButton);

        refreshButton = new JButton("Refresh");
        refreshButton.setBounds(400, 340,100,30);
        refreshButton.addActionListener(e -> loadCustomerData());
        add(refreshButton);
        
 // Close Button
    JButton closeButton = new JButton("Close");
closeButton.setBounds(250, 380,100, 30);
    closeButton.addActionListener(e -> dispose());
    add(closeButton);
        // Fetch and load customer data from the database
        loadCustomerData();

        // Set up window properties
        setSize(600, 450);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Fetch data from the database and load it into the JTable
    private void loadCustomerData() {
        // Clear the existing data in the table model
        tableModel.setRowCount(0);

        // Fetch customer data from the database
        try {
            Conn conn = new Conn();
            String query = "SELECT * FROM customers ";  // Query to select all customers
            PreparedStatement pst = conn.c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Loop through the result set and add rows to the table model
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("id")));  // customer_id from the 'customers' table
                row.add(rs.getString("name"));            // name of the customer
                row.add(rs.getString("phone"));           // phone number of the customer
                row.add(rs.getString("created_at"));      // created_at timestamp of the record
                tableModel.addRow(row);
            }

            rs.close();
            pst.close();
            conn.s.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error retrieving customer data: " + e.getMessage());
        }
    }

    // Method to add a new customer
    private void addCustomer() {
        String name = JOptionPane.showInputDialog(this, "Enter Customer Name");
        String phone = JOptionPane.showInputDialog(this, "Enter Phone Number");

        if (name != null && phone != null && !name.trim().isEmpty() && !phone.trim().isEmpty()) {
            try {
                Conn conn = new Conn();
                String query = "INSERT INTO customers (name, phone, created_at) VALUES (?, ?, NOW())";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, phone);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(this, "Customer added successfully!");
                loadCustomerData(); // Reload the customer data to reflect the new addition

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Name and Phone cannot be empty.");
        }
    }

    // Method to search customers by name
    private void searchCustomer() {
        String searchText = searchField.getText().trim();

        // Clear the existing data in the table model
        tableModel.setRowCount(0);

        if (!searchText.isEmpty()) {
            try {
                Conn conn = new Conn();
                String query = "SELECT * FROM customers WHERE name LIKE ?";
                PreparedStatement pst = conn.c.prepareStatement(query);
                pst.setString(1, "%" + searchText + "%");
                ResultSet rs = pst.executeQuery();

                // Loop through the result set and add rows to the table model
                while (rs.next()) {
                    Vector<String> row = new Vector<>();
                    row.add(String.valueOf(rs.getInt("id")));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("phone"));
                    row.add(rs.getString("created_at"));
                    tableModel.addRow(row);
                }

                rs.close();
                pst.close();
                conn.s.close();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error searching customer: " + e.getMessage());
            }
        } else {
            loadCustomerData();
        }
    }

    // Method to update a customer's details
    private void updateCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            String customerName = tableModel.getValueAt(selectedRow, 1).toString();
            String customerPhone = tableModel.getValueAt(selectedRow, 2).toString();

            String newName = JOptionPane.showInputDialog(this, "Enter new name", customerName);
            String newPhone = JOptionPane.showInputDialog(this, "Enter new phone", customerPhone);

            if (newName != null && newPhone != null) {
                try {
                    Conn conn = new Conn();
                    String query = "UPDATE customers SET name = ?, phone = ? WHERE id = ?";
                    PreparedStatement pst = conn.c.prepareStatement(query);
                    pst.setString(1, newName);
                    pst.setString(2, newPhone);
                    pst.setInt(3, customerId);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Customer details updated!");
                    loadCustomerData(); // Reload the updated customer data

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating customer: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.");
        }
    }

    // Method to delete a customer's record
    private void deleteCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow != -1) {
            int customerId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());

            int confirmDelete = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirmDelete == JOptionPane.YES_OPTION) {
                try {
                    Conn conn = new Conn();
                    String query = "DELETE FROM customers WHERE id = ?";
                    PreparedStatement pst = conn.c.prepareStatement(query);
                    pst.setInt(1, customerId);
                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Customer deleted!");
                    loadCustomerData(); // Reload customer data after deletion

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting customer: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
        }
    }

    public static void main(String[] args) {
        new CustomerListPage();
    }
}

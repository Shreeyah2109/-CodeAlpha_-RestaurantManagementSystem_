package restaurant.management.system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableCellRenderer;

public class ManageItemsPage extends JFrame {
    private JTable itemsTable;
    private JTextField searchField;
    private JButton deleteButton, addItemButton, customerDetailsButton, searchButton; // Added search button

    public ManageItemsPage() {
        setTitle("Manage Menu Items");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Search bar
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        // Search button
        searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        // Table to display items
        String[] columnNames = {"Item Name", "Price", "Category", "Image"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(100);  // Set row height for displaying images
        itemsTable.getColumn("Image").setCellRenderer(new ImageCellRenderer());  // Custom renderer for the Image column
        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

      

        deleteButton = new JButton("Delete Item");
        buttonPanel.add(deleteButton);

        addItemButton = new JButton("Add New Item");
        buttonPanel.add(addItemButton);

        customerDetailsButton = new JButton("Go to Customer Details"); // New button
        buttonPanel.add(customerDetailsButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadItems();

        // Add Item button action
        addItemButton.addActionListener(e -> new AddItemPage(ManageItemsPage.this));

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow != -1) {
                String itemName = (String) itemsTable.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete " + itemName + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Conn conn = new Conn();
                        String query = "DELETE FROM items WHERE item_name = ?";
                        PreparedStatement pst = conn.c.prepareStatement(query);
                        pst.setString(1, itemName);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Item deleted successfully!");
                        loadItems();  // Refresh the table
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please select an item to delete.");
            }
        });

        // Navigate to CustomerDetailsPage
        customerDetailsButton.addActionListener(e -> {
            new CustomerDetailsPage(); // Open CustomerDetailsPage
            this.dispose(); // Close ManageItemsPage
        });

        // Search Button ActionListener
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim();
                filterItems(searchText);
            }
        });

        setVisible(true);
    }

    // Load items into the table
    public void loadItems() {
        try {
            Conn conn = new Conn();
            String query = "SELECT item_name, price, category, image FROM items";
            Statement stmt = conn.c.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            DefaultTableModel tableModel = (DefaultTableModel) itemsTable.getModel();
            tableModel.setRowCount(0);  // Clear existing rows

            while (rs.next()) {
                String itemName = rs.getString("item_name");
                String price = rs.getString("price");
                String category = rs.getString("category");
                ImageIcon image = new ImageIcon(rs.getBytes("image"));  // Retrieve image from database
                Object[] row = {itemName, price, category, image};
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
        }
    }

    // Filter table based on search input
    private void filterItems(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) itemsTable.getModel());
        itemsTable.setRowSorter(sorter);

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 0)); // Case-insensitive search in Item Name column
        }
    }

    // Custom renderer for the Image column
    class ImageCellRenderer extends JLabel implements TableCellRenderer {
        public ImageCellRenderer() {
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof ImageIcon) {
                setIcon((ImageIcon) value);
            } else {
                setText("No Image");
            }
            return this;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageItemsPage::new);
    }
}

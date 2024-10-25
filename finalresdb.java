import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RestaurantOrderManagementSystem extends JFrame {

    // Database connection details (adjust these based on your PostgreSQL setup)
    static final String URL = "jdbc:postgresql://localhost:5432/resdb";
    static final String USER = "postgres";
    static final String PASSWORD = "root";
    static final String MANAGER_PASSWORD = "manju"; // Simple password for manager

    public RestaurantOrderManagementSystem() 
    {
                // Main window setup
                setTitle("Restaurant Management System");
                setSize(300, 150);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLayout(new FlowLayout());

                // Buttons for Manager and Customer
                JButton managerButton = new JButton("Manager Mode");
                JButton customerButton = new JButton("Customer Mode");

                // Add action for manager button
                managerButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String password = JOptionPane.showInputDialog("Enter Manager Password:");
                        if (password != null && password.equals(MANAGER_PASSWORD)) {
                            openManagerPanel();
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect Password!");
                        }
                    }
                });

                // Add action for customer button
                customerButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        openCustomerPanel();
                    }
                });

                // Add buttons to the main frame
                add(managerButton);
                add(customerButton);
                setVisible(true);
    }

    // Manager Panel (basic operations)
    private void openManagerPanel() {
        JFrame managerFrame = new JFrame("Manager Panel");
        managerFrame.setSize(300, 200);
        managerFrame.setLayout(new GridLayout(3, 1));

        // Buttons for Manager actions
        JButton viewMenuButton = new JButton("View Menu");
        JButton addItemButton = new JButton("Add Item");

        // Action to view menu
        viewMenuButton.addActionListener(e -> viewMenu());

        // Action to add a menu item
        addItemButton.addActionListener(e -> addItem());

        // Add buttons to the manager frame
        managerFrame.add(viewMenuButton);
        managerFrame.add(addItemButton);
        managerFrame.setVisible(true);
    }

    // Customer Panel (view menu and place order)
    private void openCustomerPanel() {
        JFrame customerFrame = new JFrame("Customer Panel");
        customerFrame.setSize(300, 150);
        customerFrame.setLayout(new GridLayout(2, 1));

        // Buttons for Customer actions
        JButton viewMenuButton = new JButton("View Menu");
        JButton placeOrderButton = new JButton("Place Order");

        // Action to view menu
        viewMenuButton.addActionListener(e -> viewMenu());

        // Action to place order
        placeOrderButton.addActionListener(e -> placeOrder());

        // Add buttons to the customer frame
        customerFrame.add(viewMenuButton);
        customerFrame.add(placeOrderButton);
        customerFrame.setVisible(true);
    }

    // View Menu (same for both manager and customer)
    private void viewMenu() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT menu_id, item_name, price FROM Menu")) {

            StringBuilder menuList = new StringBuilder("Menu Items:\n");
            while (rs.next()) {
                menuList.append("ID: ").append(rs.getInt("menu_id"))
                        .append(", Name: ").append(rs.getString("item_name"))
                        .append(", Price: ").append(rs.getDouble("price")).append("\n");
            }
            JOptionPane.showMessageDialog(null, menuList.toString());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading menu.");
            e.printStackTrace();
        }
    }

    // Add Menu Item (only for manager)
    private void addItem() {
        String itemName = JOptionPane.showInputDialog("Enter item name:");
        String priceStr = JOptionPane.showInputDialog("Enter item price:");

        if (itemName != null && priceStr != null) {
            double price = Double.parseDouble(priceStr);

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Menu (item_name, price) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setString(1, itemName);
                    pstmt.setDouble(2, price);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Item added successfully.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error adding item.");
                e.printStackTrace();
            }
        }
    }

    // Place Order (for customer)
    private void placeOrder() {
        String menuIdStr = JOptionPane.showInputDialog("Enter Menu ID to order:");
        String quantityStr = JOptionPane.showInputDialog("Enter quantity:");

        if (menuIdStr != null && quantityStr != null) {
            int menuId = Integer.parseInt(menuIdStr);
            int quantity = Integer.parseInt(quantityStr);

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "INSERT INTO Orders (menu_id, quantity) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                    pstmt.setInt(1, menuId);
                    pstmt.setInt(2, quantity);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Order placed successfully.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error placing order.");
                e.printStackTrace();
            }
        }
    }

    // Main method to run the application
    public static void main(String[] args) {
        new RestaurantOrderManagementSystem();
    }
}

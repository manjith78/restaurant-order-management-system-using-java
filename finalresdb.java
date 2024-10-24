import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class finalresdb extends JFrame {

    // Database connection details for PostgreSQL
    static final String URL = "jdbc:postgresql://localhost:5432/resdb";
    static final String USER = "postgres";
    static final String PASSWORD = "root";
    static final String MANAGER_PASSWORD = "manju"; // Example password for manager access

    public finalresdb() {
        // Set up the main window
        setTitle("Restaurant Order Management System");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Add Manager and Customer buttons
        JButton managerButton = new JButton("Manager Mode");
        JButton customerButton = new JButton("Customer Mode");

        // Action for Manager button
        managerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openManagerPanel();
            }
        });

        // Action for Customer button
        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCustomerPanel();
            }
        });

        add(managerButton);
        add(customerButton);
        setVisible(true);
    }

    // Method to open Manager Panel
    private void openManagerPanel() {
        String password = JOptionPane.showInputDialog(this, "Enter Manager Password:");
        if (password != null && password.equals(MANAGER_PASSWORD)) {
            new ManagerPanel().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Password!");
        }
    }

    // Method to open Customer Panel
    private void openCustomerPanel() {
        new CustomerPanel().setVisible(true);
    }

    public static void main(String[] args) {
        new finalresdb();
    }

    // Manager Panel with functionalities
    class ManagerPanel extends JFrame {

        public ManagerPanel() {
            setTitle("Manager Panel");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(6, 1));

            // Buttons for manager functions
            JButton viewMenuButton = new JButton("View Menu");
            JButton addItemButton = new JButton("Add Item");
            JButton updateItemButton = new JButton("Update Item");
            JButton deleteItemButton = new JButton("Delete Item");
            JButton viewOrdersButton = new JButton("View Orders");
            JButton updateOrderStatusButton = new JButton("Update Order Status");

            // Adding functionality for manager actions
            viewMenuButton.addActionListener(e -> viewMenu());
            addItemButton.addActionListener(e -> addItem());
            updateItemButton.addActionListener(e -> updateItem());
            deleteItemButton.addActionListener(e -> deleteItem());
            viewOrdersButton.addActionListener(e -> viewOrders());
            updateOrderStatusButton.addActionListener(e -> updateOrderStatus());

            add(viewMenuButton);
            add(addItemButton);
            add(updateItemButton);
            add(deleteItemButton);
            add(viewOrdersButton);
            add(updateOrderStatusButton);
        }

        // View Menu
        private void viewMenu() {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT menu_id, item_name, price, quantity FROM Menu")) {

                StringBuilder menuList = new StringBuilder("Menu Items:\n");
                while (rs.next()) {
                    menuList.append(String.format("ID: %d, Name: %s, Price: %.2f, Quantity: %d\n",
                            rs.getInt("menu_id"), rs.getString("item_name"),
                            rs.getDouble("price"), rs.getInt("quantity")));
                }
                JOptionPane.showMessageDialog(this, menuList.toString());

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading menu.");
                e.printStackTrace();
            }
        }

        // Add Item
        private void addItem() {
            String name = JOptionPane.showInputDialog(this, "Enter item name:");
            String priceStr = JOptionPane.showInputDialog(this, "Enter price:");
            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity:");

            if (name != null && priceStr != null && quantityStr != null) {
                double price = Double.parseDouble(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String insertQuery = "INSERT INTO Menu (item_name, price, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
                        pstmt.setString(1, name);
                        pstmt.setDouble(2, price);
                        pstmt.setInt(3, quantity);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Item added successfully.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error adding item.");
                    e.printStackTrace();
                }
            }
        }

        // Update Item
        private void updateItem() {
            String idStr = JOptionPane.showInputDialog(this, "Enter item ID to update:");
            String name = JOptionPane.showInputDialog(this, "Enter new item name:");
            String priceStr = JOptionPane.showInputDialog(this, "Enter new price:");
            String quantityStr = JOptionPane.showInputDialog(this, "Enter new quantity:");

            if (idStr != null && name != null && priceStr != null && quantityStr != null) {
                int itemId = Integer.parseInt(idStr);
                double price = Double.parseDouble(priceStr);
                int quantity = Integer.parseInt(quantityStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String updateQuery = "UPDATE Menu SET item_name = ?, price = ?, quantity = ? WHERE menu_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                        pstmt.setString(1, name);
                        pstmt.setDouble(2, price);
                        pstmt.setInt(3, quantity);
                        pstmt.setInt(4, itemId);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Item updated successfully.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating item.");
                    e.printStackTrace();
                }
            }
        }

        // Delete Item
        private void deleteItem() {
            String idStr = JOptionPane.showInputDialog(this, "Enter item ID to delete:");

            if (idStr != null) {
                int itemId = Integer.parseInt(idStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String deleteQuery = "DELETE FROM Menu WHERE menu_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
                        pstmt.setInt(1, itemId);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Item deleted successfully.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting item.");
                    e.printStackTrace();
                }
            }
        }

        // View Orders
        private void viewOrders() {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM Orders")) {

                StringBuilder orderList = new StringBuilder("Orders:\n");
                while (rs.next()) {
                    orderList.append(String.format("Order ID: %d, Menu ID: %d, Quantity: %d, Order Type: %s, Table Number: %d, Time: %s, Status: %s\n",
                            rs.getInt("order_id"), rs.getInt("menu_id"),
                            rs.getInt("quantity"), rs.getString("order_type"),
                            rs.getInt("table_number"), rs.getTimestamp("order_time"),
                            rs.getString("order_status")));
                }
                JOptionPane.showMessageDialog(this, orderList.toString());

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading orders.");
                e.printStackTrace();
            }
        }

        // Update Order Status
        private void updateOrderStatus() {
            String orderIdStr = JOptionPane.showInputDialog(this, "Enter order ID to update:");
            String status = JOptionPane.showInputDialog(this, "Enter new status (delivered/undelivered):");

            if (orderIdStr != null && status != null) {
                int orderId = Integer.parseInt(orderIdStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String updateStatusQuery = "UPDATE Orders SET order_status = ? WHERE order_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateStatusQuery)) {
                        pstmt.setString(1, status);
                        pstmt.setInt(2, orderId);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Order status updated successfully.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating order status.");
                    e.printStackTrace();
                }
            }
        }
    }

    // Customer Panel with functionalities
    class CustomerPanel extends JFrame {

        public CustomerPanel() {
            setTitle("Customer Panel");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridLayout(3, 1));

            // Buttons for customer functions
            JButton viewMenuButton = new JButton("View Menu");
            JButton placeOrderButton = new JButton("Place Order");
            JButton viewMyOrdersButton = new JButton("View My Orders");

            // Adding functionality for customer actions
            viewMenuButton.addActionListener(e -> viewMenu());
            placeOrderButton.addActionListener(e -> placeOrder());
            viewMyOrdersButton.addActionListener(e -> viewMyOrders());

            add(viewMenuButton);
            add(placeOrderButton);
            add(viewMyOrdersButton);
        }

        // View Menu
        private void viewMenu() {
            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT menu_id, item_name, price, quantity FROM Menu")) {

                StringBuilder menuList = new StringBuilder("Menu Items:\n");
                while (rs.next()) {
                    menuList.append(String.format("ID: %d, Name: %s, Price: %.2f, Quantity: %d\n",
                            rs.getInt("menu_id"), rs.getString("item_name"),
                            rs.getDouble("price"), rs.getInt("quantity")));
                }
                JOptionPane.showMessageDialog(this, menuList.toString());

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading menu.");
                e.printStackTrace();
            }
        }

        // Place Order
        private void placeOrder() {
            String menuIdStr = JOptionPane.showInputDialog(this, "Enter Menu ID to order:");
            String quantityStr = JOptionPane.showInputDialog(this, "Enter quantity:");
            String orderType = JOptionPane.showInputDialog(this, "Enter order type (dine-in/takeaway):");
            String tableNumberStr = JOptionPane.showInputDialog(this, "Enter table number:");

            if (menuIdStr != null && quantityStr != null && orderType != null && tableNumberStr != null) {
                int menuId = Integer.parseInt(menuIdStr);
                int quantity = Integer.parseInt(quantityStr);
                int tableNumber = Integer.parseInt(tableNumberStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String insertOrderQuery = "INSERT INTO Orders (menu_id, quantity, order_type, table_number, order_status) VALUES (?, ?, ?, ?, 'undelivered')";
                    try (PreparedStatement pstmt = conn.prepareStatement(insertOrderQuery)) {
                        pstmt.setInt(1, menuId);
                        pstmt.setInt(2, quantity);
                        pstmt.setString(3, orderType);
                        pstmt.setInt(4, tableNumber);
                        pstmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Order placed successfully.");
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error placing order.");
                    e.printStackTrace();
                }
            }
        }

        // View My Orders
        private void viewMyOrders() {
            String tableNumberStr = JOptionPane.showInputDialog(this, "Enter table number to view orders:");

            if (tableNumberStr != null) {
                int tableNumber = Integer.parseInt(tableNumberStr);

                try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
                    String query = "SELECT * FROM Orders WHERE table_number = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setInt(1, tableNumber);
                        ResultSet rs = pstmt.executeQuery();

                        StringBuilder orderList = new StringBuilder("My Orders:\n");
                        while (rs.next()) {
                            orderList.append(String.format("Order ID: %d, Menu ID: %d, Quantity: %d, Order Type: %s, Status: %s\n",
                                    rs.getInt("order_id"), rs.getInt("menu_id"),
                                    rs.getInt("quantity"), rs.getString("order_type"),
                                    rs.getString("order_status")));
                        }
                        JOptionPane.showMessageDialog(this, orderList.toString());

                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error loading your orders.");
                    e.printStackTrace();
                }
            }
        }
    }
}
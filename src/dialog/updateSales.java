/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package dialog;

import connection.MySql;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Chamod
 */
public class updateSales extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(updateSales.class.getName());
    private LinkedHashMap<String, Integer> customerMap;
    private LinkedHashMap<String, Integer> carNameMap;
    private int SaleID;

    /**
     * Creates new form updateSales
     */
    public updateSales(java.awt.Frame parent, boolean modal, int SaleID) {
        super(parent, modal);
        this.SaleID = SaleID;
        setLocationRelativeTo(parent);
        initComponents();
        init();
        loadCombo();
        loadSaleData();
    }

    private void init() {
        ImageIcon icon = new ImageIcon(getClass().getResource("/resource/logo7.png"));
        this.setIconImage(icon.getImage());
        setTitle("CarPlus | Update Sales");
    }

    public void loadCombo() {
        customerMap = new LinkedHashMap<>();
        carNameMap = new LinkedHashMap<>();

        Connection conn = null;
        PreparedStatement pStatement = null;
        PreparedStatement carStatement = null;

        ResultSet rs = null;
        ResultSet carrs = null;

        try {
            conn = MySql.getConnection();
            String query = "SELECT CustomerID, FullName FROM customers ORDER BY FullName";
            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();

            customer.removeAllItems();
            while (rs.next()) {
                int supplierId = rs.getInt("CustomerID");
                String supplierName = rs.getString("FullName");
                customerMap.put(supplierName, supplierId);
                customer.addItem(supplierName);
            }

            String carQuery = "SELECT VehicleID, Make, Model FROM vehicles ORDER BY Make, Model;";
            carStatement = conn.prepareStatement(carQuery);
            carrs = carStatement.executeQuery();

            cars.removeAllItems();
            while (carrs.next()) {
                int carId = carrs.getInt("VehicleID");
                String carBrand = carrs.getString("Make");
                String carModel = carrs.getString("Model");
                String carfName = carBrand + " " + carModel;
                carNameMap.put(carfName, carId);
                cars.addItem(carfName);
            }

            if (customer.getItemCount() == 0) {
                customer.addItem("No Customers Found");
            }

            if (cars.getItemCount() == 0) {
                cars.addItem("No cars Found");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading customers/cars", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading customers/cars", e);
        }
    }

    private void loadSaleData() {
        String query = "SELECT "
                + "s.Date, s.SalePrice, s.VehicleID, s.CustomerID, "
                + "v.Make AS CarMake, v.Model AS CarModel, "
                + "c.FullName AS CustomerName "
                + "FROM sales s "
                + "JOIN vehicles v ON s.VehicleID = v.VehicleID "
                + "JOIN customers c ON s.CustomerID = c.CustomerID "
                + "WHERE s.SaleID = ?;";

        try (Connection conn = MySql.getConnection(); PreparedStatement pStatement = conn.prepareStatement(query)) {

            pStatement.setInt(1, this.SaleID);
            try (ResultSet rs = pStatement.executeQuery()) {
                if (rs.next()) {
                    String currentCarName = rs.getString("CarMake") + " " + rs.getString("CarModel");
                    cars.setSelectedItem(currentCarName);
                    if (!currentCarName.equals(cars.getSelectedItem())) {
                        logger.log(Level.WARNING, "Car not found directly in combo box. Attempting ID lookup.");
                        int currentCarId = rs.getInt("VehicleID");
                        for (java.util.Map.Entry<String, Integer> entry : carNameMap.entrySet()) {
                            if (entry.getValue().equals(currentCarId)) {
                                cars.setSelectedItem(entry.getKey());
                                break;
                            }
                        }
                    }

                    String currentCustomerName = rs.getString("CustomerName");
                    customer.setSelectedItem(currentCustomerName);
                    if (!currentCustomerName.equals(customer.getSelectedItem())) {
                        logger.log(Level.WARNING, "Customer not found directly in combo box. Attempting ID lookup.");
                        int currentCustomerId = rs.getInt("CustomerID");
                        for (java.util.Map.Entry<String, Integer> entry : customerMap.entrySet()) {
                            if (entry.getValue().equals(currentCustomerId)) {
                                customer.setSelectedItem(entry.getKey());
                                break;
                            }
                        }
                    }

                    java.sql.Date saleDate = rs.getDate("Date");
                    if (saleDate != null) {
                        jSpinner1.setValue(new Date(saleDate.getTime()));
                    }

                    jTextField1.setText(String.format("%.2f", rs.getDouble("SalePrice")));

                } else {
                    JOptionPane.showMessageDialog(this, "Sale with ID " + SaleID + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    this.dispose();
                }
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading sale data for update (SaleID: " + SaleID + ")", e);
            JOptionPane.showMessageDialog(this, "Error loading sale data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading sale data for update (SaleID: " + SaleID + ")", e);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            this.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        cars = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        customer = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(224, 225, 221));
        jPanel1.setPreferredSize(new java.awt.Dimension(323, 427));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons8-book-25.png"))); // NOI18N
        jLabel1.setText("Update Sales");

        cars.setBackground(new java.awt.Color(255, 255, 255));
        cars.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        cars.setForeground(new java.awt.Color(0, 0, 0));
        cars.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cars.setPreferredSize(new java.awt.Dimension(72, 38));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Car");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Customer");

        customer.setBackground(new java.awt.Color(255, 255, 255));
        customer.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        customer.setForeground(new java.awt.Color(0, 0, 0));
        customer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        customer.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        customer.setPreferredSize(new java.awt.Dimension(72, 38));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Date");

        jSpinner1.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(1753269606727L), null, null, java.util.Calendar.DAY_OF_MONTH));
        jSpinner1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jSpinner1.setPreferredSize(new java.awt.Dimension(64, 38));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Sale Price");

        jTextField1.setBackground(new java.awt.Color(255, 255, 255));
        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(0, 0, 0));
        jTextField1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jTextField1.setPreferredSize(new java.awt.Dimension(64, 38));

        jButton1.setBackground(new java.awt.Color(27, 38, 59));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Update Sales");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cars, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(customer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jSpinner1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 145, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cars, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(customer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedCarName = (String) cars.getSelectedItem();
        String selectedCustomerName = (String) customer.getSelectedItem();
        Date saleDate = (Date) jSpinner1.getValue();
        String salePriceText = jTextField1.getText().trim();

        if (selectedCarName == null || selectedCarName.equals("No Cars Found")) {
            JOptionPane.showMessageDialog(this, "Please select a car.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedCustomerName == null || selectedCustomerName.equals("No Customers Found")) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (salePriceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the sale price.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double salePrice;
        try {
            salePrice = Double.parseDouble(salePriceText);
            if (salePrice <= 0) {
                JOptionPane.showMessageDialog(this, "Sale price must be a positive number.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid sale price. Please enter a valid number.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer vehicleId = carNameMap.get(selectedCarName);
        Integer customerId = customerMap.get(selectedCustomerName);

        if (vehicleId == null) {
            JOptionPane.showMessageDialog(this, "Could not find selected car's ID. Please try reloading or contact support.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Car ID not found");
            return;
        }
        if (customerId == null) {
            JOptionPane.showMessageDialog(this, "Could not find selected customer's ID. Please try reloading or contact support.", "Internal Error", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Customer ID not found");
            return;
        }

        String updateSQL = "UPDATE sales SET Date = ?, SalePrice = ?, VehicleID = ?, CustomerID = ? WHERE SaleID = ?";

        try (Connection conn = MySql.getConnection(); PreparedStatement pStatement = conn.prepareStatement(updateSQL)) {

            java.sql.Date sqlDate = new java.sql.Date(saleDate.getTime());

            pStatement.setDate(1, sqlDate);
            pStatement.setDouble(2, salePrice);
            pStatement.setInt(3, vehicleId);
            pStatement.setInt(4, customerId);
            pStatement.setInt(5, this.SaleID);

            int rowsAffected = pStatement.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Sale updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update sale. No rows affected (SaleID might not exist or data is identical).", "Error", JOptionPane.ERROR_MESSAGE);
                logger.log(Level.WARNING, "Update sale failed, 0 rows affected for SaleID: ");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error updating sale", e);
            JOptionPane.showMessageDialog(this, "Error updating sale: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error when updating sale", e);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateSales dialog = new updateSales(new javax.swing.JFrame(), true, 1);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cars;
    private javax.swing.JComboBox<String> customer;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}

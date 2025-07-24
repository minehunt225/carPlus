/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package panel;

import connection.MySql;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 *
 * @author Chamod
 */
public class purchases extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(purchases.class.getName()); // Corrected Logger initialization
    private LinkedHashMap<String, Integer> supplierMap;
    private LinkedHashMap<String, Integer> carNameMap;

    /**
     * Creates new form purchases
     */
    public purchases() {
        initComponents();
        loadPurchaseData();
        loadCombo();
    }

    public void loadCombo() {
        supplierMap = new LinkedHashMap<>();
        carNameMap = new LinkedHashMap<>();
        Connection conn = null;
        PreparedStatement supplierStatement = null;
        PreparedStatement carStatement = null;

        ResultSet supplierrs = null;
        ResultSet carrs = null;

        try {
            conn = MySql.getConnection();
            String supplierquery = "SELECT SupplierID, CompanyName FROM suppliers ORDER BY CompanyName";
            supplierStatement = conn.prepareStatement(supplierquery);
            supplierrs = supplierStatement.executeQuery();

            supplier.removeAllItems();
            while (supplierrs.next()) {
                int supplierId = supplierrs.getInt("SupplierID");
                String supplierName = supplierrs.getString("CompanyName");
                supplierMap.put(supplierName, supplierId);
                supplier.addItem(supplierName);
            }
            String carQuery = "SELECT VehicleID, Make, Model FROM vehicles ORDER BY Make, Model;";
            carStatement = conn.prepareStatement(carQuery);
            carrs = carStatement.executeQuery();

            carName.removeAllItems();
            while (carrs.next()) {
                int carId = carrs.getInt("VehicleID");
                String carBrand = carrs.getString("Make");
                String carModel = carrs.getString("Model");
                String carfName = carBrand + " " + carModel;
                carNameMap.put(carfName, carId);
                carName.addItem(carfName);
            }

            if (supplier.getItemCount() == 0) {
                supplier.addItem("No Suppliers Found");
            }

            if (carName.getItemCount() == 0) {
                carName.addItem("No Cars Found");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading suppliers/Cars", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading suppliers/Cars", e);
        } finally {
            try {
                if (supplierrs != null) {
                    supplierrs.close();
                }
                if (carrs != null) {
                    carrs.close();
                }
                if (supplierStatement != null) {
                    supplierStatement.close();
                }
                if (carStatement != null) {
                    carStatement.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error closing database resources after loading suppliers", ex);
            }
        }
    }

    public void loadPurchaseData() {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;

        DefaultTableModel tModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tModel.addColumn("Purchase ID"); 
        tModel.addColumn("Car"); 
        tModel.addColumn("Date");
        tModel.addColumn("Supplier");
        tModel.addColumn("Quantity");
        tModel.addColumn("Price"); 

        try {
            conn = connection.MySql.getConnection();
            String query = "SELECT "
                    + "p.PurchaseID, " 
                    + "v.Make AS CarMake, "
                    + "v.Model AS CarModel, "
                    + "s.CompanyName AS SupplierCompany, "
                    + "p.quantity AS PurchasedQuantity, "
                    + "p.Date AS PurchaseDate, "
                    + "p.PurchasePrice AS Price "
                    + "FROM purchases p "
                    + "JOIN vehicles v ON p.VehicleID = v.VehicleID "
                    + "JOIN suppliers s ON p.SupplierID = s.SupplierID "
                    + "ORDER BY p.Date DESC, p.PurchaseID DESC;";

            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 

            while (rs.next()) {
                int purchaseId = rs.getInt("PurchaseID");
                String carMake = rs.getString("CarMake");
                String carModel = rs.getString("CarModel");
                String supplierContact = rs.getString("SupplierCompany"); 
                int purchasedQuantity = rs.getInt("PurchasedQuantity"); 
                java.sql.Date purchaseDate = rs.getDate("PurchaseDate");
                double transactionPrice = rs.getDouble("Price"); 

                String carName = carMake + " " + carModel;
                String formattedDate = (purchaseDate != null) ? dateFormat.format(purchaseDate) : "N/A";

                tModel.addRow(new Object[]{
                    purchaseId,
                    carName,
                    formattedDate,
                    supplierContact,
                    purchasedQuantity,
                    String.format("%,.2f", transactionPrice)
                });
            }

            jTable1.setModel(tModel);

            if (tModel.getRowCount() == 0) {
                tModel.setColumnCount(0);
                tModel.addColumn("Purchase ID");
                tModel.addColumn("Car");
                tModel.addColumn("Date");
                tModel.addColumn("Supplier");
                tModel.addColumn("Quantity");
                tModel.addColumn("Price");
                tModel.addRow(new Object[]{"", "", "No Purchases Recorded", "", "", ""});
                jTable1.setModel(tModel); 
            }

            jTable1.setShowGrid(true);
            jTable1.setGridColor(new Color(200, 200, 200));
            jTable1.setRowHeight(30);

            JTableHeader header = jTable1.getTableHeader();
            header.setBackground(new Color(65, 90, 119));
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Segoe UI", Font.BOLD, 15));
            header.setPreferredSize(new Dimension(header.getWidth(), 35));

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

            for (int i = 0; i < jTable1.getColumnCount(); i++) {
                jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }

            jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting()) { 
                        selectedRow();
                    }
                }
            });

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading purchase data", e);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading purchase data", e);

        }
    }

    private void selectedRow() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow >= 0) { 
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

            String car = (String) model.getValueAt(selectedRow, 1);
            String supplierName = (String) model.getValueAt(selectedRow, 3);
            int quantity = (int) model.getValueAt(selectedRow, 4);
            String priceStr = (String) model.getValueAt(selectedRow, 5);
            double currentPrice;
            try {
                currentPrice = Double.parseDouble(priceStr.replace(",", ""));
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Could not parse price from table: " + priceStr, e);
                currentPrice = 0.0;
            }

            carName.setSelectedItem(car);
            supplier.setSelectedItem(supplierName);
            qty.setValue(quantity);
            price.setValue(currentPrice);
        } else {
            qty.setValue(0);
            price.setValue(0.0);
            if (carName.getItemCount() > 0) {
                carName.setSelectedIndex(0);
            }
            if (supplier.getItemCount() > 0) {
                supplier.setSelectedIndex(0);
            }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        carName = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        supplier = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        qty = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        price = new javax.swing.JSpinner();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(224, 225, 221));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 700));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons8-open-box-25.png"))); // NOI18N
        jLabel1.setText("Our Purchases");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Car Name");

        carName.setBackground(new java.awt.Color(255, 255, 255));
        carName.setForeground(new java.awt.Color(0, 0, 0));
        carName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        carName.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        carName.setPreferredSize(new java.awt.Dimension(70, 30));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Supplier");

        supplier.setBackground(new java.awt.Color(255, 255, 255));
        supplier.setForeground(new java.awt.Color(0, 0, 0));
        supplier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        supplier.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        supplier.setPreferredSize(new java.awt.Dimension(70, 30));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Quantity");
        jLabel4.setPreferredSize(new java.awt.Dimension(37, 25));

        qty.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        qty.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        qty.setPreferredSize(new java.awt.Dimension(70, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 0, 0));
        jLabel5.setText("Price");

        price.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        price.setPreferredSize(new java.awt.Dimension(70, 30));

        jButton1.setBackground(new java.awt.Color(27, 38, 59));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Add New Purchase");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton1.setPreferredSize(new java.awt.Dimension(75, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(119, 141, 169));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 0, 0));
        jButton2.setText("Updata Purchase");
        jButton2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jButton2.setPreferredSize(new java.awt.Dimension(75, 30));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(carName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(supplier, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(qty, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(price, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 99, Short.MAX_VALUE))
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 599, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 614, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(carName, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(supplier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(qty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(price, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedCarName = (String) carName.getSelectedItem();
        String selectedSupplierName = (String) supplier.getSelectedItem();
        int quantity = (Integer) qty.getValue();
        double purchasePrice = ((Number) price.getValue()).doubleValue();

        if (selectedCarName == null || selectedCarName.equals("No Cars Found")) {
            JOptionPane.showMessageDialog(this, "Please select a valid car.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedSupplierName == null || selectedSupplierName.equals("No Suppliers Found")) {
            JOptionPane.showMessageDialog(this, "Please select a valid supplier.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (purchasePrice <= 0) {
            JOptionPane.showMessageDialog(this, "Purchase price must be greater than 0.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer vehicleId = carNameMap.get(selectedCarName);
        Integer supplierId = supplierMap.get(selectedSupplierName);

        Connection conn = null;
        PreparedStatement insertPStatement = null;
        PreparedStatement updateQtyStatement = null;

        try {
            conn = MySql.getConnection();
            conn.setAutoCommit(false);

            String insertPurchaseQuery = "INSERT INTO purchases (Date, VehicleID, SupplierID, PurchasePrice, quantity) VALUES (?, ?, ?, ?, ?)";
            insertPStatement = conn.prepareStatement(insertPurchaseQuery);
            insertPStatement.setDate(1, new java.sql.Date(new Date().getTime())); // Current date
            insertPStatement.setInt(2, vehicleId);
            insertPStatement.setInt(3, supplierId);
            insertPStatement.setDouble(4, purchasePrice);
            insertPStatement.setInt(5, quantity);

            int rowsAffectedPurchase = insertPStatement.executeUpdate();

            if (rowsAffectedPurchase > 0) {
                String updateVehicleQuery = "UPDATE vehicles SET quantity = quantity + ? WHERE VehicleID = ?";
                updateQtyStatement = conn.prepareStatement(updateVehicleQuery);
                updateQtyStatement.setInt(1, quantity);
                updateQtyStatement.setInt(2, vehicleId);

                int rowsAffectedVehicle = updateQtyStatement.executeUpdate();

                if (rowsAffectedVehicle > 0) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Purchase added successfully and vehicle quantity updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPurchaseData();
                    qty.setValue(0);
                    price.setValue(0.0);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update vehicle quantity. Purchase rolled back.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error adding new purchase", e);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a purchase to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        int purchaseId = (int) model.getValueAt(selectedRow, 0); // Assuming PurchaseID is in the first column

        String selectedCarName = (String) carName.getSelectedItem();
        String selectedSupplierName = (String) supplier.getSelectedItem();
        int newQuantity = (Integer) qty.getValue();
        double newPurchasePrice = ((Number) price.getValue()).doubleValue();

        if (selectedCarName == null || selectedCarName.equals("No Cars Found")
                || selectedSupplierName == null || selectedSupplierName.equals("No Suppliers Found")
                || newQuantity <= 0 || newPurchasePrice <= 0) {
            JOptionPane.showMessageDialog(this, "Please ensure all fields are valid: select a car and supplier, and enter positive quantity and price.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer newVehicleId = carNameMap.get(selectedCarName);
        Integer newSupplierId = supplierMap.get(selectedSupplierName);

        Connection conn = null;
        PreparedStatement getOldDataStatement = null;
        PreparedStatement updatePurchaseStatement = null;
        PreparedStatement updateVehicleQuantityStatement = null; 
        ResultSet rs = null;

        try {
            conn = MySql.getConnection();

            String getOldDataQuery = "SELECT VehicleID, quantity FROM purchases WHERE PurchaseID = ?";
            getOldDataStatement = conn.prepareStatement(getOldDataQuery);
            getOldDataStatement.setInt(1, purchaseId);
            rs = getOldDataStatement.executeQuery();

            int oldVehicleId = -1;
            int oldQuantity = 0;
            if (rs.next()) { 
                oldVehicleId = rs.getInt("VehicleID");
                oldQuantity = rs.getInt("quantity");
            } else {
                JOptionPane.showMessageDialog(this, "Original purchase record not found for update. No update performed.", "Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
            rs.close();
            getOldDataStatement.close();

            String updatePurchaseQuery = "UPDATE purchases SET Date = ?, VehicleID = ?, SupplierID = ?, PurchasePrice = ?, quantity = ? WHERE PurchaseID = ?";
            updatePurchaseStatement = conn.prepareStatement(updatePurchaseQuery);
            updatePurchaseStatement.setDate(1, new java.sql.Date(new Date().getTime()));
            updatePurchaseStatement.setInt(2, newVehicleId);
            updatePurchaseStatement.setInt(3, newSupplierId);
            updatePurchaseStatement.setDouble(4, newPurchasePrice);
            updatePurchaseStatement.setInt(5, newQuantity);
            updatePurchaseStatement.setInt(6, purchaseId);

            int rowsAffectedPurchase = updatePurchaseStatement.executeUpdate();

            if (rowsAffectedPurchase > 0) {
                if (oldVehicleId == newVehicleId) {
                    int quantityDifference = newQuantity - oldQuantity;
                    String updateSameVehicleQuery = "UPDATE vehicles SET quantity = quantity + ? WHERE VehicleID = ?";
                    updateVehicleQuantityStatement = conn.prepareStatement(updateSameVehicleQuery);
                    updateVehicleQuantityStatement.setInt(1, quantityDifference);
                    updateVehicleQuantityStatement.setInt(2, newVehicleId);
                    int rowsAffectedVehicle = updateVehicleQuantityStatement.executeUpdate();
                    if (rowsAffectedVehicle > 0) {
                        JOptionPane.showMessageDialog(this, "Purchase updated successfully and vehicle quantity adjusted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Purchase updated, but failed to adjust vehicle quantity. Data might be inconsistent.", "Warning", JOptionPane.WARNING_MESSAGE);
                        logger.log(Level.WARNING, "Failed to adjust vehicle quantity for purchase ID: " + purchaseId);
                    }
                } else {

                    String decreaseOldVehicleQuery = "UPDATE vehicles SET quantity = quantity - ? WHERE VehicleID = ?";
                    updateVehicleQuantityStatement = conn.prepareStatement(decreaseOldVehicleQuery);
                    updateVehicleQuantityStatement.setInt(1, oldQuantity);
                    updateVehicleQuantityStatement.setInt(2, oldVehicleId);
                    int oldRowsAffected = updateVehicleQuantityStatement.executeUpdate();
                    updateVehicleQuantityStatement.close(); 

                    String increaseNewVehicleQuery = "UPDATE vehicles SET quantity = quantity + ? WHERE VehicleID = ?";
                    updateVehicleQuantityStatement = conn.prepareStatement(increaseNewVehicleQuery);
                    updateVehicleQuantityStatement.setInt(1, newQuantity);
                    updateVehicleQuantityStatement.setInt(2, newVehicleId);
                    int newRowsAffected = updateVehicleQuantityStatement.executeUpdate();

                    if (oldRowsAffected > 0 && newRowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Purchase updated successfully and vehicle quantities adjusted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Purchase updated, but failed to fully adjust vehicle quantities. Data might be inconsistent.", "Warning", JOptionPane.WARNING_MESSAGE);
                        logger.log(Level.WARNING, "Failed to adjust old/new vehicle quantities for purchase ID: " + purchaseId);
                    }
                }
                loadPurchaseData(); 
                qty.setValue(0);
                price.setValue(0.0);
                if (carName.getItemCount() > 0) {
                    carName.setSelectedIndex(0);
                }
                if (supplier.getItemCount() > 0) {
                    supplier.setSelectedIndex(0);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Failed to update purchase. No record changed.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error editing purchase", e);
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> carName;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JSpinner price;
    private javax.swing.JSpinner qty;
    private javax.swing.JComboBox<String> supplier;
    // End of variables declaration//GEN-END:variables
}

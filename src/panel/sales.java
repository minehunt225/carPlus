/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import connection.MySql;
import dialog.addSales;
import dialog.updateSales;
import gui.Home;

/**
 *
 * @author Chamod
 */
public class sales extends javax.swing.JPanel {

    private static final Logger logger = Logger.getLogger(sales.class.getName());
    private final Home homeScreen;

    /**
     * Creates new form sales
     */
    public sales(Home parent) {
        initComponents();
        loadData();
        loadSaleData();
        addTableSelectionListener();
        this.homeScreen = parent;
    }

    private void loadData() {
        Connection conn = null;

        PreparedStatement pSalesStatement = null;
        PreparedStatement pIncomeStatement = null;

        ResultSet sale_rs = null;
        ResultSet income_rs = null;

        try {
            conn = connection.MySql.getConnection();
            String totalSales = "SELECT COUNT(SaleID) AS TotalSales FROM sales";
            String totalIncome = "SELECT SUM(SalePrice) AS TotalIncome FROM sales;";
            pSalesStatement = conn.prepareStatement(totalSales);
            pIncomeStatement = conn.prepareStatement(totalIncome);
            sale_rs = pSalesStatement.executeQuery();
            income_rs = pIncomeStatement.executeQuery();

            if (sale_rs.next()) {
                int TotalCars = sale_rs.getInt("TotalSales");
                total_sales.setText(String.valueOf(TotalCars));
            } else {
                total_sales.setText("N/A");
            }

            if (income_rs.next()) {
                double TotalIncome = income_rs.getDouble("TotalIncome");
                total_income.setText(String.format("%,.2f", TotalIncome));
            } else {
                total_income.setText("N/A");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading Data", e);
            total_sales.setText("Error");
            total_income.setText("Error");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading Data", e);
            total_sales.setText("Error");
            total_income.setText("Error");
        }
    }

    public void loadSaleData() {
        Connection conn = null;
        PreparedStatement pStatement = null;
        ResultSet rs = null;

        DefaultTableModel tModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tModel.addColumn("Sale Id");
        tModel.addColumn("Date");
        tModel.addColumn("Car");
        tModel.addColumn("Customer");
        tModel.addColumn("Customer Mobilr");
        tModel.addColumn("Sale Price");

        try {
            conn = connection.MySql.getConnection();
            String query = "SELECT "
                    + "s.SaleID, "
                    + "s.Date AS SaleDate, "
                    + "s.SalePrice AS Price, "
                    + "v.Make AS CarMake, "
                    + "v.Model AS CarModel, "
                    + "c.FullName AS CustomerName, "
                    + "c.Phone AS CustomerMobile " // Corrected alias as per your database schema
                    + "FROM sales s "
                    + "JOIN vehicles v ON s.VehicleID = v.VehicleID "
                    + "JOIN customers c ON s.CustomerID = c.CustomerID "
                    + "ORDER BY s.Date DESC, s.SaleID DESC;";

            pStatement = conn.prepareStatement(query);
            rs = pStatement.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            while (rs.next()) {
                int saleId = rs.getInt("SaleID");
                java.sql.Date saleDate = rs.getDate("SaleDate");
                String carMake = rs.getString("CarMake");
                String carModel = rs.getString("CarModel");
                String customerName = rs.getString("CustomerName");
                String customerContact = rs.getString("CustomerMobile");
                double salePrice = rs.getDouble("Price");

                String carName = carMake + " " + carModel;
                String formattedDate = (saleDate != null) ? dateFormat.format(saleDate) : "N/A";

                tModel.addRow(new Object[]{
                    saleId,
                    formattedDate, carName,
                    customerName,
                    customerContact,
                    String.format("%,.2f", salePrice)
                });
            }

            jTable1.setModel(tModel);

            if (tModel.getRowCount() == 0) {
                tModel.setColumnCount(0);
                tModel.addColumn("Sale Id");
                tModel.addColumn("Date");
                tModel.addColumn("Car");
                tModel.addColumn("Customer");
                tModel.addColumn("Customer Mobilr");
                tModel.addColumn("Sale Price");
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

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error loading sale data", e);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error loading sale data", e);

        }
    }

    private void addTableSelectionListener() {
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                    int selectedRow = jTable1.getSelectedRow();
                    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                    if (model.getRowCount() == 1 && "No Sales Recorded".equals(model.getValueAt(0, 2))) {
                        return;
                    }

                    Object saleIdObj = model.getValueAt(selectedRow, 0);
                    if (saleIdObj instanceof Integer) {
                        int saleId = (Integer) saleIdObj;
                        openUpdateDialog(saleId);
                    }
                }
            }
        });
    }

    private void openUpdateDialog(int saleId) {
        if (homeScreen != null) {
            updateSales dialog = new updateSales(homeScreen, true, saleId);
            dialog.setVisible(true);

            loadData();
            loadSaleData();
        } else {
            logger.log(Level.WARNING, "homeScreen is null. Cannot open viewSaleDetails dialog.");
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
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        total_sales = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        total_income = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(224, 225, 221));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 700));

        jPanel2.setBackground(new java.awt.Color(119, 141, 169));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Total Sales");

        total_sales.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N
        total_sales.setForeground(new java.awt.Color(0, 0, 0));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons8-book-75.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(total_sales, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(31, 31, 31))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 17, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(total_sales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(119, 141, 169));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons8-money-75.png"))); // NOI18N

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 0, 0));
        jLabel4.setText("Total Income");

        total_income.setFont(new java.awt.Font("Segoe UI", 1, 34)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(total_income, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(total_income, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addContainerGap())
        );

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

        jButton1.setBackground(new java.awt.Color(27, 38, 59));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Add Sale");
        jButton1.setPreferredSize(new java.awt.Dimension(76, 30));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resource/icons8-book-25.png"))); // NOI18N
        jLabel1.setText("Sales");

        jTextField1.setText("jTextField1");
        jTextField1.setPreferredSize(new java.awt.Dimension(71, 30));

        jButton2.setBackground(new java.awt.Color(27, 38, 59));
        jButton2.setText("Generate Invoice");
        jButton2.setPreferredSize(new java.awt.Dimension(75, 30));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 859, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
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
        if (homeScreen != null) {
            addSales addCarDialog = new addSales(homeScreen, true);
            addCarDialog.setLocationRelativeTo(homeScreen);
            addCarDialog.setVisible(true);

            loadData();
            loadSaleData();
        } else {
            logger.log(Level.WARNING, "homeScreen is null. Cannot open addCar dialog.");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel total_income;
    private javax.swing.JLabel total_sales;
    // End of variables declaration//GEN-END:variables
}

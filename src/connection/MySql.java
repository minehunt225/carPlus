/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package connection;

/**
 *
 * @author Chamod
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySql {

    // IMPORTANT: Replace with your actual database URL, username, and password
    private static final String DB = "jdbc:mysql://localhost:3306/carplausdb1";
    private static final String user = "root";
    private static final String password = "*******";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Make sure it's in your classpath.");
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        Connection conn = DriverManager.getConnection(DB, user, password);
        return conn;
    }
}

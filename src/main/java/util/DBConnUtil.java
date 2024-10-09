package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnUtil {
    private static final String URL = "";
    private static final String USER = "";
    private static final String PASSWORD = "";


    public static Connection getDBConn() throws SQLException {
        try {

            Class.forName("");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Connection Failed! Check output console");
            e.printStackTrace();
            return null;
        }
    }
}

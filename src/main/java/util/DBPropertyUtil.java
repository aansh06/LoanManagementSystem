package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DBPropertyUtil {

     public static String getConnectionString(String propertyFileName) throws IOException {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(propertyFileName)) {
            properties.load(fis);
        }

        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

         String connectionString = url + "?user=" + username + "&password=" + password;

        return connectionString;
    }
}


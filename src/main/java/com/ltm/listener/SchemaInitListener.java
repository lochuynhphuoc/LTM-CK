package com.ltm.listener;

import com.ltm.dao.DatabaseConnection;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.Statement;

@WebListener
public class SchemaInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("SchemaInitListener: Checking and updating database schema...");
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Update 'keyword' column to LONGTEXT to support large file content
            // Using try-catch for each statement to avoid stopping if column already
            // exists/modified (though ALTER usually works fine)
            try {
                String sql1 = "ALTER TABLE tasks MODIFY COLUMN keyword LONGTEXT";
                stmt.executeUpdate(sql1);
                System.out.println("SchemaInitListener: Successfully updated 'keyword' column to LONGTEXT.");
            } catch (Exception e) {
                System.out.println("SchemaInitListener: Warning updating 'keyword' column: " + e.getMessage());
            }

            try {
                String sql2 = "ALTER TABLE tasks MODIFY COLUMN url LONGTEXT";
                stmt.executeUpdate(sql2);
                System.out.println("SchemaInitListener: Successfully updated 'url' column to LONGTEXT.");
            } catch (Exception e) {
                System.out.println("SchemaInitListener: Warning updating 'url' column: " + e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}

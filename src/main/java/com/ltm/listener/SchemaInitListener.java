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

            // Ensure LONGTEXT columns exist for plagiarism payloads
            try {
                String sql1 = "ALTER TABLE tasks MODIFY COLUMN source_content LONGTEXT";
                stmt.executeUpdate(sql1);
                System.out.println("SchemaInitListener: source_content column verified.");
            } catch (Exception e) {
                System.out.println("SchemaInitListener: Warning updating source_content: " + e.getMessage());
            }

            try {
                String sql2 = "ALTER TABLE tasks MODIFY COLUMN target_content LONGTEXT";
                stmt.executeUpdate(sql2);
                System.out.println("SchemaInitListener: target_content column verified.");
            } catch (Exception e) {
                System.out.println("SchemaInitListener: Warning updating target_content: " + e.getMessage());
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

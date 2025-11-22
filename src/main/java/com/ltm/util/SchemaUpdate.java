package com.ltm.util;

import com.ltm.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdate {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Ensure the tasks table stores full text for both source/target content
            String updateSource = "ALTER TABLE tasks MODIFY COLUMN source_content LONGTEXT";
            String updateTarget = "ALTER TABLE tasks MODIFY COLUMN target_content LONGTEXT";

            stmt.executeUpdate(updateSource);
            stmt.executeUpdate(updateTarget);

            System.out.println("Successfully verified content columns.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

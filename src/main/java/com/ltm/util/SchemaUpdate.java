package com.ltm.util;

import com.ltm.dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;

public class SchemaUpdate {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement()) {

            // Modify keyword column to TEXT to hold long URLs
            String sql = "ALTER TABLE tasks MODIFY COLUMN keyword TEXT";
            stmt.executeUpdate(sql);

            System.out.println("Successfully updated 'keyword' column to TEXT type.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

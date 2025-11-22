<%@ page import="com.ltm.dao.DatabaseConnection" %>
    <%@ page import="java.sql.*" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <html>

            <head>
                <title>Database Update</title>
            </head>

            <body>
                <h2>Database Schema Update</h2>
                <% try (Connection conn=DatabaseConnection.getConnection(); Statement stmt=conn.createStatement()) { //
                    Alter 'keyword' column to TEXT to hold file content String
                    sql1="ALTER TABLE tasks MODIFY COLUMN keyword LONGTEXT" ; stmt.executeUpdate(sql1); out.println("<p
                    style='color:green'>Successfully updated 'keyword' column to LONGTEXT.</p>");

                    // Alter 'url' column to TEXT to hold file content (just in case)
                    String sql2 = "ALTER TABLE tasks MODIFY COLUMN url LONGTEXT";
                    stmt.executeUpdate(sql2);
                    out.println("<p style='color:green'>Successfully updated 'url' column to LONGTEXT.</p>");

                    } catch (Exception e) {
                    out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
                    e.printStackTrace(new java.io.PrintWriter(out));
                    }
                    %>
                    <br>
                    <a href="dashboard">Go back to Dashboard</a>
            </body>

            </html>
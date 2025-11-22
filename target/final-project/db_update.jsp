<%@ page import="com.ltm.dao.DatabaseConnection" %>
    <%@ page import="java.sql.*" %>
        <%@ page contentType="text/html;charset=UTF-8" language="java" %>
            <html>

            <head>
                <title>Database Update</title>
            </head>

            <body>
                <h2>Database Schema Update</h2>
                <% try (Connection conn=DatabaseConnection.getConnection(); Statement stmt=conn.createStatement()) {
                    String sql1="ALTER TABLE tasks MODIFY COLUMN source_content LONGTEXT";
                    stmt.executeUpdate(sql1);
                    out.println("<p style='color:green'>source_content column confirmed as LONGTEXT.</p>");

                    String sql2="ALTER TABLE tasks MODIFY COLUMN target_content LONGTEXT";
                    stmt.executeUpdate(sql2);
                    out.println("<p style='color:green'>target_content column confirmed as LONGTEXT.</p>");

                    } catch (Exception e) {
                    out.println("<p style='color:red'>Error: " + e.getMessage() + "</p>");
                    e.printStackTrace(new java.io.PrintWriter(out));
                    }
                    %>
                    <br>
                    <a href="dashboard">Go back to Dashboard</a>
            </body>

            </html>
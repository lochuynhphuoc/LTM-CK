package com.ltm.controller;

import com.ltm.dao.TaskDAO;
import com.ltm.model.Task;
import com.ltm.model.User;
import com.ltm.worker.TaskQueue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
@javax.servlet.annotation.MultipartConfig
public class TaskServlet extends HttpServlet {
    private TaskDAO taskDAO = new TaskDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        List<Task> tasks = taskDAO.getTasksByUserId(user.getId());
        request.setAttribute("tasks", tasks);

        String mode = request.getParameter("mode");
        if ("partial".equals(mode)) {
            request.getRequestDispatcher("taskList.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        Part sourcePart = request.getPart("sourceFile");
        String topic = request.getParameter("topic");

        if (sourcePart != null && topic != null) {
            String sourceContent = extractContent(sourcePart);

            Task task = new Task();
            task.setUserId(user.getId());
            task.setTopic(topic);
            task.setSourceContent(sourceContent);
            task.setTargetContent("Processing corpus comparisons...");
            task.setComparisonDetails("Waiting for comparison results...");
            task.setStatus("PENDING");
            task.setResult(0);
            task.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));

            int taskId = taskDAO.addTask(task);
            if (taskId != -1) {
                task.setId(taskId);
                TaskQueue.getInstance().addTask(task);
            }
        }

        response.sendRedirect("dashboard");
    }

    private String extractContent(Part part) throws IOException {
        String fileName = part.getSubmittedFileName();
        if (fileName != null && fileName.toLowerCase().endsWith(".docx")) {
            try (java.io.InputStream is = part.getInputStream();
                    org.apache.poi.xwpf.usermodel.XWPFDocument doc = new org.apache.poi.xwpf.usermodel.XWPFDocument(
                            is)) {
                StringBuilder text = new StringBuilder();
                for (org.apache.poi.xwpf.usermodel.XWPFParagraph p : doc.getParagraphs()) {
                    text.append(p.getText()).append("\n");
                }
                return text.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error reading .docx file: " + e.getMessage();
            }
        } else {
            // Default to text/plain
            return new String(part.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}

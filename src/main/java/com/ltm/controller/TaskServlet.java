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
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Support Vietnamese
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        // Handle File Uploads
        Part sourcePart = request.getPart("sourceFile");
        Part targetPart = request.getPart("targetFile");

        System.out.println("Received upload request.");

        String sourceContent = extractContent(sourcePart);
        String targetContent = extractContent(targetPart);

        System.out.println("Source content length: " + sourceContent.length());
        System.out.println("Target content length: " + targetContent.length());

        Task task = new Task();
        task.setUserId(user.getId());
        task.setSourceContent(sourceContent); // store original text
        task.setTargetContent(targetContent); // store suspected text

        int taskId = taskDAO.addTask(task);
        System.out.println("Task added to DB with ID: " + taskId);

        if (taskId != -1) {
            task.setId(taskId);
            // Add to Background Queue
            TaskQueue.getInstance().addTask(task);
            System.out.println("Task added to Queue.");
        } else {
            System.out.println("Failed to add task to DB.");
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

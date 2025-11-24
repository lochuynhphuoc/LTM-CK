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

            // Find the best match in the corpus
            String corpusPath = getServletContext().getRealPath("/WEB-INF/corpus/" + topic);
            java.io.File corpusDir = new java.io.File(corpusPath);
            String bestMatchContent = "No matching content found in corpus.";
            double maxSimilarity = 0.0;
            String bestMatchFilename = "";

            if (corpusDir.exists() && corpusDir.isDirectory()) {
                java.io.File[] files = corpusDir.listFiles();
                if (files != null) {
                    for (java.io.File file : files) {
                        if (file.isFile()) {
                            try {
                                String fileContent = new String(java.nio.file.Files.readAllBytes(file.toPath()),
                                        java.nio.charset.StandardCharsets.UTF_8);

                                // Simple Jaccard similarity for initial check
                                java.util.Set<String> sourceWords = new java.util.HashSet<>(
                                        java.util.Arrays.asList(sourceContent.toLowerCase().split("\\s+")));
                                java.util.Set<String> targetWords = new java.util.HashSet<>(
                                        java.util.Arrays.asList(fileContent.toLowerCase().split("\\s+")));
                                java.util.Set<String> intersection = new java.util.HashSet<>(sourceWords);
                                intersection.retainAll(targetWords);

                                double union = sourceWords.size() + targetWords.size() - intersection.size();
                                double similarity = (union == 0) ? 0 : (double) intersection.size() / union;

                                if (similarity > maxSimilarity) {
                                    maxSimilarity = similarity;
                                    bestMatchContent = fileContent;
                                    bestMatchFilename = file.getName();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (!bestMatchFilename.isEmpty()) {
                bestMatchContent = "Matched with: " + bestMatchFilename + "\n\n" + bestMatchContent;
            } else if (maxSimilarity == 0.0 && corpusDir.exists() && corpusDir.listFiles() != null
                    && corpusDir.listFiles().length > 0) {
                // Fallback: if no similarity found (e.g. empty files), just pick the first one
                // to avoid empty target
                try {
                    java.io.File firstFile = corpusDir.listFiles()[0];
                    bestMatchContent = "Matched with: " + firstFile.getName() + " (Fallback)\n\n"
                            + new String(java.nio.file.Files.readAllBytes(firstFile.toPath()),
                                    java.nio.charset.StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Task task = new Task();
            task.setUserId(user.getId());
            task.setSourceContent(sourceContent);
            task.setTargetContent(bestMatchContent);
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

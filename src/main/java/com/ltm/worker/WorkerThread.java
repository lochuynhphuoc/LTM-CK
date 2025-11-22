package com.ltm.worker;

import com.ltm.dao.TaskDAO;
import com.ltm.model.Task;
// import org.jsoup.Jsoup;
// import org.jsoup.nodes.Document;

// import java.io.IOException;

public class WorkerThread extends Thread {
    private boolean running = true;
    private TaskDAO taskDAO = new TaskDAO();

    @Override
    public void run() {
        System.out.println("Worker Thread started...");
        while (running) {
            try {
                Task task = TaskQueue.getInstance().takeTask();
                System.out.println("Processing task ID: " + task.getId());

                // Update status to PROCESSING
                taskDAO.updateTaskStatus(task.getId(), "PROCESSING", 0);

                // Perform plagiarism check using the stored contents
                int similarity = checkPlagiarism(task.getSourceContent(), task.getTargetContent());

                // Update status to COMPLETED
                taskDAO.updateTaskStatus(task.getId(), "COMPLETED", similarity);
                System.out.println("Task ID " + task.getId() + " completed. Similarity: " + similarity + "%");

            } catch (InterruptedException e) {
                System.out.println("Worker Thread interrupted.");
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
                // Ideally handle failure status here
            }
        }
    }

    public void stopWorker() {
        running = false;
        this.interrupt();
    }

    private int checkPlagiarism(String text1, String text2) {
        try {
            // In this new version, text1 and text2 ARE the content, not URLs
            // So we don't need Jsoup.connect() anymore.

            String lowerText1 = text1.toLowerCase();
            String lowerText2 = text2.toLowerCase();

            // Tokenize (split by non-word characters)
            java.util.Set<String> words1 = new java.util.HashSet<>(java.util.Arrays.asList(lowerText1.split("\\W+")));
            java.util.Set<String> words2 = new java.util.HashSet<>(java.util.Arrays.asList(lowerText2.split("\\W+")));

            // Calculate Jaccard Similarity
            // Intersection
            java.util.Set<String> intersection = new java.util.HashSet<>(words1);
            intersection.retainAll(words2);

            // Union
            java.util.Set<String> union = new java.util.HashSet<>(words1);
            union.addAll(words2);

            if (union.isEmpty()) {
                return 0;
            }

            double jaccardIndex = (double) intersection.size() / union.size();
            int percentage = (int) (jaccardIndex * 100);

            // Simulate heavy processing delay
            System.out.println("Worker: Starting heavy calculation (sleeping 5s)...");
            Thread.sleep(5000);
            System.out.println("Worker: Heavy calculation finished.");

            return percentage;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1; // Error code
        }
    }
}

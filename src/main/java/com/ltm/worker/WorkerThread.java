package com.ltm.worker;

import com.ltm.dao.TaskDAO;
import com.ltm.model.Task;

import javax.servlet.ServletContext;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkerThread extends Thread {
    private final ServletContext servletContext;
    private final TaskDAO taskDAO = new TaskDAO();
    private volatile boolean running = true;

    public WorkerThread(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // Fallback no-arg constructor for tests/tooling
    public WorkerThread() {
        this(null);
    }

    @Override
    public void run() {
        System.out.println("Worker Thread started...");
        while (running) {
            try {
                Task task = TaskQueue.getInstance().takeTask();
                System.out.println("Processing task ID: " + task.getId());

                // Update status to PROCESSING
                taskDAO.updateTaskStatus(task.getId(), "PROCESSING", 0);

                CorpusEvaluation evaluation = evaluateCorpus(task);

                taskDAO.updateTaskResult(
                    task.getId(),
                    evaluation.failed ? "FAILED" : "COMPLETED",
                    evaluation.bestSimilarity,
                    evaluation.bestMatchContent,
                    evaluation.comparisonDetails);

                System.out.println("Task ID " + task.getId() +
                    (evaluation.failed ? " failed." : (" completed. Best similarity: " + evaluation.bestSimilarity + "%")));

            } catch (InterruptedException e) {
                System.out.println("Worker Thread interrupted.");
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stopWorker() {
        running = false;
        this.interrupt();
    }

    private CorpusEvaluation evaluateCorpus(Task task) {
        if (servletContext == null) {
            return CorpusEvaluation.failure("Server is missing ServletContext; cannot locate corpus directory.");
        }

        String topic = task.getTopic();
        if (topic == null || topic.isBlank()) {
            return CorpusEvaluation.failure("Task does not include a topic selection.");
        }

        String corpusPath = servletContext.getRealPath("/WEB-INF/corpus/" + topic);
        if (corpusPath == null) {
            return CorpusEvaluation.failure("Unable to resolve corpus path for topic: " + topic);
        }

        File corpusDir = new File(corpusPath);
        if (!corpusDir.exists() || !corpusDir.isDirectory()) {
            return CorpusEvaluation.failure("Corpus directory not found for topic: " + topic);
        }

        File[] files = corpusDir.listFiles();
        if (files == null || files.length == 0) {
            return CorpusEvaluation.failure("Corpus directory is empty for topic: " + topic);
        }

        List<String> comparisonLines = new ArrayList<>();
        int bestSimilarity = 0;
        String bestFileName = "";
        String bestFileContent = "";

        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }
            try {
                String fileContent = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                int similarity = computeSimilarityPercentage(task.getSourceContent(), fileContent);

                comparisonLines.add(String.format("%s -> %d%%", file.getName(), similarity));

                if (similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestFileName = file.getName();
                    bestFileContent = fileContent;
                }
            } catch (Exception e) {
                comparisonLines.add(String.format("%s -> ERROR: %s", file.getName(), e.getMessage()));
            }
        }

        if (comparisonLines.isEmpty()) {
            return CorpusEvaluation.failure("No readable files found in corpus for topic: " + topic);
        }

        String details = String.join("\n", comparisonLines);
        String bestMatchText = bestFileName.isEmpty()
                ? "No successful comparisons were produced."
                : "Matched file: " + bestFileName + "\n\n" + bestFileContent;

        simulateHeavyWork();

        return new CorpusEvaluation(bestSimilarity, bestMatchText, details, false);
    }

    private int computeSimilarityPercentage(String text1, String text2) {
        String lowerText1 = text1 == null ? "" : text1.toLowerCase();
        String lowerText2 = text2 == null ? "" : text2.toLowerCase();

        Set<String> words1 = new HashSet<>(List.of(lowerText1.split("\\W+")));
        Set<String> words2 = new HashSet<>(List.of(lowerText2.split("\\W+")));

        words1.removeIf(String::isBlank);
        words2.removeIf(String::isBlank);

        if (words1.isEmpty() && words2.isEmpty()) {
            return 0;
        }

        Set<String> intersection = new HashSet<>(words1);
        intersection.retainAll(words2);

        Set<String> union = new HashSet<>(words1);
        union.addAll(words2);

        if (union.isEmpty()) {
            return 0;
        }

        double jaccardIndex = (double) intersection.size() / union.size();
        return (int) Math.round(jaccardIndex * 100);
    }

    private void simulateHeavyWork() {
        try {
            System.out.println("Worker: Starting heavy calculation (sleeping 5s)...");
            Thread.sleep(5000);
            System.out.println("Worker: Heavy calculation finished.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class CorpusEvaluation {
        final int bestSimilarity;
        final String bestMatchContent;
        final String comparisonDetails;
        final boolean failed;

        CorpusEvaluation(int bestSimilarity, String bestMatchContent, String comparisonDetails, boolean failed) {
            this.bestSimilarity = bestSimilarity;
            this.bestMatchContent = bestMatchContent;
            this.comparisonDetails = comparisonDetails;
            this.failed = failed;
        }

        static CorpusEvaluation failure(String message) {
            return new CorpusEvaluation(0, message, message, true);
        }
    }
}

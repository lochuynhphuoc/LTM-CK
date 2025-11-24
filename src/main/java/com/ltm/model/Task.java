package com.ltm.model;

import java.sql.Timestamp;

public class Task {
    private int id;
    private int userId;
    private String topic;
    private String sourceContent;
    private String targetContent;
    private String comparisonDetails;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private int result;
    private Timestamp createdAt;

    public Task() {}

    public Task(int id, int userId, String topic, String sourceContent, String targetContent,
                String comparisonDetails, String status, int result, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.topic = topic;
        this.sourceContent = sourceContent;
        this.targetContent = targetContent;
        this.comparisonDetails = comparisonDetails;
        this.status = status;
        this.result = result;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getSourceContent() { return sourceContent; }
    public void setSourceContent(String sourceContent) { this.sourceContent = sourceContent; }

    public String getTargetContent() { return targetContent; }
    public void setTargetContent(String targetContent) { this.targetContent = targetContent; }

    public String getComparisonDetails() { return comparisonDetails; }
    public void setComparisonDetails(String comparisonDetails) { this.comparisonDetails = comparisonDetails; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getResult() { return result; }
    public void setResult(int result) { this.result = result; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

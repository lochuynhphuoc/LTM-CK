package com.ltm.model;

import java.sql.Timestamp;

public class Task {
    private int id;
    private int userId;
    private String url;
    private String keyword;
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED
    private int result;
    private Timestamp createdAt;

    public Task() {}

    public Task(int id, int userId, String url, String keyword, String status, int result, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.url = url;
        this.keyword = keyword;
        this.status = status;
        this.result = result;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getResult() { return result; }
    public void setResult(int result) { this.result = result; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}

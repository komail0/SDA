package com.example.sda.models;

import java.io.InputStream;
import java.sql.Timestamp;

/**
 * Represents a project uploaded by an Alumni user.
 * Maps to the 'projects' table.
 */
public class Project {
    private int projectId;
    private int userId;
    private String title;
    private String description;
    private String category;
    private int year;
    private String university;
    private String supervisor;
    private String githubLink;
    private String technologies;
    private InputStream pdfFileStream; // Used for saving (BLOB insertion)
    private byte[] pdfFileBytes;       // Used for reading/retrieval
    private Timestamp uploadedAt;

    // NEW FIELD: Author's Name for display purposes
    private String authorName;

    // Constructor for creating a new project (upload)
    public Project(int userId, String title, String description, String category, int year,
                   String university, String supervisor, String githubLink, String technologies,
                   InputStream pdfFileStream) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.year = year;
        this.university = university;
        this.supervisor = supervisor;
        this.githubLink = githubLink;
        this.technologies = technologies;
        this.pdfFileStream = pdfFileStream;
    }

    // Constructor for reading project data from the database (omitted BLOB retrieval for simplicity)
    public Project(int projectId, int userId, String title, String description, String category,
                   int year, String university, String supervisor, String githubLink,
                   String technologies, Timestamp uploadedAt) {
        this.projectId = projectId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.year = year;
        this.university = university;
        this.supervisor = supervisor;
        this.githubLink = githubLink;
        this.technologies = technologies;
        this.uploadedAt = uploadedAt;
    }

    // NEW Constructor overload to include Author Name for search results
    public Project(int projectId, int userId, String title, String description, String category,
                   int year, String university, String supervisor, String githubLink,
                   String technologies, Timestamp uploadedAt, String authorName) {
        this(projectId, userId, title, description, category, year, university, supervisor, githubLink, technologies, uploadedAt);
        this.authorName = authorName;
    }


    // --- Getters and Setters ---

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }
    public int getUserId() { return userId; }
    public InputStream getPdfFileStream() { return pdfFileStream; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public int getYear() { return year; }
    public String getUniversity() { return university; }
    public String getSupervisor() { return supervisor; }
    public String getGithubLink() { return githubLink; }
    public String getTechnologies() { return technologies; }
    public byte[] getPdfFileBytes() { return pdfFileBytes; }
    public void setPdfFileBytes(byte[] pdfFileBytes) { this.pdfFileBytes = pdfFileBytes; }
    public Timestamp getUploadedAt() { return uploadedAt; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
}
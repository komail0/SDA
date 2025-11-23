package com.example.sda.models;

import java.sql.Timestamp;

public class Rating {
    private int id;
    private int studentId;
    private int mentorId;
    private int overallRating;
    private int communicationRating;
    private int knowledgeRating;
    private int responsivenessRating;
    private int helpfulnessRating;
    private String feedbackText;
    private Timestamp createdAt;

    // Helper field for display
    private String studentName;

    // Constructor for Saving (No ID, No Student Name)
    public Rating(int studentId, int mentorId, int overallRating, int communicationRating,
                  int knowledgeRating, int responsivenessRating, int helpfulnessRating, String feedbackText) {
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.overallRating = overallRating;
        this.communicationRating = communicationRating;
        this.knowledgeRating = knowledgeRating;
        this.responsivenessRating = responsivenessRating;
        this.helpfulnessRating = helpfulnessRating;
        this.feedbackText = feedbackText;
    }

    // Constructor for Fetching (With Student Name & Date)
    public Rating(int id, int studentId, int mentorId, int overallRating, int communicationRating,
                  int knowledgeRating, int responsivenessRating, int helpfulnessRating,
                  String feedbackText, Timestamp createdAt, String studentName) {
        this.id = id;
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.overallRating = overallRating;
        this.communicationRating = communicationRating;
        this.knowledgeRating = knowledgeRating;
        this.responsivenessRating = responsivenessRating;
        this.helpfulnessRating = helpfulnessRating;
        this.feedbackText = feedbackText;
        this.createdAt = createdAt;
        this.studentName = studentName;
    }

    // Getters
    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getMentorId() { return mentorId; }
    public int getOverallRating() { return overallRating; }
    public int getCommunicationRating() { return communicationRating; }
    public int getKnowledgeRating() { return knowledgeRating; }
    public int getResponsivenessRating() { return responsivenessRating; }
    public int getHelpfulnessRating() { return helpfulnessRating; }
    public String getFeedbackText() { return feedbackText; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getStudentName() { return studentName; }
}
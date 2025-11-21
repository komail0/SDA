package com.example.sda.models;

import java.time.LocalDateTime;

/**
 * Model class representing a rating given by a student to a mentor
 */
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
    private LocalDateTime createdAt;

    // Constructor for creating new rating (without ID)
    public Rating(int studentId, int mentorId, int overallRating,
                  int communicationRating, int knowledgeRating,
                  int responsivenessRating, int helpfulnessRating,
                  String feedbackText) {
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.overallRating = overallRating;
        this.communicationRating = communicationRating;
        this.knowledgeRating = knowledgeRating;
        this.responsivenessRating = responsivenessRating;
        this.helpfulnessRating = helpfulnessRating;
        this.feedbackText = feedbackText;
    }

    // Constructor for existing rating (with ID and timestamp)
    public Rating(int id, int studentId, int mentorId, int overallRating,
                  int communicationRating, int knowledgeRating,
                  int responsivenessRating, int helpfulnessRating,
                  String feedbackText, LocalDateTime createdAt) {
        this(studentId, mentorId, overallRating, communicationRating,
                knowledgeRating, responsivenessRating, helpfulnessRating, feedbackText);
        this.id = id;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public int getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(int overallRating) {
        this.overallRating = overallRating;
    }

    public int getCommunicationRating() {
        return communicationRating;
    }

    public void setCommunicationRating(int communicationRating) {
        this.communicationRating = communicationRating;
    }

    public int getKnowledgeRating() {
        return knowledgeRating;
    }

    public void setKnowledgeRating(int knowledgeRating) {
        this.knowledgeRating = knowledgeRating;
    }

    public int getResponsivenessRating() {
        return responsivenessRating;
    }

    public void setResponsivenessRating(int responsivenessRating) {
        this.responsivenessRating = responsivenessRating;
    }

    public int getHelpfulnessRating() {
        return helpfulnessRating;
    }

    public void setHelpfulnessRating(int helpfulnessRating) {
        this.helpfulnessRating = helpfulnessRating;
    }

    public String getFeedbackText() {
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", mentorId=" + mentorId +
                ", overallRating=" + overallRating +
                ", feedbackText='" + feedbackText + '\'' +
                '}';
    }
}
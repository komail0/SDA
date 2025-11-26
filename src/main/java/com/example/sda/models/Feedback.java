package com.example.sda.models;

import java.time.LocalDateTime;


public class Feedback {
    private int id;
    private int studentId;
    private String studentName;
    private int mentorId;
    private String mentorName;
    private int overallRating;
    private int communicationRating;
    private int knowledgeRating;
    private int responsivenessRating;
    private int helpfulnessRating;
    private String feedbackText;
    private LocalDateTime createdAt;

    // Constructor
    public Feedback(int id, int studentId, String studentName, int mentorId, String mentorName,
                    int overallRating, int communicationRating, int knowledgeRating,
                    int responsivenessRating, int helpfulnessRating, String feedbackText,
                    LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.mentorId = mentorId;
        this.mentorName = mentorName;
        this.overallRating = overallRating;
        this.communicationRating = communicationRating;
        this.knowledgeRating = knowledgeRating;
        this.responsivenessRating = responsivenessRating;
        this.helpfulnessRating = helpfulnessRating;
        this.feedbackText = feedbackText;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getMentorId() {
        return mentorId;
    }

    public void setMentorId(int mentorId) {
        this.mentorId = mentorId;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
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

    /**
     * Get star string representation of rating
     */
    public String getStarsString(int rating) {
        return "⭐".repeat(Math.max(0, rating));
    }

    /**
     * Get formatted date string
     */
    public String getFormattedDate() {
        if (createdAt == null) return "";

        LocalDateTime now = LocalDateTime.now();
        long days = java.time.Duration.between(createdAt, now).toDays();

        if (days == 0) return "Today";
        if (days == 1) return "1 day ago";
        if (days < 7) return days + " days ago";
        if (days < 14) return "1 week ago";
        if (days < 30) return (days / 7) + " weeks ago";
        return (days / 30) + " months ago";
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", studentName='" + studentName + '\'' +
                ", mentorName='" + mentorName + '\'' +
                ", overallRating=" + overallRating +
                ", feedbackText='" + feedbackText + '\'' +
                '}';
    }
}
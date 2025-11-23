package com.example.sda.models;

import java.sql.Timestamp;

public class MentorshipRequest {
    private int requestId;
    private int studentId;
    private int mentorId;
    private String title;
    private String description;
    private String university;
    private String academicYear;
    private String status;
    private Timestamp requestDate;

    // Helper fields for display (fetched via JOINs)
    private String mentorName; // Used when Student views list
    private String studentName; // Used when Mentor views list

    // Constructor for fetching from DB (Generalized for both Student and Mentor views)
    public MentorshipRequest(int requestId, int studentId, int mentorId, String title,
                             String description, String university, String academicYear,
                             String status, Timestamp requestDate, String relatedUserName) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.title = title;
        this.description = description;
        this.university = university;
        this.academicYear = academicYear;
        this.status = status;
        this.requestDate = requestDate;

        // We store the related user's name (Student or Mentor) in both fields
        // or you can use specific logic if needed. For now, assigning to both covers all cases.
        this.mentorName = relatedUserName;
        this.studentName = relatedUserName;
    }

    // Constructor for creating a new request (User Upload)
    public MentorshipRequest(int studentId, int mentorId, String title, String description,
                             String university, String academicYear) {
        this.studentId = studentId;
        this.mentorId = mentorId;
        this.title = title;
        this.description = description;
        this.university = university;
        this.academicYear = academicYear;
        this.status = "pending";
    }

    // --- Getters ---
    public int getRequestId() { return requestId; }
    public int getStudentId() { return studentId; }
    public int getMentorId() { return mentorId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUniversity() { return university; }
    public String getAcademicYear() { return academicYear; }
    public String getStatus() { return status; }
    public Timestamp getRequestDate() { return requestDate; }
    public String getMentorName() { return mentorName; }
    public String getStudentName() { return studentName; }
}
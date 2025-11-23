package com.example.sda.models;

import java.sql.Timestamp;

public class Chat {
    private int chatId;
    private int mentorId;
    private int studentId;
    private Timestamp createdAt;
    
    // Helper field for display (Name of the person you are chatting with)
    private String otherParticipantName;

    public Chat(int chatId, int mentorId, int studentId, Timestamp createdAt, String otherParticipantName) {
        this.chatId = chatId;
        this.mentorId = mentorId;
        this.studentId = studentId;
        this.createdAt = createdAt;
        this.otherParticipantName = otherParticipantName;
    }

    // Getters
    public int getChatId() { return chatId; }
    public int getMentorId() { return mentorId; }
    public int getStudentId() { return studentId; }
    public Timestamp getCreatedAt() { return createdAt; }
    public String getOtherParticipantName() { return otherParticipantName; }
}
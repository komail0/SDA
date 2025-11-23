package com.example.sda.models;

import java.sql.Timestamp;

public class Message {
    private int messageId;
    private int chatId;
    private int mentorId;
    private int studentId;
    private int senderId;
    private String messageText;
    private Timestamp sentAt;

    // Constructor for fetching
    public Message(int messageId, int chatId, int mentorId, int studentId, int senderId, String messageText, Timestamp sentAt) {
        this.messageId = messageId;
        this.chatId = chatId;
        this.mentorId = mentorId;
        this.studentId = studentId;
        this.senderId = senderId;
        this.messageText = messageText;
        this.sentAt = sentAt;
    }

    // Constructor for sending (ID and Timestamp auto-generated)
    public Message(int chatId, int mentorId, int studentId, int senderId, String messageText) {
        this.chatId = chatId;
        this.mentorId = mentorId;
        this.studentId = studentId;
        this.senderId = senderId;
        this.messageText = messageText;
    }

    public int getMessageId() { return messageId; }
    public int getChatId() { return chatId; }
    public int getMentorId() { return mentorId; }
    public int getStudentId() { return studentId; }
    public int getSenderId() { return senderId; }
    public String getMessageText() { return messageText; }
    public Timestamp getSentAt() { return sentAt; }
}
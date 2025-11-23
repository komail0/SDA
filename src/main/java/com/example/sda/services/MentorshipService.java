package com.example.sda.services;

import com.example.sda.dao.ChatDAO;
import com.example.sda.dao.MentorshipDAO;
import com.example.sda.models.MentorshipRequest;
import java.util.List;

public class MentorshipService {
    private final MentorshipDAO dao = new MentorshipDAO();
    private final ChatDAO chatDAO = new ChatDAO(); // Needed to create chat on acceptance

    public boolean sendRequest(MentorshipRequest request) {
        return dao.createRequest(request);
    }

    public boolean hasExistingRequest(int studentId, int mentorId, String projectTitle) {
        return dao.checkRequestExists(studentId, mentorId, projectTitle);
    }

    public List<MentorshipRequest> getStudentRequests(int studentId) {
        return dao.getRequestsByStudentId(studentId);
    }

    public boolean cancelRequest(int requestId) {
        return dao.deleteRequest(requestId);
    }

    // --- NEW METHODS FOR ALUMNI ---

    public List<MentorshipRequest> getRequestsForMentor(int mentorId) {
        return dao.getRequestsByMentorId(mentorId);
    }

    public boolean updateRequestStatus(int requestId, String status) {
        boolean updated = dao.updateStatus(requestId, status);

        if (updated && "accepted".equalsIgnoreCase(status)) {
            // 1. Fetch the request to get Student ID and Mentor ID
            MentorshipRequest req = dao.getRequestById(requestId);
            if (req != null) {
                // 2. Create the Chat
                chatDAO.createChat(req.getMentorId(), req.getStudentId());
            }
        }
        return updated;
    }
}
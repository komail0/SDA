package com.example.sda.services;

import com.example.sda.dao.RatingDAO;
import com.example.sda.models.Rating;
import com.example.sda.models.User;
import java.util.List;

public class RatingService {
    private final RatingDAO ratingDAO = new RatingDAO();

    public List<User> getMentorsForStudent(int studentId) {
        return ratingDAO.getMentorsByStudentChat(studentId);
    }

    public boolean hasRated(int studentId, int mentorId) {
        return ratingDAO.hasStudentRatedMentor(studentId, mentorId);
    }

    public boolean submitRating(Rating rating) {
        return ratingDAO.saveRating(rating);
    }

    // --- NEW ---
    public List<Rating> getMentorRatings(int mentorId) {
        return ratingDAO.getRatingsForMentor(mentorId);
    }
}
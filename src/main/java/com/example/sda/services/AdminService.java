package com.example.sda.services;

import com.example.sda.dao.AdminDAO;
import com.example.sda.dao.ProjectDAO;
import com.example.sda.dao.UserDAO;
import com.example.sda.models.Project;
import com.example.sda.models.User;

import java.util.List;
import java.util.Map;

public class AdminService {
    private final AdminDAO adminDAO = new AdminDAO();
    private final UserDAO userDAO = new UserDAO();
    private final ProjectDAO projectDAO = new ProjectDAO();

    // Dashboard Stats
    public int getTotalUsers() { return adminDAO.getTotalUsers(); }
    public int getTotalProjects() { return adminDAO.getTotalProjects(); }
    public int getActiveMentors() { return adminDAO.getActiveMentors(); }
    public double getAverageRating() { return adminDAO.getAverageRating(); }
    public Map<String, Integer> getUserDistribution() { return adminDAO.getUserDistribution(); }
    public Map<String, Integer> getProjectStatusDistribution() { return adminDAO.getProjectStatusDistribution(); }

    // User Management
    public List<User> getAllUsers() { return userDAO.getAllUsers(); }
    public boolean deleteUser(int userId) { return userDAO.deleteUser(userId); }

    // Repository Management
    public List<Project> getAllProjects() { return projectDAO.getAllProjectsForAdmin(); }
    public String getProjectStatus(int projectId) { return projectDAO.getProjectStatus(projectId); }
    public boolean approveProject(int projectId) { return projectDAO.updateProjectStatus(projectId, "approved"); }
    public boolean rejectProject(int projectId) { return projectDAO.updateProjectStatus(projectId, "rejected"); }
    public boolean deleteProject(int projectId) { return projectDAO.deleteProject(projectId); }
}
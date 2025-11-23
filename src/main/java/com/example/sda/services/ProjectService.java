package com.example.sda.services;

import com.example.sda.dao.ProjectDAO;
import com.example.sda.models.Project;
import com.example.sda.models.User;

import java.util.List;

public class ProjectService {
    private final ProjectDAO projectDAO;

    public ProjectService() {
        this.projectDAO = new ProjectDAO();
    }

    public boolean uploadNewProject(Project project, long fileSize) {
        int projectId = projectDAO.saveProject(project, fileSize);
        return projectId != -1;
    }

    public Project getProjectById(int projectId) {
        return projectDAO.getProjectById(projectId);
    }

    public List<Project> searchProjects(String keyword, Integer year, String category) {
        return projectDAO.getProjectsByFilter(keyword, year, category);
    }

    public int countTotalProjects() {
        return projectDAO.countProjects();
    }

    public int countUniqueContributors() {
        return projectDAO.countUniqueProjectUsers();
    }

    // --- NEW METHODS ---
    public List<Project> getAlumniProjects(int userId) {
        return projectDAO.getProjectsByUserId(userId);
    }

    public boolean deleteProject(int projectId) {
        return projectDAO.deleteProject(projectId);
    }
}
package com.example.sda.dao;

import com.example.sda.DB;
import com.example.sda.models.Project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for the Project model.
 */
public class ProjectDAO {

    public int saveProject(Project project, long fileSize) {
        String query = "INSERT INTO project (user_id, title, description, category, year, university, supervisor, github_link, technologies, pdf_file) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, project.getUserId());
            stmt.setString(2, project.getTitle());
            stmt.setString(3, project.getDescription());
            stmt.setString(4, project.getCategory());
            stmt.setInt(5, project.getYear());
            stmt.setString(6, project.getUniversity());

            if (project.getSupervisor() != null && !project.getSupervisor().isEmpty()) stmt.setString(7, project.getSupervisor());
            else stmt.setNull(7, Types.VARCHAR);

            if (project.getGithubLink() != null && !project.getGithubLink().isEmpty()) stmt.setString(8, project.getGithubLink());
            else stmt.setNull(8, Types.VARCHAR);

            if (project.getTechnologies() != null && !project.getTechnologies().isEmpty()) stmt.setString(9, project.getTechnologies());
            else stmt.setNull(9, Types.VARCHAR);

            stmt.setBinaryStream(10, project.getPdfFileStream(), fileSize);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) generatedId = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Error saving new project: " + e.getMessage());
            e.printStackTrace();
        }
        return generatedId;
    }

    public Project getProjectById(int projectId) {
        String query = "SELECT p.*, u.username AS author_name FROM project p JOIN user u ON p.user_id = u.id WHERE p.project_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                byte[] pdfBytes = rs.getBytes("pdf_file");
                Project project = new Project(
                        rs.getInt("project_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("university"),
                        rs.getString("supervisor"),
                        rs.getString("github_link"),
                        rs.getString("technologies"),
                        rs.getTimestamp("uploaded_at"),
                        rs.getString("author_name")
                );
                project.setPdfFileBytes(pdfBytes);
                return project;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Project> getProjectsByFilter(String keyword, Integer year, String category) {
        String baseQuery = "SELECT p.*, u.username AS author_name FROM project p JOIN user u ON p.user_id = u.id WHERE 1=1";
        StringBuilder sql = new StringBuilder(baseQuery);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isEmpty()) {
            sql.append(" AND (p.title LIKE ? OR p.description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        if (year != null && year > 1900) {
            sql.append(" AND p.year = ?");
            params.add(year);
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND p.category LIKE ?");
            params.add("%" + category + "%");
        }
        sql.append(" ORDER BY p.uploaded_at DESC");

        List<Project> projects = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projects.add(new Project(
                        rs.getInt("project_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("university"),
                        rs.getString("supervisor"),
                        rs.getString("github_link"),
                        rs.getString("technologies"),
                        rs.getTimestamp("uploaded_at"),
                        rs.getString("author_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }

    public int countProjects() {
        String query = "SELECT COUNT(*) FROM project";
        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    public int countUniqueProjectUsers() {
        String query = "SELECT COUNT(DISTINCT user_id) FROM project";
        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }

    // --- NEW METHODS FOR ALUMNI REPOSITORY ---

    public List<Project> getProjectsByUserId(int userId) {
        List<Project> projects = new ArrayList<>();
        String query = "SELECT p.*, u.username AS author_name FROM project p JOIN user u ON p.user_id = u.id WHERE p.user_id = ? ORDER BY p.uploaded_at DESC";

        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                projects.add(new Project(
                        rs.getInt("project_id"),
                        rs.getInt("user_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("category"),
                        rs.getInt("year"),
                        rs.getString("university"),
                        rs.getString("supervisor"),
                        rs.getString("github_link"),
                        rs.getString("technologies"),
                        rs.getTimestamp("uploaded_at"),
                        rs.getString("author_name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }

    public boolean deleteProject(int projectId) {
        String query = "DELETE FROM project WHERE project_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, projectId);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
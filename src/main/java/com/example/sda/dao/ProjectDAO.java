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

public class ProjectDAO {

    public int saveProject(Project project, long fileSize) {
        // Status defaults to 'pending' in DB
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
                return mapResultSetToProject(rs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Project> getProjectsByFilter(String keyword, Integer year, String category) {
        // Optimization: Explicitly select columns to avoid fetching BLOBs in lists if possible,
        // but for now, we keep logic consistent.
        // Note: Ideally you should apply the same optimization here as I did in getAllProjectsForAdmin if search is slow.
        String baseQuery = "SELECT p.*, u.username AS author_name FROM project p JOIN user u ON p.user_id = u.id WHERE p.status = 'approved'";
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

        return executeQuery(sql.toString(), params);
    }

    public int countProjects() {
        return getCount("SELECT COUNT(*) FROM project WHERE status = 'approved'");
    }

    public int countUniqueProjectUsers() {
        return getCount("SELECT COUNT(DISTINCT user_id) FROM project WHERE status = 'approved'");
    }


    public List<Project> getProjectsByUserId(int userId) {
        String query = "SELECT p.*, u.username AS author_name FROM project p JOIN user u ON p.user_id = u.id WHERE p.user_id = ? ORDER BY p.uploaded_at DESC";
        List<Object> params = new ArrayList<>();
        params.add(userId);
        return executeQuery(query, params);
    }

    public boolean deleteProject(int projectId) {
        String query = "DELETE FROM project WHERE project_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- ADMIN METHODS ---

    public List<Project> getAllProjectsForAdmin() {
        // PERFORMANCE FIX: DO NOT use SELECT * or p.* here.
        // We explicitly exclude 'pdf_file' to prevent downloading huge BLOBs for the list view.
        String query = "SELECT p.project_id, p.user_id, p.title, p.description, p.category, " +
                "p.year, p.university, p.supervisor, p.github_link, p.technologies, " +
                "p.uploaded_at, p.status, u.username AS author_name " +
                "FROM project p " +
                "LEFT JOIN user u ON p.user_id = u.id " +
                "ORDER BY p.uploaded_at DESC";

        return executeQuery(query, new ArrayList<>());
    }

    public String getProjectStatus(int projectId) {
        String query = "SELECT status FROM project WHERE project_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, projectId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) return rs.getString("status");
        } catch (Exception e) { e.printStackTrace(); }
        return "pending";
    }

    public boolean updateProjectStatus(int projectId, String status) {
        String query = "UPDATE project SET status = ? WHERE project_id = ?";
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, projectId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Helpers ---

    private List<Project> executeQuery(String sql, List<Object> params) {
        List<Project> projects = new ArrayList<>();
        try (Connection conn = DB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) stmt.setObject(i + 1, params.get(i));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projects;
    }

    private Project mapResultSetToProject(ResultSet rs) throws java.sql.SQLException {
        Project p = new Project(
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
                rs.getString("author_name"),
                rs.getString("status")
        );
        // This try-catch is crucial. If 'pdf_file' is missing (like in the Admin optimization),
        // it throws an exception which we ignore, leaving pdfFileBytes null (which is what we want).
        try { p.setPdfFileBytes(rs.getBytes("pdf_file")); } catch (Exception e) {}
        return p;
    }

    private int getCount(String query) {
        try (Connection conn = DB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {}
        return 0;
    }
}
package com.example.sda;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {
        Button insertBtn = new Button("Insert User");
        Button selectBtn = new Button("Show Users");

        insertBtn.setOnAction(e -> insertUser("Komail", "komail@gmail.com"));
        selectBtn.setOnAction(e -> showUsers());

        VBox root = new VBox(10, insertBtn, selectBtn);
        Scene scene = new Scene(root, 300, 200);
        stage.setTitle("Railway MySQL JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    private void insertUser(String username, String email) {
        try (Connection conn = DB.getConnection()) {
            String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.executeUpdate();
            System.out.println("User inserted!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showUsers() {
        try (Connection conn = DB.getConnection()) {
            String sql = "SELECT * FROM users";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("username") + " | " +
                        rs.getString("email"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

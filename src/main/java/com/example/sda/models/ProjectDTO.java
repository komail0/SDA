package com.example.sda.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Data Transfer Object for the Admin Repository TableView.
 * This must be a public standalone class for PropertyValueFactory to work.
 */
public class ProjectDTO {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty description;
    private final SimpleStringProperty owner;
    private final SimpleStringProperty status;

    public ProjectDTO(Integer id, String title, String desc, String owner, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(desc);
        this.owner = new SimpleStringProperty(owner);
        this.status = new SimpleStringProperty(status);
    }

    // Getters required by PropertyValueFactory
    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public String getOwner() { return owner.get(); }
    public String getStatus() { return status.get(); }
}
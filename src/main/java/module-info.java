module com.example.sda {
    // Required JavaFX and external modules
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Database connection (for DB and DAO classes)
    requires javafx.web; // Included from your provided content
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires jbcrypt;


    // --- JPMS ACCESS RULES ---

    // 1. OPENINGS (Allow JavaFX Reflection for Controllers and Resources)
    // Opens the root package (com.example.sda) for HelloApplication and resources
    opens com.example.sda to javafx.fxml;

    // Opens controller packages so FXMLoader can instantiate them and inject @FXML fields
    opens com.example.sda.controllers.auth to javafx.fxml;
    opens com.example.sda.controllers.shared to javafx.fxml;

    // Opens the utility package to allow resources (like CSS for AlertHelper) to be loaded via reflection
    opens com.example.sda.utils to javafx.fxml;


    // 2. EXPORTS (Allow code access to packages across module boundaries)

    // Export the root package for application launch
    exports com.example.sda;

    // Export core business packages so Controllers/Services/DAOs can see Models/Enums/Utils
    exports com.example.sda.models;
    exports com.example.sda.enums;

    // Export service and DAO packages (if used by other modules, which is common)
    exports com.example.sda.services;
    exports com.example.sda.dao;

    // Export the utility package for use by all other parts of the application
    exports com.example.sda.utils;

    // Export controllers (optional, but often done if other modules might navigate to them)
    exports com.example.sda.controllers.auth;
    exports com.example.sda.controllers.shared;
    // NOTE: You may need to add exports for other controller packages (admin/components) later.
}
# AcadBridge - IntelliJ IDEA Project Structure


## Project Structure

```
SDA/
├── .idea/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── sda/
│   │   │               ├── DB.java
│   │   │               ├── HelloApplication.java
│   │   │               ├── HelloController.java
│   │   │               ├── Launcher.java
│   │   │               ├── module-info.java
│   │   │               │
│   │   │               ├── controllers/
│   │   │               │   ├── auth/
│   │   │               │   │   ├── RegistrationController.java
│   │   │               │   │   └── LoginController.java
│   │   │               │   │
│   │   │               │   ├── shared/
│   │   │               │   │   ├── ProjectRepositoryController.java
│   │   │               │   │   ├── ProjectSearchController.java
│   │   │               │   │   ├── UploadProjectController.java
│   │   │               │   │   ├── MentorshipRequestController.java
│   │   │               │   │   ├── MentorshipApprovalController.java
│   │   │               │   │   ├── ChatController.java
│   │   │               │   │   ├── ResourceSharingController.java
│   │   │               │   │   ├── FeedbackRatingController.java
│   │   │               │   │   └── ViewFeedbackController.java
│   │   │               │   │
│   │   │               │   ├── admin/
│   │   │               │   │   ├── UserManagementController.java
│   │   │               │   │   ├── RepositoryManagementController.java
│   │   │               │   │   └── ReportsGenerationController.java
│   │   │               │   │
│   │   │               │   └── components/
│   │   │               │       └── SidebarController.java
│   │   │               │
│   │   │               ├── dao/
│   │   │               │   ├── UserDAO.java
│   │   │               │   ├── StudentDAO.java
│   │   │               │   ├── AlumniDAO.java
│   │   │               │   ├── AdminDAO.java
│   │   │               │   ├── ProjectDAO.java
│   │   │               │   ├── MentorshipRequestDAO.java
│   │   │               │   ├── ChatMessageDAO.java
│   │   │               │   ├── ResourceDAO.java
│   │   │               │   ├── FeedbackDAO.java
│   │   │               │   └── RatingDAO.java
│   │   │               │
│   │   │               ├── models/
│   │   │               │   ├── User.java
│   │   │               │   ├── Student.java
│   │   │               │   ├── Alumni.java
│   │   │               │   ├── Admin.java
│   │   │               │   ├── Project.java
│   │   │               │   ├── MentorshipRequest.java
│   │   │               │   ├── ChatMessage.java
│   │   │               │   ├── Resource.java
│   │   │               │   ├── Feedback.java
│   │   │               │   └── Rating.java
│   │   │               │
│   │   │               ├── services/
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── UserService.java
│   │   │               │   ├── ProjectService.java
│   │   │               │   ├── MentorshipService.java
│   │   │               │   ├── ChatService.java
│   │   │               │   ├── ResourceService.java
│   │   │               │   ├── FeedbackService.java
│   │   │               │   └── ReportService.java
│   │   │               │
│   │   │               ├── utils/
│   │   │               │   ├── SessionManager.java
│   │   │               │   ├── SceneManager.java
│   │   │               │   ├── Validator.java
│   │   │               │   ├── FileHandler.java
│   │   │               │   ├── DateFormatter.java
│   │   │               │   ├── AlertHelper.java
│   │   │               │   ├── JsonParser.java
│   │   │               │   └── HttpRequestHelper.java
│   │   │               │
│   │   │               └── enums/
│   │   │                   ├── UserRole.java
│   │   │                   ├── RequestStatus.java
│   │   │                   ├── ProjectStatus.java
│   │   │                   └── ResourceType.java
│   │   │
│   │   └── resources/
│   │       └── com/
│   │           └── example/
│   │               └── sda/
│   │                   ├── hello-view.fxml
│   │                   │
│   │                   ├── fxml/
│   │                   │   ├── auth/
│   │                   │   │   ├── registration-view.fxml
│   │                   │   │   └── login-view.fxml
│   │                   │   │
│   │                   │   ├── shared/
│   │                   │   │   ├── project-repository-view.fxml
│   │                   │   │   ├── project-search-view.fxml
│   │                   │   │   ├── upload-project-view.fxml
│   │                   │   │   ├── mentorship-request-view.fxml
│   │                   │   │   ├── mentorship-approval-view.fxml
│   │                   │   │   ├── chat-view.fxml
│   │                   │   │   ├── resource-sharing-view.fxml
│   │                   │   │   ├── feedback-rating-view.fxml
│   │                   │   │   └── view-feedback-view.fxml
│   │                   │   │
│   │                   │   ├── admin/
│   │                   │   │   ├── user-management-view.fxml
│   │                   │   │   ├── repository-management-view.fxml
│   │                   │   │   └── reports-generation-view.fxml
│   │                   │   │
│   │                   │   └── components/
│   │                   │       └── sidebar.fxml
│   │                   │
│   │                   ├── css/
│   │                   │   ├── styles/
│   │                   │   │   ├── main-style.css
│   │                   │   │   ├── auth-style.css
│   │                   │   │   ├── project-repository-style.css
│   │                   │   │   ├── project-search-style.css
│   │                   │   │   ├── upload-project-style.css
│   │                   │   │   ├── mentorship-request-style.css
│   │                   │   │   ├── mentorship-approval-style.css
│   │                   │   │   ├── chat-style.css
│   │                   │   │   ├── resource-sharing-style.css
│   │                   │   │   ├── feedback-rating-style.css
│   │                   │   │   ├── view-feedback-style.css
│   │                   │   │   ├── user-management-style.css
│   │                   │   │   ├── repository-management-style.css
│   │                   │   │   └── reports-generation-style.css
│   │                   │   │
│   │                   │   └── components/
│   │                   │       ├── sidebar-style.css
│   │                   │       ├── button-style.css
│   │                   │       ├── card-style.css
│   │                   │       ├── table-style.css
│   │                   │       └── form-style.css
│   │                   │
│   │                   ├── images/
│   │                   │   ├── icons/
│   │                   │   │   ├── logo.png
│   │                   │   │   ├── user-icon.png
│   │                   │   │   ├── project-icon.png
│   │                   │   │   ├── search-icon.png
│   │                   │   │   ├── upload-icon.png
│   │                   │   │   ├── mentorship-icon.png
│   │                   │   │   ├── chat-icon.png
│   │                   │   │   ├── resource-icon.png
│   │                   │   │   ├── feedback-icon.png
│   │                   │   │   ├── admin-icon.png
│   │                   │   │   ├── reports-icon.png
│   │                   │   │   ├── settings-icon.png
│   │                   │   │   └── logout-icon.png
│   │                   │   │
│   │                   │   ├── backgrounds/
│   │                   │   │   └── login-bg.jpg
│   │                   │   │
│   │                   │   └── placeholders/
│   │                   │       ├── avatar-placeholder.png
│   │                   │       └── project-placeholder.png
│   │                   │
│   │                   ├── fonts/
│   │                   │   ├── Roboto-Regular.ttf
│   │                   │   ├── Roboto-Bold.ttf
│   │                   │   └── Roboto-Light.ttf
│   │                   │
│   │                   └── config/
│   │                       └── database.properties
│
├── test/
│
├── target/
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

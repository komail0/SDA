# AcadBridge - IntelliJ IDEA Project Structure

```
SDA/
├── .idea/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── acadbridge/
│   │   │           └── sda/
│   │   │               ├── Main.java
│   │   │               ├── module-info.java
│   │   │               │
│   │   │               ├── controllers/
│   │   │               │   ├── auth/
│   │   │               │   │   ├── RegistrationController.java
│   │   │               │   │   └── LoginController.java
│   │   │               │   │
│   │   │               │   ├── shared/
│   │   │               │   │   ├── DashboardController.java
│   │   │               │   │   ├── ProfileController.java
│   │   │               │   │   ├── ProjectRepositoryController.java
│   │   │               │   │   ├── ProjectDetailsController.java
│   │   │               │   │   ├── SearchController.java
│   │   │               │   │   ├── ChatController.java
│   │   │               │   │   ├── ResourcesLibraryController.java
│   │   │               │   │   ├── NotificationsController.java
│   │   │               │   │   ├── FeedbackRatingsController.java
│   │   │               │   │   ├── UserManagementController.java
│   │   │               │   │   ├── UploadSubmissionController.java
│   │   │               │   │   ├── RequestManagementController.java
│   │   │               │   │   ├── SettingsController.java
│   │   │               │   │   └── ReportsAnalyticsController.java
│   │   │               │   │
│   │   │               │   └── components/
│   │   │               │       ├── NavbarController.java
│   │   │               │       ├── SidebarController.java
│   │   │               │       └── HeaderController.java
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
│   │   │               │   ├── Rating.java
│   │   │               │   └── Notification.java
│   │   │               │
│   │   │               ├── services/
│   │   │               │   ├── ApiClient.java
│   │   │               │   ├── AuthService.java
│   │   │               │   ├── UserService.java
│   │   │               │   ├── ProjectService.java
│   │   │               │   ├── MentorshipService.java
│   │   │               │   ├── ChatService.java
│   │   │               │   ├── ResourceService.java
│   │   │               │   ├── FeedbackService.java
│   │   │               │   ├── NotificationService.java
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
│   │   │                   ├── NotificationType.java
│   │   │                   └── ResourceType.java
│   │   │
│   │   └── resources/
│   │       └── com/
│   │           └── acadbridge/
│   │               └── sda/
│   │                   ├── fxml/
│   │                   │   ├── auth/
│   │                   │   │   ├── registration-view.fxml
│   │                   │   │   └── login-view.fxml
│   │                   │   │
│   │                   │   ├── shared/
│   │                   │   │   ├── dashboard-view.fxml
│   │                   │   │   ├── profile-view.fxml
│   │                   │   │   ├── project-repository-view.fxml
│   │                   │   │   ├── project-details-view.fxml
│   │                   │   │   ├── search-view.fxml
│   │                   │   │   ├── chat-view.fxml
│   │                   │   │   ├── resources-library-view.fxml
│   │                   │   │   ├── notifications-view.fxml
│   │                   │   │   ├── feedback-ratings-view.fxml
│   │                   │   │   ├── user-management-view.fxml
│   │                   │   │   ├── upload-submission-view.fxml
│   │                   │   │   ├── request-management-view.fxml
│   │                   │   │   ├── settings-view.fxml
│   │                   │   │   └── reports-analytics-view.fxml
│   │                   │   │
│   │                   │   └── components/
│   │                   │       ├── navbar.fxml
│   │                   │       ├── sidebar.fxml
│   │                   │       └── header.fxml
│   │                   │
│   │                   ├── css/
│   │                   │   ├── styles/
│   │                   │   │   ├── main-style.css
│   │                   │   │   ├── auth-style.css
│   │                   │   │   ├── dashboard-style.css
│   │                   │   │   ├── profile-style.css
│   │                   │   │   ├── repository-style.css
│   │                   │   │   ├── project-details-style.css
│   │                   │   │   ├── search-style.css
│   │                   │   │   ├── chat-style.css
│   │                   │   │   ├── resources-style.css
│   │                   │   │   ├── notifications-style.css
│   │                   │   │   ├── feedback-style.css
│   │                   │   │   ├── user-management-style.css
│   │                   │   │   ├── upload-style.css
│   │                   │   │   ├── request-management-style.css
│   │                   │   │   ├── settings-style.css
│   │                   │   │   └── reports-style.css
│   │                   │   │
│   │                   │   └── components/
│   │                   │       ├── navbar-style.css
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
│   │                   │   │   ├── dashboard-icon.png
│   │                   │   │   ├── project-icon.png
│   │                   │   │   ├── chat-icon.png
│   │                   │   │   ├── resource-icon.png
│   │                   │   │   ├── notification-icon.png
│   │                   │   │   ├── settings-icon.png
│   │                   │   │   └── logout-icon.png
│   │                   │   │
│   │                   │   ├── backgrounds/
│   │                   │   │   ├── login-bg.jpg
│   │                   │   │   └── dashboard-bg.jpg
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
│   │                       └── api.properties
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── acadbridge/
│                   └── sda/
│                       ├── services/
│                       │   ├── AuthServiceTest.java
│                       │   ├── UserServiceTest.java
│                       │   └── ProjectServiceTest.java
│                       │
│                       └── utils/
│                           ├── ValidatorTest.java
│                           └── JsonParserTest.java
│
├── target/
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

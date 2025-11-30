# AcadBridge - IntelliJ IDEA Project Structure


## Project Structure

```

└── main/
    ├── java/
    │   └── sda/
    │       ├── controllers/
    │       │   ├── admin/
    │       │   │   ├── AdminDashboardController.java
    │       │   │   ├── ReportsGenerationController.java
    │       │   │   ├── RepositoryManagementController.java
    │       │   │   └── UserManagementController.java
    │       │   ├── auth/
    │       │   │   ├── LoginController.java
    │       │   │   └── RegistrationController.java
    │       │   ├── components/
    │       │   │   ├── AdminSidebarController.java
    │       │   │   └── SidebarController.java
    │       │   └── shared/
    │       │       ├── AlumniRepositoryController.java
    │       │       ├── AlumniRequestsController.java
    │       │       ├── ChatAreaController.java
    │       │       ├── ChatController.java
    │       │       ├── FeedbackRatingController.java
    │       │       ├── MentorRatingListController.java
    │       │       ├── ProjectSearchController.java
    │       │       ├── SendRequestController.java
    │       │       ├── StudentRequestsController.java
    │       │       ├── UploadProjectController.java
    │       │       ├── ViewFeedbackController.java
    │       │       └── ViewProjectController.java
    │       ├── dao/
    │       │   ├── AdminDAO.java
    │       │   ├── ChatDAO.java
    │       │   ├── FeedbackDAO.java
    │       │   ├── MentorshipDAO.java
    │       │   ├── MessageDAO.java
    │       │   ├── ProjectDAO.java
    │       │   ├── RatingDAO.java
    │       │   └── UserDAO.java
    │       ├── enums/
    │       │   └── UserRole.java
    │       ├── models/
    │       │   ├── Chat.java
    │       │   ├── Feedback.java
    │       │   ├── MentorshipRequest.java
    │       │   ├── Message.java
    │       │   ├── Project.java
    │       │   ├── ProjectDTO.java
    │       │   ├── Rating.java
    │       │   └── User.java
    │       ├── services/
    │       │   ├── AdminService.java
    │       │   ├── AuthService.java
    │       │   ├── ChatService.java
    │       │   ├── MentorshipService.java
    │       │   ├── MessageService.java
    │       │   ├── ProjectService.java
    │       │   └── RatingService.java
    │       ├── utils/
    │       │   ├── SceneManager.java
    │       │   ├── SessionManager.java
    │       │   ├── ToastHelper.java
    │       │   └── Validator.java
    │       ├── HelloApplication.java
    │       ├── HelloController.java
    │       └── Launcher.java
    │
    └── resources/
        └── sda/
            ├── css/
            │   ├── admin-dashboard-style.css
            │   ├── Alumni-Repository.css
            │   ├── chat.css
            │   ├── feedback-rating-style.css
            │   ├── login.css
            │   ├── mentor-ratinglist.css
            │   ├── message.css
            │   ├── Pending-Requests.css
            │   ├── Registration.css
            │   ├── reports-generation-style.css
            │   ├── repository-management-style.css
            │   ├── Search.css
            │   ├── Send-Requests.css
            │   ├── Upload-Project.css
            │   ├── user-management-style.css
            │   ├── view-feedback-style.css
            │   └── View-Project.css
            ├── fxml/
            │   ├── admin/
            │   │   ├── admin-dashboard-view.fxml
            │   │   ├── reports-generation-view.fxml
            │   │   ├── repository-management-view.fxml
            │   │   └── user-management-view.fxml
            │   ├── auth/
            │   │   ├── Login.fxml
            │   │   └── Registration.fxml
            │   ├── components/
            │   │   ├── admin_sidebar.fxml
            │   │   ├── alumni_sidebar.fxml
            │   │   └── student_sidebar.fxml
            │   ├── Toast.fxml
            │   └── shared/
            │       ├── Alumni-Repository.fxml
            │       ├── Alumni-Requests.fxml
            │       ├── chat.fxml
            │       ├── feedback-rating-view.fxml
            │       ├── home.fxml
            │       ├── mentor-rating.fxml
            │       ├── message.fxml
            │       ├── Pending-Requests.fxml
            │       ├── Search.fxml
            │       ├── Send-Request.fxml
            │       ├── Upload-Project.fxml
            │       ├── view-feedback.fxml
            │       ├── view-feedback-view.fxml
            │       └── View-Project.fxml

```

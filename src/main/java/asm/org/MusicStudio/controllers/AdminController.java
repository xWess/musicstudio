package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.*;
import asm.org.MusicStudio.services.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import java.util.List;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class AdminController {
    @FXML
    private TableView<User> usersTable;
    @FXML
    private TableColumn<User, Integer> idColumn;
    @FXML
    private TableColumn<User, String> nameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Boolean> activeColumn;
    @FXML
    private TableColumn<User, String> lastLoginColumn;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane contentArea;
    @FXML
    private ComboBox<String> roleFilter;

    private UserService userService;
    private User currentUser;
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    public void initialize() {
        userService = new UserServiceImpl();
        setupTableColumns();
        
        // Initialize role filter
        roleFilter.setItems(FXCollections.observableArrayList(
            "All", "Admin", "Student", "Teacher", "Artist"
        ));
        roleFilter.setValue("All");
        
        roleFilter.setOnAction(event -> {
            String selectedRole = roleFilter.getValue();
            if ("All".equals(selectedRole)) {
                loadUsers();
            } else {
                try {
                    Role role = Role.valueOf(selectedRole.toUpperCase());
                    List<User> filteredUsers = userService.getUsersByRole(role);
                    usersTable.setItems(FXCollections.observableArrayList(filteredUsers));
                } catch (Exception e) {
                    showError("Error", "Failed to filter users: " + e.getMessage());
                }
            }
        });
        
        loadUsers(); // Load initial data
    }

    @FXML
    public void setCurrentAdmin(User admin) {
        System.out.println("Validating admin role: " + admin.getRole()); // Debug log
        
        if (admin == null || admin.getRole() != Role.ADMIN) {
            System.out.println("Role validation failed. Current role: " + 
                (admin != null ? admin.getRole() : "null")); // Debug log
            showError("Access Denied", "Only administrators can access this view.");
            return;
        }
        
        this.currentUser = admin;
        statusLabel.setText("Logged in as: " + admin.getName());
        loadUsers();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        roleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getRole().toString())
        );
        activeColumn.setCellValueFactory(cellData -> cellData.getValue().activeProperty());
        lastLoginColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().lastLoginProperty().get() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().lastLoginProperty().get().format(DATE_FORMATTER)
                );
            }
            return new SimpleStringProperty("Never");
        });
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            usersTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    private void showAddUserDialog() {
        // Similar to registration dialog but with admin privileges
    }

    @FXML
    private void showEditUserDialog() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user to edit");
            return;
        }
        // Show edit dialog
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user to delete");
            return;
        }

        if (selectedUser.getId() == currentUser.getId()) {
            showError("Invalid Operation", "Cannot delete current admin user");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete User");
        alert.setHeaderText("Delete User Confirmation");
        alert.setContentText("Are you sure you want to delete this user?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.deleteUserById(selectedUser.getId());
                    loadUsers();
                    statusLabel.setText("User deleted successfully");
                } catch (Exception e) {
                    showError("Error deleting user", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleLogout() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/LoginView.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error", "Could not load login view");
        }
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Add methods for course management
    @FXML
    private void showCoursesManagement() {
        // Implement course management view
    }

    @FXML
    private void showReports() {
        // Implement reports view
    }

    public void initData(User user) {
        setCurrentAdmin(user);
        statusLabel.setText("Logged in as: " + user.getName());
        loadUsers();
    }
}
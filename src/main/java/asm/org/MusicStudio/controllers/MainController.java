package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class MainController {
    @FXML
    private StackPane contentArea;
    
    @FXML
    private TableView<User> userTable;
    
    @FXML
    private TableColumn<User, Integer> idColumn;
    
    @FXML
    private TableColumn<User, String> nameColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, Role> roleColumn;
    
    @FXML
    private Label statusLabel;
    
    private UserService userService;
    
    @FXML
    public void initialize() {
        // Initialize columns
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
        
        // Don't refresh table here - will do it after userService is set
    }
    
    public void setUserService(UserService userService) {
        this.userService = userService;
        // Refresh table after service is set
        refreshUserTable();
    }
    
    private void refreshUserTable() {
        if (userService != null) {
            userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        }
    }
    
    @FXML
    private void handleExit() {
        Stage stage = (Stage) contentArea.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Music Studio");
        alert.setHeaderText(null);
        alert.setContentText("Music Studio Application\nVersion 1.0");
        alert.showAndWait();
    }

    @FXML
    private void showUsers() {
        userTable.setVisible(true);
        // Here you would typically load users from your service
        // userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        statusLabel.setText("Showing users");
    }
    
    @FXML
    private void showSchedule() {
        userTable.setVisible(false);
        statusLabel.setText("Schedule view not implemented yet");
    }
    
    @FXML
    private void showSettings() {
        userTable.setVisible(false);
        statusLabel.setText("Settings view not implemented yet");
    }
    
    @FXML
    private void showAddUserDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        TextField emailField = new TextField();
        ComboBox<Role> roleComboBox = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleComboBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Role selectedRole = roleComboBox.getValue();
                    User newUser;
                    switch (selectedRole) {
                        case TEACHER -> newUser = new Teacher();
                        case STUDENT -> newUser = new Student();
                        case ARTIST -> newUser = new Artist();
                        default -> throw new IllegalArgumentException("Invalid role");
                    }
                    newUser.setName(nameField.getText());
                    newUser.setEmail(emailField.getText());
                    newUser.setRole(selectedRole);
                    
                    userService.addUser(newUser);
                    refreshUserTable();
                    statusLabel.setText("User added successfully");
                    return newUser;
                } catch (Exception e) {
                    showError("Error adding user", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void showEditUserDialog() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user to edit");
            return;
        }
        
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(selectedUser.getName());
        TextField emailField = new TextField(selectedUser.getEmail());
        ComboBox<Role> roleComboBox = new ComboBox<>(FXCollections.observableArrayList(Role.values()));
        roleComboBox.setValue(selectedUser.getRole());
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleComboBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    selectedUser.setName(nameField.getText());
                    selectedUser.setEmail(emailField.getText());
                    selectedUser.setRole(roleComboBox.getValue());
                    
                    userService.updateUser(selectedUser);
                    refreshUserTable();
                    statusLabel.setText("User updated successfully");
                    return selectedUser;
                } catch (Exception e) {
                    showError("Error updating user", e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void deleteSelectedUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("No Selection", "Please select a user to delete");
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
                    refreshUserTable();
                    statusLabel.setText("User deleted successfully");
                } catch (Exception e) {
                    showError("Error deleting user", e.getMessage());
                }
            }
        });
    }
    
    private GridPane createUserFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        return grid;
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

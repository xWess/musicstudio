package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import asm.org.MusicStudio.dialogs.PasswordChangeDialog;
import java.util.Optional;

public class ProfileViewController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Label roleLabel;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Button saveButton;

    private UserService userService;
    private User currentUser;

    @FXML
    public void initialize() {
        userService = new UserServiceImpl();
        
        // Style the labels
        nameLabel.setTextFill(Color.web("#2196F3"));  // Material Blue
        emailLabel.setTextFill(Color.web("#2196F3"));
        roleLabel.setTextFill(Color.web("#2196F3"));
        
        // Add listeners for text changes
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(false);
        });
        
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            saveButton.setDisable(false);
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        loadUserProfile();
    }

    @FXML
    public void showChangePasswordDialog() {
        try {
            PasswordChangeDialog dialog = new PasswordChangeDialog(currentUser);
            Optional<ButtonType> result = dialog.showAndWait();
            
            if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                String currentPassword = dialog.getCurrentPassword();
                String newPassword = dialog.getNewPassword();
                String confirmPassword = dialog.getConfirmPassword();
                
                if (!newPassword.equals(confirmPassword)) {
                    showError("Error", "New passwords do not match!");
                    return;
                }
                
                userService.updatePassword(currentUser, currentPassword, newPassword);
                showSuccess("Success", "Password updated successfully");
            }
        } catch (Exception e) {
            showError("Error", "Failed to change password: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            roleLabel.setText("Role: " + currentUser.getRole().toString().toUpperCase());
            saveButton.setDisable(true);
        }
    }

    @FXML
    public void saveProfileChanges() {
        try {
            if (currentUser != null) {
                // Update the user object
                currentUser.setName(nameField.getText().trim());
                String newEmail = emailField.getText().trim();
                
                // Validate email format
                if (!isValidEmail(newEmail)) {
                    showError("Error", "Please enter a valid email address");
                    return;
                }
                
                currentUser.setEmail(newEmail);
                
                try {
                    // Update in database
                    userService.updateUser(currentUser);
                    showSuccess("Success", "Profile updated successfully");
                    saveButton.setDisable(true);
                } catch (Exception e) {
                    if (e.getMessage().contains("users_email_key") || 
                        e.getMessage().contains("clé dupliquée")) {
                        showError("Email Error", "This email address is already in use");
                        emailField.setText(currentUser.getEmail()); // Reset to original email
                    } else {
                        throw e;
                    }
                }
            }
        } catch (Exception e) {
            showError("Error", "Failed to update profile: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
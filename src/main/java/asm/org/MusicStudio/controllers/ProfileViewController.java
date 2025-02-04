package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import asm.org.MusicStudio.dialogs.PasswordChangeDialog;
import java.util.Optional;

public class ProfileViewController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Label roleLabel;

    private UserService userService;
    private User currentUser;

    @FXML
    public void initialize() {
        userService = new UserServiceImpl();
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
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
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

    @FXML
    public void saveProfileChanges() {
        try {
            currentUser.setName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            
            userService.updateUser(currentUser);
            showSuccess("Success", "Profile updated successfully");
        } catch (Exception e) {
            showError("Error", "Failed to update profile: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            roleLabel.setText(currentUser.getRole().toString());
        }
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
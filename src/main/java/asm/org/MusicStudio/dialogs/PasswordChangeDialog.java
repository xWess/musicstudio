package asm.org.MusicStudio.dialogs;

import asm.org.MusicStudio.entity.User;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.Node;

public class PasswordChangeDialog extends Dialog<ButtonType> {
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;

    public PasswordChangeDialog(User user) {
        setTitle("Change Password");
        setHeaderText("Change password for " + user.getName());

        // Set the button types
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Enter current password");
        
        newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Enter new password");
        
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm new password");

        grid.add(new Label("Current Password:"), 0, 0);
        grid.add(currentPasswordField, 1, 0);
        grid.add(new Label("New Password:"), 0, 1);
        grid.add(newPasswordField, 1, 1);
        grid.add(new Label("Confirm Password:"), 0, 2);
        grid.add(confirmPasswordField, 1, 2);

        getDialogPane().setContent(grid);

        // Enable/Disable change button depending on whether fields are filled
        Node changeButton = getDialogPane().lookupButton(changeButtonType);
        changeButton.setDisable(true);

        // Add listeners to enable/disable the change button
        currentPasswordField.textProperty().addListener((observable, oldValue, newValue) -> 
            validateFields(changeButton));
        newPasswordField.textProperty().addListener((observable, oldValue, newValue) -> 
            validateFields(changeButton));
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> 
            validateFields(changeButton));
    }

    private void validateFields(Node changeButton) {
        boolean fieldsEmpty = getCurrentPassword().trim().isEmpty() ||
                            getNewPassword().trim().isEmpty() ||
                            getConfirmPassword().trim().isEmpty();
        boolean passwordsMatch = getNewPassword().equals(getConfirmPassword());
        
        changeButton.setDisable(fieldsEmpty || !passwordsMatch);
    }

    public String getCurrentPassword() {
        return currentPasswordField.getText();
    }

    public String getNewPassword() {
        return newPasswordField.getText();
    }

    public String getConfirmPassword() {
        return confirmPasswordField.getText();
    }
} 
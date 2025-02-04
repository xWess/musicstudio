package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.services.AuthService;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;
import asm.org.MusicStudio.exception.AuthenticationException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import javafx.scene.Parent;
import asm.org.MusicStudio.util.WindowManager;
import asm.org.MusicStudio.MusicStudioApplication;

public class LoginController {
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    private AuthService authService;
    private UserService userService;
    
    public void initialize() {
        authService = AuthService.createDefault();
        userService = new UserServiceImpl();
        errorLabel.setVisible(false);
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();
        
        try {
            User user = authService.login(email, password);
            if (user != null) {
                handleSuccessfulLogin(user);
            }
        } catch (AuthenticationException e) {
            errorLabel.setText("Invalid email or password");
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void showRegisterDialog() {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Register New User");
        dialog.setHeaderText("Please enter your details");

        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Student", "Teacher", "Artist");
        roleCombo.setValue("Student");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Role:"), 0, 3);
        grid.add(roleCombo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                try {
                    // Basic validation
                    if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                        passwordField.getText().isEmpty()) {
                        errorLabel.setText("All fields are required");
                        errorLabel.setVisible(true);
                        return null;
                    }

                    Role selectedRole = Role.valueOf(roleCombo.getValue().toUpperCase());
                    // Create appropriate user type based on role
                    User newUser;
                    switch (selectedRole) {
                        case STUDENT:
                            newUser = new Student();
                            break;
                        case TEACHER:
                            newUser = new Teacher();
                            break;
                        case ARTIST:
                            newUser = new Artist();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid role selected");
                    }
                    // Set user properties
                    newUser.setName(nameField.getText());
                    newUser.setEmail(emailField.getText());
                    newUser.setPassword(passwordField.getText());
                    
                    // Register the user
                    User registeredUser = authService.register(newUser, passwordField.getText());
                    errorLabel.setText("Registration successful! Please login.");
                    errorLabel.setVisible(true);
                    return registeredUser;
                } catch (Exception e) {
                    errorLabel.setText("Registration failed: " + e.getMessage());
                    errorLabel.setVisible(true);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    
    private void openMainWindow(User user) {
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
            Parent root = loader.load();
            
            // Get the controller and initialize it
            MainController controller = loader.getController();
            if (controller == null) {
                throw new IllegalStateException("Failed to get MainController");
            }
            
            // Set up the controller
            controller.setCurrentUser(user);
            controller.initializeData();
            
            // Create and show the new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
            
            Stage mainStage = new Stage();
            mainStage.setTitle("Music Studio - " + user.getName());
            mainStage.setScene(scene);
            
            // Close login window
            ((Stage) emailField.getScene().getWindow()).close();
            
            // Show main window
            mainStage.show();
            
        } catch (IOException e) {
            e.printStackTrace(); // Print the full stack trace
            showError("Application Error", 
                     "Failed to load main window: " + e.getMessage() + 
                     "\nCause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Application Error", 
                     "Unexpected error: " + e.getMessage());
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void openAdminWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdminView.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the user
            AdminController adminController = loader.getController();
            adminController.setCurrentAdmin(user);  // Changed from initData to setCurrentAdmin
            
            // Create new scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Music Studio Admin Panel");
            stage.setScene(scene);
            
            // Close the login window
            Stage loginStage = (Stage) emailField.getScene().getWindow();
            loginStage.close();
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading admin window: " + e.getMessage());
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load admin window");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void handleSuccessfulLogin(User user) {
        try {
            System.out.println("Login successful, user role: " + user.getRole());
            
            // Load the main view for all users
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
            Parent view = loader.load();
            
            // Get the controller and initialize with user
            MainController controller = loader.getController();
            if (controller != null) {
                controller.setCurrentUser(user);
                controller.initializeData();
            } else {
                throw new IllegalStateException("Failed to get MainController");
            }
            
            // Update scene
            Scene scene = emailField.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.setScene(new Scene(view));
            stage.setTitle("Music Studio - " + user.getRole().toString());
            
        } catch (Exception e) {
            System.err.println("Error switching to main view: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to load main view: " + e.getMessage());
        }
    }

    private void switchToMainView(Parent mainView) {
        Stage stage = new Stage();
        Scene scene = new Scene(mainView);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        
        stage.setTitle("Music Studio Management");
        stage.setScene(scene);
        
        // Configure main window using the utility method
        WindowManager.configureMainWindow(stage);
        
        stage.show();
        
        // Close login window
        ((Stage) emailField.getScene().getWindow()).close();
    }
} 
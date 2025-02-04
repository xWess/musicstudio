package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.services.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import java.util.List;
import java.util.Collections;
import javafx.beans.property.SimpleStringProperty;

public class UserViewController {
    @FXML 
    private VBox userViewRoot;
    
    private UserService userService;
    private User currentUser;

    @FXML
    private TableView<User> userTable;
    
    @FXML
    private TableColumn<User, Integer> idColumn;
    
    @FXML
    private TableColumn<User, String> nameColumn;
    
    @FXML
    private TableColumn<User, String> emailColumn;
    
    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    public void initialize() {
        System.out.println("Initializing UserViewController...");
        setupTableColumns();
        
        // Add a listener to wait for the scene to be ready
        userTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                System.out.println("Scene is now ready");
                if (isReadyToLoad()) {
                    loadUsers();
                }
            }
        });
    }

    private void setupTableColumns() {
        System.out.println("Setting up table columns...");
        
        if (userTable == null) {
            System.err.println("ERROR: userTable is null in setupTableColumns()");
            return;
        }

        try {
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            roleColumn.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getRole().toString()));

            // Initialize with empty list
            userTable.setItems(FXCollections.observableArrayList());
            System.out.println("Table columns setup completed successfully");

        } catch (Exception e) {
            System.err.println("Error setting up table columns: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setUserService(UserService service) {
        System.out.println("Setting UserService in UserViewController");
        this.userService = service;
        checkAndLoadUsers();
    }

    public void setCurrentUser(User user) {
        if (user == null) {
            System.err.println("ERROR: Attempting to set null user in UserViewController");
            return;
        }

        try {
            System.out.println("Setting current user:");
            System.out.println(" - Name: " + user.getName());
            System.out.println(" - Email: " + user.getEmail());
            System.out.println(" - Role: " + user.getRole());
            
            this.currentUser = user;
            
            // Verify the user was set correctly
            if (this.currentUser != null) {
                System.out.println("Current user set successfully");
                System.out.println("Stored user role: " + this.currentUser.getRole());
            } else {
                System.err.println("Failed to set current user - still null after assignment");
            }
            
            checkAndLoadUsers();
            
        } catch (Exception e) {
            System.err.println("Error in setCurrentUser: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void checkAndLoadUsers() {
        System.out.println("\nChecking if ready to load users...");
        
        if (currentUser == null) {
            System.err.println("checkAndLoadUsers: currentUser is null!");
            return;
        }
        
        System.out.println("Current state:");
        System.out.println(" - Current user: " + currentUser.getName());
        System.out.println(" - User role: " + currentUser.getRole());
        System.out.println(" - Service ready: " + (userService != null));
        System.out.println(" - Table ready: " + (userTable != null));
        System.out.println(" - Scene ready: " + (userTable != null && userTable.getScene() != null));
        
        if (Platform.isFxApplicationThread()) {
            if (isReadyToLoad()) {
                System.out.println("All dependencies ready - loading users");
                loadUsers();
            } else {
                System.out.println("Not all dependencies are ready yet");
            }
        } else {
            System.out.println("Not on FX thread - scheduling load");
            Platform.runLater(this::checkAndLoadUsers);
        }
    }

    private boolean isReadyToLoad() {
        boolean serviceReady = userService != null;
        boolean userReady = currentUser != null;
        boolean tableReady = userTable != null;
        boolean sceneReady = userTable != null && userTable.getScene() != null;
        
        System.out.println("Checking ready state:");
        System.out.println(" - service: " + serviceReady + 
                         "\n - user: " + userReady + 
                         (userReady ? " (Role: " + currentUser.getRole() + ")" : "") +
                         "\n - table: " + tableReady +
                         "\n - scene: " + sceneReady);
                         
        return serviceReady && userReady && tableReady && sceneReady;
    }

    public void loadUsers() {
        System.out.println("Attempting to load users...");
        
        if (!isReadyToLoad()) {
            System.out.println("Not ready to load users - missing dependencies");
            return;
        }

        try {
            System.out.println("Current user role: " + currentUser.getRole());
            
            if (currentUser.getRole() != Role.ADMIN) {
                System.out.println("Loading single user view for non-admin");
                userTable.setItems(FXCollections.observableArrayList(
                    Collections.singletonList(currentUser)));
            } else {
                System.out.println("Loading all users for admin view");
                List<User> users = userService.getAllUsers();
                userTable.setItems(FXCollections.observableArrayList(users));
                System.out.println("Loaded " + users.size() + " users");
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
            e.printStackTrace();
            showError("Error", "Failed to load users: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void showAddUserDialog() {
        // Implement add user dialog
    }
    
    @FXML
    public void showEditUserDialog() {
        // Implement edit user dialog
    }
    
    @FXML
    public void deleteSelectedUser() {
        // Implement delete user functionality
    }
} 
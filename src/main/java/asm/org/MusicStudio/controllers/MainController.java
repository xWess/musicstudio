package asm.org.MusicStudio.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import asm.org.MusicStudio.entity.Artist;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.Role;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.entity.Teacher;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.services.PaymentService;
import asm.org.MusicStudio.services.ScheduleService;
import asm.org.MusicStudio.services.UserService;
import asm.org.MusicStudio.services.UserServiceImpl;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {
    @FXML
    private BorderPane root;

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
    private TableColumn<User, String> roleColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox paymentsContent;

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    private TableColumn<Payment, LocalDateTime> paymentDateColumn;

    @FXML
    private TableColumn<Payment, String> paymentDescriptionColumn;

    @FXML
    private TableColumn<Payment, BigDecimal> paymentAmountColumn;

    @FXML
    private TableColumn<Payment, String> paymentStatusColumn;

    @FXML
    private VBox usersContent;

    @FXML
    private Button usersButton;
    @FXML
    private Button paymentsButton;
    @FXML
    private Button scheduleButton;
    @FXML
    private Button settingsButton;
    @FXML
    private Button enrollmentsButton;
    @FXML
    private Button roomsButton;
    @FXML
    private Button profileButton;
    @FXML
    private Button studentsButton;

    @FXML
    private VBox scheduleContent;

    @FXML
    private VBox enrollmentsContent;

    @FXML
    private VBox roomsContent;

    @FXML
    private VBox profileContent;

    @FXML
    private ProfileViewController profileViewController;

    @FXML
    private UserViewController usersViewController;

    @FXML
    private VBox filesContent;

    @FXML
    private Button filesButton;

    @FXML
    private Button fileManagementButton;

    @FXML
    private FileManagementController fileManagementController;

    private UserService userService;
    private PaymentService paymentService;
    private ScheduleService scheduleService;
    private User currentUser;

    @FXML
    public void initialize() {
        try {
            // Initialize services
            userService = new UserServiceImpl();
            paymentService = new PaymentService();
            scheduleService = new ScheduleService();
            
            // Initialize the users controller if it exists
            if (usersViewController != null) {
                usersViewController.setUserService(userService);
            }
            
            // Hide all content initially
            hideAllContent();
            
            // Set initial status
            if (statusLabel != null) {
                statusLabel.setText("Ready");
            }
            
            // Initialize table columns only if they exist
            initializeTables();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Initialization Error", "Failed to initialize main window: " + e.getMessage());
        }
    }

    private void initializeTables() {
        // Initialize user table if it exists
        if (userTable != null) {
            userTable.setItems(FXCollections.observableArrayList());
            
            if (idColumn != null) {
                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            }
            if (nameColumn != null) {
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            }
            if (emailColumn != null) {
                emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            }
            if (roleColumn != null) {
                roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
            }
        }

        // Initialize payment table if it exists
        if (paymentTable != null) {
            paymentTable.setItems(FXCollections.observableArrayList());
            
            if (paymentDateColumn != null) {
                paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
            }
            if (paymentDescriptionColumn != null) {
                paymentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
            }
            if (paymentAmountColumn != null) {
                paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
            }
            if (paymentStatusColumn != null) {
                paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            }
        }
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
        // Don't refresh here - it will be done in showUsers()
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
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

    private void clearSelectedButtons() {
        usersButton.getStyleClass().remove("selected");
        paymentsButton.getStyleClass().remove("selected");
        scheduleButton.getStyleClass().remove("selected");
        settingsButton.getStyleClass().remove("selected");
        enrollmentsButton.getStyleClass().remove("selected");
        roomsButton.getStyleClass().remove("selected");
        profileButton.getStyleClass().remove("selected");
        studentsButton.getStyleClass().remove("selected");
    }

    @FXML
    private void showUsers() {
        try {
            // Check permissions
            if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
                showError("Access Denied", "You do not have permission to view this section.");
                return;
            }

            System.out.println("Showing users view...");
            
            // Hide all content first
            hideAllContent();
            
            // Load the UserView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserView.fxml"));
            Node userView = loader.load();
            
            // Get and configure the controller
            UserViewController controller = loader.getController();
            if (controller == null) {
                throw new RuntimeException("Failed to get UserViewController");
            }
            
            // Set up the controller
            controller.setUserService(userService);
            controller.setCurrentUser(currentUser);
            
            // Add view to content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(userView);
            System.out.println("Users view added to content area");
            
            // Load the users
            controller.loadUsers();
            System.out.println("Users loaded");
            
            // Update navigation state
            updateNavButtonStates(usersButton);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load user view: " + e.getMessage());
        }
    }

    @FXML
    private void showSchedule() {
        try {
            if (currentUser == null || currentUser.getRole() != Role.TEACHER) {
                showError("Access Denied", "Only teachers can access the schedule view.");
                return;
            }

            System.out.println("Loading schedule view for teacher ID: " + currentUser.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ScheduleView.fxml"));
            VBox scheduleView = loader.load();
            
            ScheduleViewController controller = loader.getController();
            controller.setCurrentInstructorId(currentUser.getId());
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(scheduleView);
            
            updateNavButtonStates(scheduleButton);
            statusLabel.setText("Schedule view loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load schedule view: " + e.getMessage());
        }
    }

    @FXML
    private void showEnrollments() {
        try {
            System.out.println("Loading enrollments view...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnrollmentView.fxml"));
            VBox enrollmentView = loader.load();
            
            EnrollmentViewController controller = loader.getController();
            // Set any necessary properties on the controller
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(enrollmentView);
            
            updateNavButtonStates(enrollmentsButton);
            statusLabel.setText("Enrollments view loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load enrollments view: " + e.getMessage());
        }
    }

    @FXML
    private void showRooms() {
        loadView("/fxml/RoomView.fxml", roomsButton);
    }

    @FXML
    private void showProfile() {
        loadView("/fxml/ProfileView.fxml", profileButton);
    }

    private void loadView(String fxmlPath, Button selectedButton) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            
            // Clear existing content and add new view
            if (contentArea != null) {
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
            
            // Update selected button state only if the button exists
            if (selectedButton != null) {
                updateNavButtonStates(selectedButton);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load view: " + e.getMessage());
        }
    }

    private void updateNavButtonStates(Button selectedButton) {
        // Remove selected class from all buttons if they exist
        if (usersButton != null) usersButton.getStyleClass().remove("selected");
        if (paymentsButton != null) paymentsButton.getStyleClass().remove("selected");
        if (scheduleButton != null) scheduleButton.getStyleClass().remove("selected");
        if (settingsButton != null) settingsButton.getStyleClass().remove("selected");
        if (enrollmentsButton != null) enrollmentsButton.getStyleClass().remove("selected");
        if (roomsButton != null) roomsButton.getStyleClass().remove("selected");
        if (profileButton != null) profileButton.getStyleClass().remove("selected");
        if (studentsButton != null) studentsButton.getStyleClass().remove("selected");
        
        // Add selected class to the clicked button if it exists
        if (selectedButton != null) {
            selectedButton.getStyleClass().add("selected");
        }
    }

    @FXML
    private void showSettings() {
        clearSelectedButtons();
        settingsButton.getStyleClass().add("selected");
        hideAllContent();
        statusLabel.setText("Settings view not implemented yet");
    }

    @FXML
    private void showPayments() {
        clearSelectedButtons();
        paymentsButton.getStyleClass().add("selected");
        usersContent.setVisible(false);
        usersContent.setManaged(false);
        paymentsContent.setVisible(true);
        paymentsContent.setManaged(true);
        refreshPaymentTable();
    }

    private void hideAllContent() {
        // Hide all content containers
        if (usersContent != null) {
            usersContent.setVisible(false);
            usersContent.setManaged(false);
        }
        if (paymentsContent != null) {
            paymentsContent.setVisible(false);
            paymentsContent.setManaged(false);
        }
        if (scheduleContent != null) {
            scheduleContent.setVisible(false);
            scheduleContent.setManaged(false);
        }
        if (enrollmentsContent != null) {
            enrollmentsContent.setVisible(false);
            enrollmentsContent.setManaged(false);
        }
        if (roomsContent != null) {
            roomsContent.setVisible(false);
            roomsContent.setManaged(false);
        }
        if (profileContent != null) {
            profileContent.setVisible(false);
            profileContent.setManaged(false);
        }
        if (filesContent != null) {
            filesContent.setVisible(false);
            filesContent.setManaged(false);
        }

        // Clear any selections
        if (userTable != null)
            userTable.getSelectionModel().clearSelection();
        if (paymentTable != null)
            paymentTable.getSelectionModel().clearSelection();
    }

    private void refreshUserTable() {
        if (userService != null) {
            try {
                List<User> users = userService.getAllUsers();
                userTable.setItems(FXCollections.observableArrayList(users));
            } catch (Exception e) {
                showError("Error", "Failed to load users: " + e.getMessage());
            }
        }
    }

    private void refreshPaymentTable() {
        if (paymentService != null && paymentTable != null && getCurrentUser() != null) {
            try {
                List<Payment> payments = paymentService.getUserPayments(getCurrentUser());
                paymentTable.setItems(FXCollections.observableArrayList(payments));
                statusLabel.setText("Payments loaded successfully");
            } catch (Exception e) {
                showError("Error", "Failed to load payments: " + e.getMessage());
                statusLabel.setText("Failed to load payments");
            }
        }
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

    @FXML
    private void showNewPaymentDialog() {
        Dialog<Payment> dialog = new Dialog<>();
        dialog.setTitle("Enroll in a Course");
        dialog.setHeaderText("Course Enrollment");

        ButtonType enrollButtonType = new ButtonType("Enroll & Pay", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(enrollButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<Course> courseCombo = new ComboBox<>();
        try {
            courseCombo.getItems().addAll(paymentService.getAvailableCourses());

            // Fix the display format
            courseCombo.setButtonCell(new CourseListCell());
            courseCombo.setCellFactory(lv -> new CourseListCell());

        } catch (SQLException e) {
            showError("Error", "Failed to load courses");
            return;
        }

        Spinner<Integer> monthsSpinner = new Spinner<>(1, 12, 1);
        Label totalLabel = new Label("$0.00");

        grid.add(new Label("Select Course:"), 0, 0);
        grid.add(courseCombo, 1, 0);
        grid.add(new Label("Number of Months:"), 0, 1);
        grid.add(monthsSpinner, 1, 1);
        grid.add(new Label("Total Amount:"), 0, 2);
        grid.add(totalLabel, 1, 2);

        // Update total when course or months change
        courseCombo.valueProperty()
                .addListener((obs, oldVal, newVal) -> updateTotal(newVal, monthsSpinner.getValue(), totalLabel));

        monthsSpinner.valueProperty()
                .addListener((obs, oldVal, newVal) -> updateTotal(courseCombo.getValue(), newVal, totalLabel));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == enrollButtonType) {
                try {
                    Course selectedCourse = courseCombo.getValue();
                    int months = monthsSpinner.getValue();

                    Payment payment = paymentService.processEnrollmentPayment(
                            getCurrentUser(),
                            selectedCourse,
                            months);

                    showEnrollmentSuccess(selectedCourse, months);
                    refreshPaymentTable();
                    return payment;
                } catch (SQLException e) {
                    showError("Error", "Failed to process enrollment: " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void updateTotal(Course course, int months, Label totalLabel) {
        if (course != null) {
            double total = course.getMonthlyFee() * months;
            totalLabel.setText(String.format("$%.2f", total));
        }
    }

    private void showEnrollmentSuccess(Course course, int months) {
        showAlert("Enrollment Successful",
                String.format("""
                        You have been enrolled in %s
                        Duration: %d month(s)
                        Schedule: %s
                        Instructor: %s

                        Please check your email for detailed information.
                        """,
                        course.getName(),
                        months,
                        course.getSchedule(),
                        course.getInstructor() != null ? course.getInstructor().getName() : "No instructor assigned"));
    }

    @FXML
    public void showEnrollmentDialog() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Course Enrollment");
            dialog.setHeaderText("Enroll in a New Course");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnrollmentDialog.fxml"));
            dialog.getDialogPane().setContent(loader.load());
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                EnrollmentDialogController controller = loader.getController();
                controller.processEnrollment();
                showEnrollmentsView(); // Refresh the view
            }
        } catch (IOException e) {
            showError("Error", "Failed to open enrollment dialog: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Setting current user: " + user.getName());
        
        // Configure UI based on user role
        if (user.getRole() != Role.ADMIN) {
            // Hide users view for non-admins
            if (usersButton != null) {
                usersButton.setVisible(false);
                usersButton.setManaged(false);
            }
            // Show appropriate default view (e.g., schedule)
            showSchedule();
        } else {
            // Show users view for admin
            if (usersButton != null) {
                usersButton.setVisible(true);
                usersButton.setManaged(true);
            }
            showUsers();
        }
        
        // Update status label
        if (statusLabel != null) {
            statusLabel.setText("Welcome, " + user.getName());
        }
    }

    private void configureUIForUserRole() {
        if (currentUser == null) return;

        // Hide/show navigation buttons based on role
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        
        if (usersButton != null) {
            usersButton.setVisible(isAdmin);
            usersButton.setManaged(isAdmin);
        }

        // Show appropriate default view based on role
        if (isAdmin) {
            showUsers();
        } else {
            showSchedule(); // or any other appropriate default view
        }
    }

    private User getCurrentUser() {
        if (currentUser == null) {
            // For testing purposes, you might want to create a dummy user
            // In production, you should handle this properly with login
            showAlert("Error", "No user is currently logged in!");
            return null;
        }
        return currentUser;
    }

    public void initializeData() {
        if (userService != null) {
            refreshUserTable();
            statusLabel.setText("Application ready");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add this inner class to format course display
    private static class CourseListCell extends ListCell<Course> {
        @Override
        protected void updateItem(Course course, boolean empty) {
            super.updateItem(course, empty);
            if (empty || course == null) {
                setText(null);
            } else {
                setText(String.format("%s with %s - $%.2f/month",
                        course.getName(),
                        course.getInstructor() != null ? course.getInstructor().getName() : "No instructor assigned",
                        course.getMonthlyFee()));
            }
        }
    }

    private void filterUsersByRole(Role selectedRole) {
        if (selectedRole == null) {
            userTable.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        } else {
            ObservableList<User> filteredUsers = FXCollections.observableArrayList(userService.getAllUsers().stream()
                    .filter(user -> user.getRole() == selectedRole)
                    .toList());
            userTable.setItems(filteredUsers);
        }
    }

    private void loadEnrollments() {
        try {
            // Clear existing content
            contentArea.getChildren().clear();
            
            // Load the enrollment view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnrollmentView.fxml"));
            VBox enrollmentView = loader.load();
            
            // Add the view to the content area
            contentArea.getChildren().add(enrollmentView);
            
            // Update selected button state
            updateNavButtonStates(enrollmentsButton);
            
            // Update status
            statusLabel.setText("Enrollments loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load enrollments: " + e.getMessage());
        }
    }

    @FXML
    public void showRoomBookingDialog() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Book Practice Room");
            dialog.setHeaderText("Room Booking");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            ComboBox<String> roomCombo = new ComboBox<>();
            roomCombo.getItems().addAll("Room 101", "Room 102", "Room 103");
            
            DatePicker datePicker = new DatePicker(LocalDate.now());
            
            ComboBox<String> timeSlotCombo = new ComboBox<>();
            timeSlotCombo.getItems().addAll(
                "09:00 AM - 10:00 AM",
                "10:00 AM - 11:00 AM",
                "11:00 AM - 12:00 PM"
            );

            grid.add(new Label("Room:"), 0, 0);
            grid.add(roomCombo, 1, 0);
            grid.add(new Label("Date:"), 0, 1);
            grid.add(datePicker, 1, 1);
            grid.add(new Label("Time:"), 0, 2);
            grid.add(timeSlotCombo, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Process room booking
                showSuccess("Success", "Room booked successfully!");
                showRoomsView(); // Refresh the view
            }
        } catch (Exception e) {
            showError("Error", "Failed to open room booking dialog: " + e.getMessage());
        }
    }

    @FXML
    public void showEnrollmentsView() {
        try {
            System.out.println("Loading enrollments view...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EnrollmentView.fxml"));
            VBox enrollmentView = loader.load();
            
            EnrollmentViewController controller = loader.getController();
            // Initialize any controller properties if needed
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(enrollmentView);
            
            updateNavButtonStates(enrollmentsButton);
            statusLabel.setText("Enrollments view loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load enrollments view: " + e.getMessage());
        }
    }

    @FXML
    public void showRoomsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoomView.fxml"));
            Node roomView = loader.load();
            RoomViewController controller = loader.getController();
            
            contentArea.getChildren().setAll(roomView);
        } catch (IOException e) {
            showError("Error", "Failed to load rooms view: " + e.getMessage());
        }
    }

    @FXML
    public void showProfileView() {
        loadView("/fxml/ProfileView.fxml", profileButton);
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Music Studio Login");
            stage.show();

            // Close current window
            ((Stage) contentArea.getScene().getWindow()).close();
        } catch (IOException e) {
            showError("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void saveProfileChanges() {
        if (profileViewController != null) {
            profileViewController.saveProfileChanges();
        }
    }

    @FXML
    public void showChangePasswordDialog() {
        if (profileViewController != null) {
            profileViewController.showChangePasswordDialog();
        }
    }

    // Add this method to UserViewController if it doesn't exist
    public void loadUsers() {
        if (userTable != null && userService != null) {
            try {
                List<User> users = userService.getAllUsers();
                userTable.setItems(FXCollections.observableArrayList(users));
            } catch (Exception e) {
                // Handle the error appropriately
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void toggleFullScreen() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    private void showStudents() {
        try {
            System.out.println("Attempting to show students view...");
            // Only show for teachers
            if (currentUser == null || currentUser.getRole() != Role.TEACHER) {
                System.out.println("Access denied - current user role: " + 
                    (currentUser != null ? currentUser.getRole() : "null"));
                showError("Access Denied", "Only teachers can access the students view.");
                return;
            }

            System.out.println("Loading students view for teacher ID: " + currentUser.getId());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/StudentsView.fxml"));
            VBox studentsView = loader.load();
            
            StudentsViewController controller = loader.getController();
            controller.setUserService(userService);
            controller.setCurrentInstructorId(currentUser.getId());
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(studentsView);
            
            updateNavButtonStates(studentsButton);
            statusLabel.setText("Students view loaded successfully");
            System.out.println("Students view loaded successfully");
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error", "Failed to load students view: " + e.getMessage());
        }
    }

    @FXML
    public void showFileManagementView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FileManagement.fxml"));
            Node fileManagementView = loader.load();
            
            FileManagementController controller = loader.getController();
            controller.setInstructorId(currentUser.getId());
            
            contentArea.getChildren().setAll(fileManagementView);
            updateNavButtonStates(fileManagementButton);
            
        } catch (IOException e) {
            showError("Error", "Failed to load file management view: " + e.getMessage());
        }
    }

    @FXML
    public void showFiles() {
        hideAllContent();
        filesContent.setVisible(true);
        if (fileManagementController != null && currentUser != null) {
            fileManagementController.setInstructorId(currentUser.getId());
            fileManagementController.initialize(); // Reinitialize the controller
        }
    }
}

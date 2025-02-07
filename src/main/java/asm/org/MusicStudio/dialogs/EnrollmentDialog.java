package asm.org.MusicStudio.dialogs;

import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Course;
import asm.org.MusicStudio.entity.User;
import asm.org.MusicStudio.entity.Role;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import java.time.LocalDate;

public class EnrollmentDialog extends Dialog<Enrollment> {
    private ComboBox<String> courseComboBox;
    private ComboBox<String> semesterComboBox;
    private TextField instructorField;
    private TextField scheduleField;

    public EnrollmentDialog() {
        setTitle("Enroll in Course");
        
        DialogPane dialogPane = getDialogPane();
        VBox content = new VBox(10);
        
        // Create UI components
        courseComboBox = new ComboBox<>();
        courseComboBox.getItems().addAll("Piano Basics", "Guitar 101", "Voice Training");
        
        semesterComboBox = new ComboBox<>();
        semesterComboBox.getItems().addAll("Spring 2024", "Summer 2024", "Fall 2024");
        
        instructorField = new TextField();
        instructorField.setEditable(false);
        
        scheduleField = new TextField();
        scheduleField.setEditable(false);

        // Create layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Course:"), 0, 0);
        grid.add(courseComboBox, 1, 0);
        grid.add(new Label("Semester:"), 0, 1);
        grid.add(semesterComboBox, 1, 1);
        grid.add(new Label("Instructor:"), 0, 2);
        grid.add(instructorField, 1, 2);
        grid.add(new Label("Schedule:"), 0, 3);
        grid.add(scheduleField, 1, 3);

        content.getChildren().add(grid);

        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String selectedCourseName = courseComboBox.getValue();
                String selectedInstructor = instructorField.getText();
                
                User teacher = new User();
                teacher.setName(selectedInstructor);
                teacher.setRole(Role.TEACHER);

                Course course = Course.builder()
                    .name(selectedCourseName)
                    .teacher(teacher)
                    .build();

                return Enrollment.builder()
                    .course(course)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(4))
                    .status("PENDING")
                    .build();
            }
            return null;
        });

        // Add validation
        Node enrollButton = dialogPane.lookupButton(ButtonType.OK);
        enrollButton.setDisable(true);

        // Enable button only when selections are made
        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput());
        semesterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput());
    }

    private void validateInput() {
        Node enrollButton = getDialogPane().lookupButton(ButtonType.OK);
        
        boolean isValid = courseComboBox.getValue() != null && 
                         semesterComboBox.getValue() != null;
        
        enrollButton.setDisable(!isValid);

        // Set mock data for instructor and schedule when course is selected
        if (courseComboBox.getValue() != null) {
            switch (courseComboBox.getValue()) {
                case "Piano Basics":
                    instructorField.setText("John Smith");
                    scheduleField.setText("Mon/Wed 2:00 PM");
                    break;
                case "Guitar 101":
                    instructorField.setText("Jane Doe");
                    scheduleField.setText("Tue/Thu 3:00 PM");
                    break;
                case "Voice Training":
                    instructorField.setText("Mike Johnson");
                    scheduleField.setText("Fri 1:00 PM");
                    break;
            }
        }
    }
} 
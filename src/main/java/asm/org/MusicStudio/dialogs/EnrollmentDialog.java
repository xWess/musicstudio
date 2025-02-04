package asm.org.MusicStudio.dialogs;

import asm.org.MusicStudio.entity.Enrollment;
import asm.org.MusicStudio.entity.Course;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import java.time.LocalDate;

public class EnrollmentDialog extends Dialog<Enrollment> {
    private ComboBox<String> courseComboBox;
    private ComboBox<String> semesterComboBox;
    private TextField instructorField;
    private TextField scheduleField;

    public EnrollmentDialog() {
        setTitle("New Enrollment");
        setHeaderText("Please enter enrollment details");

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

        getDialogPane().setContent(grid);

        // Add buttons
        ButtonType enrollButton = new ButtonType("Enroll", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(enrollButton, ButtonType.CANCEL);

        // Convert result - this is the only place that should create an Enrollment
        setResultConverter(new Callback<ButtonType, Enrollment>() {
            @Override
            public Enrollment call(ButtonType button) {
                if (button == enrollButton) {
                    Course course = Course.builder()
                        .name(courseComboBox.getValue())
                        .instructor(instructorField.getText())
                        .schedule(scheduleField.getText())
                        .build();

                    return Enrollment.builder()
                        .course(course)
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusMonths(4))
                        .status("PENDING")
                        .build();
                }
                return null;
            }
        });

        // Add validation
        Node enrollButtonNode = getDialogPane().lookupButton(enrollButton);
        enrollButtonNode.setDisable(true);

        // Enable button only when selections are made
        courseComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput());
        semesterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> 
            validateInput());
    }

    private void validateInput() {
        Node enrollButton = getDialogPane().lookupButton(
            getDialogPane().getButtonTypes().stream()
                .filter(bt -> bt.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                .findFirst().get()
        );
        
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
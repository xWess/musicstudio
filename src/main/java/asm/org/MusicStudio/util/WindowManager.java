package asm.org.MusicStudio.util;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class WindowManager {
    private static final double LOGIN_WIDTH = 400;
    private static final double LOGIN_HEIGHT = 500;
    private static final double MAIN_WIDTH = 1280;
    private static final double MAIN_HEIGHT = 800;

    public static void configureLoginWindow(Stage stage) {
        stage.setWidth(LOGIN_WIDTH);
        stage.setHeight(LOGIN_HEIGHT);
        stage.setMinWidth(LOGIN_WIDTH);
        stage.setMinHeight(LOGIN_HEIGHT);
        stage.setResizable(false);

        centerOnPrimaryScreen(stage, LOGIN_WIDTH, LOGIN_HEIGHT);
    }

    public static void configureMainWindow(Stage stage) {
        // Set minimum dimensions
        stage.setMinWidth(1024);
        stage.setMinHeight(768);

        // Set initial size
        stage.setWidth(MAIN_WIDTH);
        stage.setHeight(MAIN_HEIGHT);

        // Enable resizing
        stage.setResizable(true);

        // Get primary screen dimensions
        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D bounds = primaryScreen.getVisualBounds();

        // Calculate center position
        double centerX = bounds.getMinX() + (bounds.getWidth() - MAIN_WIDTH) / 2;
        double centerY = bounds.getMinY() + (bounds.getHeight() - MAIN_HEIGHT) / 2;

        // Position the window
        stage.setX(centerX);
        stage.setY(centerY);

        // Add a listener to ensure it stays centered after showing
        stage.showingProperty().addListener((observable, oldValue, showing) -> {
            if (showing) {
                stage.setX(centerX);
                stage.setY(centerY);
            }
        });
    }

    private static void centerOnPrimaryScreen(Stage stage, double width, double height) {
        // Get primary screen dimensions
        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D bounds = primaryScreen.getVisualBounds();

        // Calculate center position
        double centerX = bounds.getMinX() + (bounds.getWidth() - width) / 2;
        double centerY = bounds.getMinY() + (bounds.getHeight() - height) / 2;

        // Set size and position
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(centerX);
        stage.setY(centerY);
    }
}
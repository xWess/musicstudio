package asm.org.MusicStudio.ui;

import asm.org.MusicStudio.event.FileUploadEvent;
import asm.org.MusicStudio.event.FileUploadListener;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

public class FileUploadProgressBar extends VBox implements FileUploadListener {
    private final ProgressBar progressBar;
    private final Label statusLabel;
    
    public FileUploadProgressBar() {
        progressBar = new ProgressBar(0);
        statusLabel = new Label("Ready");
        
        progressBar.setMaxWidth(Double.MAX_VALUE);
        getChildren().addAll(progressBar, statusLabel);
        setSpacing(5);
    }
    
    @Override
    public void onUploadStarted(FileUploadEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(0);
            statusLabel.setText(event.getMessage());
        });
    }
    
    @Override
    public void onUploadProgress(FileUploadEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
            statusLabel.setText(event.getMessage());
        });
    }
    
    @Override
    public void onUploadCompleted(FileUploadEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(1);
            statusLabel.setText(event.getMessage());
        });
    }
    
    @Override
    public void onUploadFailed(FileUploadEvent event) {
        Platform.runLater(() -> {
            progressBar.setProgress(0);
            statusLabel.setText(event.getMessage());
        });
    }
} 
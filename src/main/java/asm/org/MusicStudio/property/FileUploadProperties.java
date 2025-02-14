package asm.org.MusicStudio.property;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

public class FileUploadProperties {
    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final DoubleProperty progress = new SimpleDoubleProperty();
    
    public StringProperty fileNameProperty() {
        return fileName;
    }
    
    public StringProperty statusProperty() {
        return status;
    }
    
    public DoubleProperty progressProperty() {
        return progress;
    }
    
    public void setFileName(String value) {
        fileName.set(value);
    }
    
    public void setStatus(String value) {
        status.set(value);
    }
    
    public void setProgress(double value) {
        progress.set(value);
    }
} 
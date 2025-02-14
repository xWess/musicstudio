package asm.org.MusicStudio.enums;

public enum FileUploadStatus {
    PENDING("PENDING"),
    UPLOADING("UPLOADING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");
    
    private final String value;
    
    FileUploadStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static FileUploadStatus fromString(String text) {
        for (FileUploadStatus status : FileUploadStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text);
    }
} 
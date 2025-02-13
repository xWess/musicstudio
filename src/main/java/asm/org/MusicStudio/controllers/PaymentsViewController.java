package asm.org.MusicStudio.controllers;

import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.Student;
import asm.org.MusicStudio.services.PaymentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;

public class PaymentsViewController {
    @FXML private TableView<Payment> paymentsTable;
    @FXML private TableColumn<Payment, String> dateColumn;
    @FXML private TableColumn<Payment, String> amountColumn;
    @FXML private TableColumn<Payment, String> descriptionColumn;
    @FXML private TableColumn<Payment, String> statusColumn;
    
    private PaymentService paymentService;
    private Student currentStudent;

    @FXML
    public void initialize() {
        paymentService = new PaymentService();
        setupTableColumns();
    }

    private void setupTableColumns() {
        dateColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getPaymentDate().toString()));
        amountColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(String.format("$%.2f", data.getValue().getAmount())));
        descriptionColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getDescription()));
        statusColumn.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getStatus()));
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        if (student != null) {
            loadPayments();
        }
    }

    private void loadPayments() {
        try {
            List<Payment> payments = paymentService.getUserPayments(currentStudent);
            paymentsTable.setItems(FXCollections.observableArrayList(payments));
        } catch (SQLException e) {
            showError("Error", "Failed to load payments: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
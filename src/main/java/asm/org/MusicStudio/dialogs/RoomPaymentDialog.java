package asm.org.MusicStudio.dialogs;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import asm.org.MusicStudio.entity.Payment;
import asm.org.MusicStudio.entity.Room;
import asm.org.MusicStudio.services.PaymentService;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RoomPaymentDialog extends Dialog<Payment> {
    private final Room room;
    private final PaymentService paymentService;
    private final ComboBox<String> paymentMethodCombo;
    private final TextField amountField;
    private final Integer userId;

    public RoomPaymentDialog(Room room, LocalDate bookingDate, Integer userId) {
        this.room = room;
        this.paymentService = new PaymentService();
        this.userId = userId;

        setTitle("Room Booking Payment");
        setHeaderText("Complete payment for room " + room.getLocation());

        // Create the payment form
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Payment amount from room price with null check
        Label amountLabel = new Label("Amount ($):");
        BigDecimal price = room.getPrice() != null ? room.getPrice() : BigDecimal.valueOf(50.00);
        amountField = new TextField(price.toString());
        amountField.setEditable(false);

        // Payment method
        Label methodLabel = new Label("Payment Method:");
        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll("Credit Card", "Cash", "Bank Transfer");
        paymentMethodCombo.setValue("Credit Card");

        // Add form elements
        content.getChildren().addAll(
            amountLabel,
            amountField,
            methodLabel,
            paymentMethodCombo
        );

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Convert the result to Payment object
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Payment payment = new Payment();
                    payment.setAmount(new BigDecimal(amountField.getText()));
                    payment.setPaymentMethod(paymentMethodCombo.getValue());
                    payment.setStatus("COMPLETED");
                    payment.setPaymentDate(LocalDateTime.now());
                    payment.setUserId(userId);
                    payment.setDescription("Room booking payment for " + room.getLocation() + " on " + bookingDate);
                    return payment;
                } catch (NumberFormatException e) {
                    showError("Invalid amount format");
                    return null;
                }
            }
            return null;
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 
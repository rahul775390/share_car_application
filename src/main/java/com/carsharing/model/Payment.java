package com.carsharing.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;
    private String bookingType; // 'RIDE', 'RENTAL'
    private Integer rideBookingId;
    private Integer rentalBookingId;
    private BigDecimal amount;
    private String paymentMethod; // 'CARD', 'UPI', 'NETBANKING'
    private String status; // 'PENDING', 'COMPLETED', 'REFUNDED'
    private String transactionId;
    private Timestamp createdAt;

    public Payment() {}

    public Payment(int id, int userId, String bookingType, Integer rideBookingId, 
                   Integer rentalBookingId, BigDecimal amount, String paymentMethod, 
                   String status, String transactionId, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.bookingType = bookingType;
        this.rideBookingId = rideBookingId;
        this.rentalBookingId = rentalBookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.transactionId = transactionId;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public Integer getRideBookingId() {
        return rideBookingId;
    }

    public void setRideBookingId(Integer rideBookingId) {
        this.rideBookingId = rideBookingId;
    }

    public Integer getRentalBookingId() {
        return rentalBookingId;
    }

    public void setRentalBookingId(Integer rentalBookingId) {
        this.rentalBookingId = rentalBookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

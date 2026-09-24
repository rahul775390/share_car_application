package com.carsharing.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int raterId;
    private int rateeId;
    private String bookingType; // 'RIDE', 'RENTAL'
    private Integer rideBookingId;
    private Integer rentalBookingId;
    private int ratingValue; // 1 to 5
    private String comment;
    private Timestamp createdAt;

    // Helper Join Field
    private String raterName;

    public Rating() {}

    public Rating(int id, int raterId, int rateeId, String bookingType, Integer rideBookingId, 
                  Integer rentalBookingId, int ratingValue, String comment, Timestamp createdAt) {
        this.id = id;
        this.raterId = raterId;
        this.rateeId = rateeId;
        this.bookingType = bookingType;
        this.rideBookingId = rideBookingId;
        this.rentalBookingId = rentalBookingId;
        this.ratingValue = ratingValue;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRaterId() {
        return raterId;
    }

    public void setRaterId(int raterId) {
        this.raterId = raterId;
    }

    public int getRateeId() {
        return rateeId;
    }

    public void setRateeId(int rateeId) {
        this.rateeId = rateeId;
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

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getRaterName() {
        return raterName;
    }

    public void setRaterName(String raterName) {
        this.raterName = raterName;
    }
}

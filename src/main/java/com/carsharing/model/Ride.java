package com.carsharing.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class Ride implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int driverId;
    private int vehicleId;
    private String source;
    private String destination;
    private Date rideDate;
    private Time rideTime;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal pricePerSeat;
    private String description;
    private String status; // 'ACTIVE', 'FULL', 'CANCELLED', 'COMPLETED'
    private Timestamp createdAt;

    // Joins/Helper Fields for rendering
    private String driverName;
    private String driverPhone;
    private String vehicleMake;
    private String vehicleModel;
    private String vehiclePlate;

    public Ride() {}

    public Ride(int id, int driverId, int vehicleId, String source, String destination, 
                Date rideDate, Time rideTime, int totalSeats, int availableSeats, 
                BigDecimal pricePerSeat, String description, String status, Timestamp createdAt) {
        this.id = id;
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.source = source;
        this.destination = destination;
        this.rideDate = rideDate;
        this.rideTime = rideTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.pricePerSeat = pricePerSeat;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getRideDate() {
        return rideDate;
    }

    public void setRideDate(Date rideDate) {
        this.rideDate = rideDate;
    }

    public Time getRideTime() {
        return rideTime;
    }

    public void setRideTime(Time rideTime) {
        this.rideTime = rideTime;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public BigDecimal getPricePerSeat() {
        return pricePerSeat;
    }

    public void setPricePerSeat(BigDecimal pricePerSeat) {
        this.pricePerSeat = pricePerSeat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getVehicleMake() {
        return vehicleMake;
    }

    public void setVehicleMake(String vehicleMake) {
        this.vehicleMake = vehicleMake;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + id +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId +
                ", source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", rideDate=" + rideDate +
                ", rideTime=" + rideTime +
                ", totalSeats=" + totalSeats +
                ", availableSeats=" + availableSeats +
                ", pricePerSeat=" + pricePerSeat +
                ", status='" + status + '\'' +
                '}';
    }
}

package com.carsharing.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Vehicle implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int ownerId;
    private String make; // Brand
    private String model;
    private int year;
    private String licensePlate; // Registration Number
    private String type; // Vehicle Type
    private String fuelType;
    private String transmission;
    private String color;
    private int seatingCapacity;
    private String status; // 'AVAILABLE', 'UNAVAILABLE', 'REMOVED'
    private Timestamp createdAt;

    public Vehicle() {}

    public Vehicle(int id, int ownerId, String make, String model, int year, String licensePlate, 
                   String type, String fuelType, String transmission, String color, int seatingCapacity, 
                   String status, Timestamp createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.type = type;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.color = color;
        this.seatingCapacity = seatingCapacity;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(int seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
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

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", licensePlate='" + licensePlate + '\'' +
                ", type='" + type + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", transmission='" + transmission + '\'' +
                ", color='" + color + '\'' +
                ", seatingCapacity=" + seatingCapacity +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

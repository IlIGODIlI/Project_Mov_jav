package com.mov.transport.model;

import jakarta.persistence.*;

@Entity
@Table(name = "driver_trips")
public class DriverTrip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String driverEmail;
    private String routeCode;

    // This is the source of the naming convention
    private boolean active;

    public DriverTrip(){}

    public Long getId() { return id; }

    public String getDriverEmail() { return driverEmail; }

    public void setDriverEmail(String driverEmail) { this.driverEmail = driverEmail; }

    public String getRouteCode() { return routeCode; }

    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}
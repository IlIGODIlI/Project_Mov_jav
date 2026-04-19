package com.mov.transport.model;

import jakarta.persistence.*;

@Entity
@Table(name = "student_routes")
public class StudentRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentEmail;

    private String routeCode;

    public StudentRoute() {}

    public Long getId() {
        return id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getRouteCode() {
        return routeCode;
    }

    public void setRouteCode(String routeCode) {
        this.routeCode = routeCode;
    }
}
package com.mov.transport.repository;

import com.mov.transport.model.StudentRoute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRouteRepository extends JpaRepository<StudentRoute, Long> {

    StudentRoute findByStudentEmail(String studentEmail);
    long countByRouteCode(String routeCode);
}
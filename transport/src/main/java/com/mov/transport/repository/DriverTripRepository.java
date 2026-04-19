package com.mov.transport.repository;

import com.mov.transport.model.DriverTrip;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverTripRepository extends JpaRepository<DriverTrip, Long> {
    // Corrected: Removed "Is" to match the field name 'active'
    DriverTrip findFirstByRouteCodeAndActiveTrue(String routeCode);
}
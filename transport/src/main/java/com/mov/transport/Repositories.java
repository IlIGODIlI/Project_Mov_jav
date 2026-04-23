package com.mov.transport;

import org.springframework.data.jpa.repository.JpaRepository;

public class Repositories {}

interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}

interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    BusLocation findByRouteCode(String routeCode);
}

interface DriverTripRepository extends JpaRepository<DriverTrip, Long> {
    DriverTrip findFirstByRouteCodeAndActiveTrue(String routeCode);
}

interface RouteRepository extends JpaRepository<Route, Long> {
    Route findByRouteCode(String routeCode);
}

interface StudentRouteRepository extends JpaRepository<StudentRoute, Long> {
    StudentRoute findByStudentEmail(String studentEmail);
    long countByRouteCode(String routeCode);
}

interface DriverRouteRepository extends JpaRepository<DriverRoute, Long> {
    DriverRoute findByDriverEmail(String driverEmail);
}

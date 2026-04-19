package com.mov.transport.repository;

import com.mov.transport.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Route findByRouteCode(String routeCode);

}
package com.mov.transport.repository;

import com.mov.transport.model.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {

    BusLocation findByRouteCode(String routeCode);

}
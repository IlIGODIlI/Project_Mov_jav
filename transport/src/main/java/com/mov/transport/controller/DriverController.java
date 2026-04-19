package com.mov.transport.controller;

import com.mov.transport.model.DriverTrip;
import com.mov.transport.repository.DriverTripRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mov.transport.repository.BusLocationRepository;
import com.mov.transport.model.BusLocation;

@RestController
@RequestMapping("/driver")
public class DriverController {

    @Autowired
    private DriverTripRepository driverTripRepository;

    @PostMapping("/startTrip")
    public String startTrip(@RequestBody DriverTrip trip)
    {

        trip.setActive(true);

        driverTripRepository.save(trip);

        return "Trip started successfully";
    }

    @Autowired
    private BusLocationRepository busLocationRepository;

    @PostMapping("/updateLocation")
    public String updateLocation(@RequestBody BusLocation location){

        BusLocation existing = busLocationRepository.findByRouteCode(location.getRouteCode());

        if(existing != null){
            existing.setLatitude(location.getLatitude());
            existing.setLongitude(location.getLongitude());
            busLocationRepository.save(existing);
        } else {
            busLocationRepository.save(location);
        }

        return "Location updated";
    }

}
package com.mov.transport.controller;

import com.mov.transport.model.DriverTrip;
import com.mov.transport.model.StudentRoute;
import com.mov.transport.model.BusLocation;
import com.mov.transport.repository.DriverTripRepository;
import com.mov.transport.repository.StudentRouteRepository;
import com.mov.transport.repository.RouteRepository;
import com.mov.transport.repository.BusLocationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private StudentRouteRepository studentRouteRepository;

    @Autowired
    private BusLocationRepository busLocationRepository;

    @Autowired
    private DriverTripRepository driverTripRepository;

    @PostMapping("/joinRoute")
    public String joinRoute(@RequestBody Map<String,String> data){
        String routeCode = data.get("routeCode");

        String studentEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        StudentRoute sr = new StudentRoute();
        sr.setRouteCode(routeCode);
        sr.setStudentEmail(studentEmail);

        studentRouteRepository.save(sr);
        return "Successfully joined route";
    }

    @GetMapping("/busLocation/{routeCode}")
    public BusLocation getBusLocation(@PathVariable String routeCode){
        return busLocationRepository.findByRouteCode(routeCode);
    }

    @GetMapping("/myRoute")
    public StudentRoute getMyRoute(){
        String studentEmail = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return studentRouteRepository.findByStudentEmail(studentEmail);
    }

    @GetMapping("/eta/{routeCode}")
    public double getETA(@PathVariable String routeCode){
        BusLocation bus = busLocationRepository.findByRouteCode(routeCode);

        // Coordinates for the school destination
        double schoolLat = 28.6139;
        double schoolLng = 77.2090;

        // Euclidean distance formula
        double distance = Math.sqrt(
                Math.pow(bus.getLatitude() - schoolLat, 2)
                        +
                        Math.pow(bus.getLongitude() - schoolLng, 2)
        );

        double speed = 0.01;
        return distance / speed;
    }

    @GetMapping("/routeDetails/{routeCode}")
    public Map<String, Object> getRouteDetails(@PathVariable String routeCode) {
        // Fetch count of students on this route
        long count = studentRouteRepository.countByRouteCode(routeCode);

        // Fetch the active driver trip for this route
        DriverTrip trip = driverTripRepository.findFirstByRouteCodeAndActiveTrue(routeCode);

        return Map.of(
                "studentCount", count,
                "driverEmail", (trip != null) ? trip.getDriverEmail() : "No active driver"
        );
    }
}
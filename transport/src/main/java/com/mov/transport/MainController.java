package com.mov.transport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;
import com.mov.transport.config.JwtUtil;

public class MainController {}

@RestController
class AuthController {

    @Autowired
    private UserRepository userRepository;

    @SuppressWarnings("null")
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping("/admin/allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public Object loginUser(@RequestBody User user){
        User existingUser = userRepository.findByEmail(user.getEmail());

        if(existingUser == null)
            return "User not found";

        if(!existingUser.getRole().equalsIgnoreCase(user.getRole()))
            return "Incorrect role";

        if(existingUser.getPassword().equals(user.getPassword())){
            String token = JwtUtil.generateToken(
                    existingUser.getEmail(),
                    existingUser.getRole()
            );
            return token;
        }

        return "Invalid password";
    }
}

@RestController
@RequestMapping("/driver")
class DriverController {

    @Autowired
    private DriverTripRepository driverTripRepository;

    @PostMapping("/startTrip")
    public String startTrip(@RequestBody DriverTrip trip) {
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

@RestController
@RequestMapping("/admin")
class RouteController {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRouteRepository studentRouteRepository;

    @Autowired
    private DriverRouteRepository driverRouteRepository;

    @GetMapping("/allRoutes")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Autowired
    private RouteService routeService;

    @PostMapping("/createRoute")
    public Route createRoute(@RequestBody Route route) {
        String code = routeService.generateRouteCode();
        route.setRouteCode(code);
        return routeRepository.save(route);
    }

    @Autowired
    private BusLocationRepository busLocationRepository;

    @GetMapping("/allLocations")
    public List<BusLocation> getAllLocations() {
        return busLocationRepository.findAll();
    }

    @PostMapping("/assignRoute")
    public String assignRoute(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String routeCode = data.get("routeCode");

        User user = userRepository.findByEmail(email);
        if (user == null) return "User not found";

        if (user.getRole().equalsIgnoreCase("STUDENT")) {
            StudentRoute sr = studentRouteRepository.findByStudentEmail(email);
            if (sr == null) sr = new StudentRoute();
            sr.setStudentEmail(email);
            sr.setRouteCode(routeCode);
            studentRouteRepository.save(sr);
            return "Route " + routeCode + " assigned to student " + email;
        } else if (user.getRole().equalsIgnoreCase("DRIVER")) {
            DriverRoute dr = driverRouteRepository.findByDriverEmail(email);
            if (dr == null) dr = new DriverRoute();
            dr.setDriverEmail(email);
            dr.setRouteCode(routeCode);
            driverRouteRepository.save(dr);
            return "Route " + routeCode + " assigned to driver " + email;
        }

        return "Invalid role for assignment";
    }

    @PutMapping("/updateRoutePosition")
    public String updateRoutePosition(@RequestBody Map<String, String> data) {
        String routeCode = data.get("routeCode");
        
        try {
            double sLat = Double.parseDouble(data.get("startLat"));
            double sLng = Double.parseDouble(data.get("startLng"));
            double eLat = Double.parseDouble(data.get("endLat"));
            double eLng = Double.parseDouble(data.get("endLng"));

            Route route = routeRepository.findByRouteCode(routeCode);
            if (route == null) return "Route not found";

            route.setStartLat(sLat);
            route.setStartLng(sLng);
            route.setEndLat(eLat);
            route.setEndLng(eLng);
            routeRepository.save(route);
            return "Route coordinates updated successfully";
        } catch (Exception e) {
            return "Invalid coordinate format: " + e.getMessage();
        }
    }
}

@RestController
@RequestMapping("/student")
class StudentController {

    @Autowired
    private StudentRouteRepository studentRouteRepository;

    @Autowired
    private BusLocationRepository busLocationRepository;

    @Autowired
    private DriverTripRepository driverTripRepository;

    @Autowired
    private RouteRepository routeRepository;

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
        Route route = routeRepository.findByRouteCode(routeCode);

        if (bus == null || route == null) return 0;

        // Use the route's end coordinates as the destination
        double destLat = route.getEndLat();
        double destLng = route.getEndLng();

        // Euclidean distance formula
        double distance = Math.sqrt(
                Math.pow(bus.getLatitude() - destLat, 2)
                        +
                        Math.pow(bus.getLongitude() - destLng, 2)
        );

        double speed = 0.01;
        return distance / speed;
    }

    @GetMapping("/routeDetails/{routeCode}")
    public Map<String, Object> getRouteDetails(@PathVariable String routeCode) {
        long count = studentRouteRepository.countByRouteCode(routeCode);
        DriverTrip trip = driverTripRepository.findFirstByRouteCodeAndActiveTrue(routeCode);
        Route route = routeRepository.findByRouteCode(routeCode);

        return Map.of(
                "studentCount", count,
                "driverEmail", (trip != null) ? trip.getDriverEmail() : "No active driver",
                "startLat", (route != null) ? route.getStartLat() : 0,
                "startLng", (route != null) ? route.getStartLng() : 0,
                "endLat", (route != null) ? route.getEndLat() : 0,
                "endLng", (route != null) ? route.getEndLng() : 0
        );
    }
}

@RestController
class TestController {
    @GetMapping("/")
    public String home() {
        return "Bus Transport Management System Running 🚍";
    }
}

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
        long count = studentRouteRepository.countByRouteCode(routeCode);
        DriverTrip trip = driverTripRepository.findFirstByRouteCodeAndActiveTrue(routeCode);

        return Map.of(
                "studentCount", count,
                "driverEmail", (trip != null) ? trip.getDriverEmail() : "No active driver"
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

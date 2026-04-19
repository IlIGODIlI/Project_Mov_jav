package com.mov.transport.controller;

import com.mov.transport.model.Route;
import com.mov.transport.repository.RouteRepository;
import com.mov.transport.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController
@RequestMapping("/admin")
public class RouteController {

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
}
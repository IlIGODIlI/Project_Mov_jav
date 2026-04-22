package com.mov.transport;

import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class RouteService {

    public String generateRouteCode() {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        StringBuilder code = new StringBuilder("BUS-");

        for(int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }
}

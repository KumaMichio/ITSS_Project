package controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import response.ResponseObject;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${spring.application.api-prefix}")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class HealthController {

    @GetMapping("/health")
    public ResponseObject<Map<String, Object>> health() {
        Map<String, Object> healthData = new HashMap<>();
        healthData.put("status", "UP");
        healthData.put("timestamp", System.currentTimeMillis());
        healthData.put("service", "ITSS Payment Backend");
        healthData.put("version", "1.0.0");

        return new ResponseObject<>(HttpStatus.OK, "Service is healthy", healthData);
    }
}

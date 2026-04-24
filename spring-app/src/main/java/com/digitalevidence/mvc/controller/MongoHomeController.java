package com.digitalevidence.mvc.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile("mongo")
public class MongoHomeController {

    @GetMapping("/")
    public ResponseEntity<?> home() {
        return ResponseEntity.ok(new ApiInfo(
            "Digital Evidence Management System - MongoDB Backend",
            "API Endpoints available at /api/mongo/v1/",
            "Cases: /api/mongo/v1/cases",
            "Evidence: /api/mongo/v1/evidence",
            "Custody: /api/mongo/v1/custody"
        ));
    }

    record ApiInfo(String title, String message, String cases, String evidence, String custody) {}
}
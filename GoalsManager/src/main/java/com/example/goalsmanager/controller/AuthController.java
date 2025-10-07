package com.example.goalsmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        // For now, return a simple response since we're using NextAuth.js on the frontend
        return ResponseEntity.ok(Map.of(
            "message", "Backend is running",
            "status", "ok"
        ));
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus() {
        return ResponseEntity.ok(Map.of(
            "message", "Backend is running",
            "status", "ok"
        ));
    }
}

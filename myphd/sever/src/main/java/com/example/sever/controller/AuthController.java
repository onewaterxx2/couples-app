package com.example.sever.controller;

import com.example.sever.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<Map<String, Object>> sendCode(
            @RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");

        Map<String, Object> result = authService.sendCode(email);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        String nickname = (String) request.get("nickname");
        Boolean isMale = (Boolean) request.get("isMale");
        String code = (String) request.get("code");

        Map<String, Object> result = authService.register(email, password, nickname, isMale, code);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        String password = (String) request.get("password");

        Map<String, Object> result = authService.login(email, password);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/create-couple")
    public ResponseEntity<Map<String, Object>> createCouple(
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();

        Map<String, Object> result = authService.createCouple(userId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/join-couple")
    public ResponseEntity<Map<String, Object>> joinCouple(
            @RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        String code = (String) request.get("code");

        Map<String, Object> result = authService.joinCouple(userId, code);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/couple-status")
    public ResponseEntity<Map<String, Object>> coupleStatus(
            @RequestParam("coupleId") Long coupleId) {
        Map<String, Object> result = authService.coupleStatus(coupleId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
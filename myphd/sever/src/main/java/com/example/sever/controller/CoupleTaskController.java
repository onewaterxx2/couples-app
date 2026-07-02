package com.example.sever.controller;

import com.example.sever.service.CoupleTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class CoupleTaskController {

    @Autowired
    private CoupleTaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTask(@RequestBody Map<String, Object> request) {
        Long coupleId = ((Number) request.get("coupleId")).longValue();
        Long creatorId = ((Number) request.get("creatorId")).longValue();
        String title = (String) request.get("title");
        String description = (String) request.get("description");

        Map<String, Object> result = taskService.createTask(coupleId, creatorId, title, description);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getTasks(@RequestParam("coupleId") Long coupleId) {
        Map<String, Object> result = taskService.getTasks(coupleId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleComplete(@RequestBody Map<String, Object> request) {
        Long taskId = ((Number) request.get("taskId")).longValue();
        Map<String, Object> result = taskService.toggleComplete(taskId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteTask(@RequestParam("taskId") Long taskId) {
        Map<String, Object> result = taskService.deleteTask(taskId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

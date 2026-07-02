package com.example.sever.controller;

import com.example.sever.entity.Mood;
import com.example.sever.service.MoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/moods")
public class MoodController {

    @Autowired
    private MoodService moodService;

    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> setMood(
            @RequestBody Map<String, Object> request) {

        Long userId = ((Number) request.get("userId")).longValue();
        Long coupleId = ((Number) request.get("coupleId")).longValue();
        Integer moodType = ((Number) request.get("moodType")).intValue();
        String message = (String) request.get("message");

        Map<String, Object> result = moodService.setMood(userId, coupleId, moodType, message);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/today")
    public ResponseEntity<List<Mood>> getTodayMoods(@RequestParam("coupleId") Long coupleId) {
        List<Mood> moods = moodService.getTodayMoods(coupleId);
        return ResponseEntity.ok(moods);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Mood>> getAllMoods(@RequestParam("coupleId") Long coupleId) {
        List<Mood> moods = moodService.getAllMoodsByCoupleId(coupleId);
        return ResponseEntity.ok(moods);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Mood>> getMoodsByUser(@PathVariable Long userId) {
        List<Mood> moods = moodService.getMoodsByUserId(userId);
        return ResponseEntity.ok(moods);
    }
}
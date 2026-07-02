package com.example.sever.controller;

import com.example.sever.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * 更新当前用户位置
     */
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateLocation(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long coupleId = ((Number) request.get("coupleId")).longValue();
        Double latitude = ((Number) request.get("latitude")).doubleValue();
        Double longitude = ((Number) request.get("longitude")).doubleValue();
        String address = (String) request.get("address");

        Map<String, Object> result = locationService.updateLocation(userId, coupleId, latitude, longitude, address);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取对方位置
     */
    @GetMapping("/partner")
    public ResponseEntity<Map<String, Object>> getPartnerLocation(
            @RequestParam("userId") Long userId,
            @RequestParam("coupleId") Long coupleId) {

        Map<String, Object> result = locationService.getPartnerLocation(userId, coupleId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取情侣双方位置
     */
    @GetMapping("/couple")
    public ResponseEntity<Map<String, Object>> getCoupleLocations(@RequestParam("coupleId") Long coupleId) {
        Map<String, Object> result = locationService.getCoupleLocations(coupleId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 切换位置共享开关
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleSharing(@RequestBody Map<String, Object> request) {
        Long userId = ((Number) request.get("userId")).longValue();
        Long coupleId = ((Number) request.get("coupleId")).longValue();
        Boolean enabled = (Boolean) request.get("enabled");

        Map<String, Object> result = locationService.toggleSharing(userId, coupleId, enabled);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 获取位置共享状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSharingStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("coupleId") Long coupleId) {

        Map<String, Object> result = locationService.getSharingStatus(userId, coupleId);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

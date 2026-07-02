package com.example.sever.service;

import com.example.sever.entity.Location;
import com.example.sever.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;

    /**
     * 更新用户位置
     */
    public Map<String, Object> updateLocation(Long userId, Long coupleId, Double latitude, Double longitude, String address) {
        Map<String, Object> result = new HashMap<>();

        if (latitude == null || longitude == null) {
            result.put("success", false);
            result.put("message", "位置信息不完整");
            return result;
        }

        try {
            Optional<Location> existingLocation = locationRepository.findByUserIdAndCoupleId(userId, coupleId);

            Location location;
            if (existingLocation.isPresent()) {
                // 更新现有位置
                location = existingLocation.get();
                location.setLatitude(latitude);
                location.setLongitude(longitude);
                location.setAddress(address);
                location.setUpdatedAt(LocalDateTime.now());
            } else {
                // 创建新位置记录
                location = new Location(userId, coupleId, latitude, longitude, address);
            }

            Location savedLocation = locationRepository.save(location);

            result.put("success", true);
            result.put("message", "位置更新成功");
            result.put("location", buildLocationResponse(savedLocation));

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "位置更新失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取对方的位置
     */
    public Map<String, Object> getPartnerLocation(Long userId, Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Location> locations = locationRepository.findByCoupleId(coupleId);

            // 找到对方的位置（不是自己的）
            Optional<Location> partnerLocation = locations.stream()
                    .filter(loc -> !loc.getUserId().equals(userId))
                    .findFirst();

            if (partnerLocation.isEmpty()) {
                result.put("success", false);
                result.put("message", "对方尚未分享位置");
                return result;
            }

            Location location = partnerLocation.get();

            // 检查对方是否开启了位置共享
            if (!location.getSharingEnabled()) {
                result.put("success", false);
                result.put("message", "对方已关闭位置共享");
                return result;
            }

            result.put("success", true);
            result.put("location", buildLocationResponse(location));

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取位置失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取情侣双方的位置
     */
    public Map<String, Object> getCoupleLocations(Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<Location> locations = locationRepository.findByCoupleId(coupleId);

            // 只返回启用共享的位置
            List<Map<String, Object>> locationList = locations.stream()
                    .filter(Location::getSharingEnabled)
                    .map(this::buildLocationResponse)
                    .toList();

            result.put("success", true);
            result.put("locations", locationList);

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取位置失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 切换位置共享开关
     */
    public Map<String, Object> toggleSharing(Long userId, Long coupleId, Boolean enabled) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<Location> locationOpt = locationRepository.findByUserIdAndCoupleId(userId, coupleId);

            Location location;
            if (locationOpt.isEmpty()) {
                // 如果还没有位置记录，创建一个默认的（纬度经度为0）
                location = new Location();
                location.setUserId(userId);
                location.setCoupleId(coupleId);
                location.setLatitude(0.0);
                location.setLongitude(0.0);
                location.setAddress("未设置");
                location.setSharingEnabled(enabled);
            } else {
                location = locationOpt.get();
                location.setSharingEnabled(enabled);
            }

            location.setUpdatedAt(LocalDateTime.now());
            locationRepository.save(location);

            result.put("success", true);
            result.put("message", enabled ? "位置共享已开启" : "位置共享已关闭");
            result.put("sharingEnabled", enabled);

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "操作失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 获取用户的位置共享状态
     */
    public Map<String, Object> getSharingStatus(Long userId, Long coupleId) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<Location> locationOpt = locationRepository.findByUserIdAndCoupleId(userId, coupleId);

            if (locationOpt.isEmpty()) {
                result.put("success", true);
                result.put("sharingEnabled", false);
                result.put("hasLocation", false);
                return result;
            }

            Location location = locationOpt.get();

            result.put("success", true);
            result.put("sharingEnabled", location.getSharingEnabled());
            result.put("hasLocation", true);
            result.put("updatedAt", location.getUpdatedAt().toString());

            return result;
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取状态失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 构建位置响应对象
     */
    private Map<String, Object> buildLocationResponse(Location location) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", location.getUserId());
        response.put("latitude", location.getLatitude());
        response.put("longitude", location.getLongitude());
        response.put("address", location.getAddress());
        response.put("updatedAt", location.getUpdatedAt().toString());
        return response;
    }
}

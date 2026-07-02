package com.example.sever.service;

import com.example.sever.entity.Mood;
import com.example.sever.repository.MoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MoodService {

    @Autowired
    private MoodRepository moodRepository;

    public Map<String, Object> setMood(Long userId, Long coupleId, Integer moodType, String message) {
        Map<String, Object> result = new HashMap<>();

        Optional<Mood> existingMood = moodRepository.findByUserIdAndMoodDate(userId, LocalDate.now());

        if (existingMood.isPresent()) {
            result.put("success", false);
            result.put("message", "今日已设置心情");
            return result;
        }

        Mood mood = new Mood(userId, coupleId, moodType, message);
        Mood savedMood = moodRepository.save(mood);

        result.put("success", true);
        result.put("message", "心情设置成功");
        result.put("mood", savedMood);

        return result;
    }

    public List<Mood> getTodayMoods(Long coupleId) {
        return moodRepository.findByCoupleIdAndMoodDate(coupleId, LocalDate.now());
    }

    public List<Mood> getAllMoodsByCoupleId(Long coupleId) {
        return moodRepository.findByCoupleIdOrderByMoodDateDesc(coupleId);
    }

    public List<Mood> getMoodsByUserId(Long userId) {
        return moodRepository.findByUserIdOrderByMoodDateDesc(userId);
    }
}
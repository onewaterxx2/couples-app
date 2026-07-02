package com.example.sever.repository;

import com.example.sever.entity.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MoodRepository extends JpaRepository<Mood, Long> {
    Optional<Mood> findByUserIdAndMoodDate(Long userId, LocalDate moodDate);
    List<Mood> findByCoupleIdOrderByMoodDateDesc(Long coupleId);
    List<Mood> findByUserIdOrderByMoodDateDesc(Long userId);
    List<Mood> findByCoupleIdAndMoodDate(Long coupleId, LocalDate moodDate);
}
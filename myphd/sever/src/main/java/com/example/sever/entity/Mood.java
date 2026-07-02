package com.example.sever.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "moods")
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "couple_id", nullable = false)
    private Long coupleId;

    @Column(name = "mood_type", nullable = false)
    private Integer moodType;

    @Column(name = "message")
    private String message;

    @Column(name = "mood_date", nullable = false)
    private LocalDate moodDate;

    public Mood() {
        this.moodDate = LocalDate.now();
    }

    public Mood(Long userId, Long coupleId, Integer moodType, String message) {
        this.userId = userId;
        this.coupleId = coupleId;
        this.moodType = moodType;
        this.message = message;
        this.moodDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCoupleId() {
        return coupleId;
    }

    public void setCoupleId(Long coupleId) {
        this.coupleId = coupleId;
    }

    public Integer getMoodType() {
        return moodType;
    }

    public void setMoodType(Integer moodType) {
        this.moodType = moodType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDate getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(LocalDate moodDate) {
        this.moodDate = moodDate;
    }
}
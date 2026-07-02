package com.example.sever.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "couples")
public class Couple {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "male_id")
    private Long maleId;

    @Column(name = "female_id")
    private Long femaleId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    public Couple() {
    }

    public Couple(Long maleId, Long femaleId, LocalDate startDate, String code) {
        this.maleId = maleId;
        this.femaleId = femaleId;
        this.startDate = startDate;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMaleId() {
        return maleId;
    }

    public void setMaleId(Long maleId) {
        this.maleId = maleId;
    }

    public Long getFemaleId() {
        return femaleId;
    }

    public void setFemaleId(Long femaleId) {
        this.femaleId = femaleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
package com.example.sever.repository;

import com.example.sever.entity.Couple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoupleRepository extends JpaRepository<Couple, Long> {
    Optional<Couple> findByCode(String code);
    Optional<Couple> findByMaleId(Long maleId);
    Optional<Couple> findByFemaleId(Long femaleId);
}
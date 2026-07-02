package com.example.sever.repository;

import com.example.sever.entity.CoupleTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoupleTaskRepository extends JpaRepository<CoupleTask, Long> {
    List<CoupleTask> findByCoupleIdOrderByCreatedAtDesc(Long coupleId);
}

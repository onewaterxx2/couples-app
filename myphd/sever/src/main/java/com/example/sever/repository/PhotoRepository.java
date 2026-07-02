package com.example.sever.repository;

import com.example.sever.entity.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    Page<Photo> findByCoupleIdOrderByCreatedAtDesc(Long coupleId, Pageable pageable);
    List<Photo> findByCoupleIdOrderByCreatedAtDesc(Long coupleId);
}
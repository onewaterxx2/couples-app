package com.example.sever.repository;

import com.example.sever.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByCoupleIdOrderByCreatedAtDesc(Long coupleId, Pageable pageable);
    List<Message> findByCoupleIdOrderByCreatedAtDesc(Long coupleId);
    List<Message> findByCoupleIdAndIdLessThanOrderByCreatedAtDesc(Long coupleId, Long lastId, Pageable pageable);
}
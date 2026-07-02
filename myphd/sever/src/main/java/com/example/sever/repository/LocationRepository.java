package com.example.sever.repository;

import com.example.sever.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByUserId(Long userId);

    List<Location> findByCoupleId(Long coupleId);

    Optional<Location> findByUserIdAndCoupleId(Long userId, Long coupleId);
}

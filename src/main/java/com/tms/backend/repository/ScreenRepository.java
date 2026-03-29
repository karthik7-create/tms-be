// ═══ FILE: src/main/java/com/tms/backend/repository/ScreenRepository.java ═══
package com.tms.backend.repository;

import com.tms.backend.model.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {

    List<Screen> findByTheatreId(Long theatreId);
}

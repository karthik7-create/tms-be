// ═══ FILE: src/main/java/com/tms/backend/repository/TheatreRepository.java ═══
package com.tms.backend.repository;

import com.tms.backend.model.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {

    List<Theatre> findByCityIgnoreCase(String city);
}

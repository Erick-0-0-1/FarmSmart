package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.PlantingRecord;
import com.agriconnect.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlantingRecordRepository extends JpaRepository<PlantingRecord, Long> {

    // Get all planting records for a user
    List<PlantingRecord> findByUserOrderByPlantingDateDesc(User user);

    // Get by user ID
    List<PlantingRecord> findByUserIdOrderByPlantingDateDesc(Long userId);

    // Get active plantings (not harvested or cancelled)
    List<PlantingRecord> findByUserAndStatusIn(User user, List<PlantingRecord.Status> statuses);
}
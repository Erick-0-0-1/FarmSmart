package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.FertilizerSchedule;
import com.agriconnect.backend.model.RiceVariety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FertilizerScheduleRepository extends JpaRepository<FertilizerSchedule, Long> {

    // Get fertilizer schedule for a rice variety, ordered by days after planting
    List<FertilizerSchedule> findByRiceVarietyOrderByDayAfterPlanting(RiceVariety riceVariety);

    // Get schedule by variety ID
    List<FertilizerSchedule> findByRiceVarietyIdOrderByDayAfterPlanting(Long varietyId);
}
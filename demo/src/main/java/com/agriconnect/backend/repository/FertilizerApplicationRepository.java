package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.FertilizerApplication;
import com.agriconnect.backend.model.PlantingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FertilizerApplicationRepository extends JpaRepository<FertilizerApplication, Long> {

    List<FertilizerApplication> findByPlantingRecordOrderByScheduledDateAsc(PlantingRecord plantingRecord);

    List<FertilizerApplication> findByPlantingRecordIdOrderByScheduledDateAsc(Long plantingRecordId);

    List<FertilizerApplication> findByStatusAndScheduledDateBetween(
            FertilizerApplication.Status status,
            LocalDate startDate,
            LocalDate endDate
    );

    List<FertilizerApplication> findByPlantingRecordAndStatus(
            PlantingRecord plantingRecord,
            FertilizerApplication.Status status
    );
}
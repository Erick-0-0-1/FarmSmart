package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.CropHealthAnalysis;
import com.agriconnect.backend.model.PlantingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CropHealthAnalysisRepository extends JpaRepository<CropHealthAnalysis, Long> {

    List<CropHealthAnalysis> findByPlantingRecordOrderByAnalyzedAtDesc(PlantingRecord plantingRecord);

    List<CropHealthAnalysis> findByPlantingRecordIdOrderByAnalyzedAtDesc(Long plantingRecordId);
}
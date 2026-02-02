package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.RiceVariety;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RiceVarietyRepository extends JpaRepository<RiceVariety, Long> {

    // Find varieties by season
    List<RiceVariety> findBySeason(RiceVariety.Season season);

    // Find varieties suitable for current season or BOTH
    List<RiceVariety> findBySeasonIn(List<RiceVariety.Season> seasons);

    // Find drought-tolerant varieties
    List<RiceVariety> findByDroughtTolerantTrue();

    // Find flood-tolerant varieties
    List<RiceVariety> findByFloodTolerantTrue();
}
package com.agriconnect.backend.repository;

import com.agriconnect.backend.model.Field;
import com.agriconnect.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Long> {

    List<Field> findByUserOrderByCreatedAtDesc(User user);

    List<Field> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Field> findByUserAndIsActiveTrue(User user);
}
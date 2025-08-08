package com.healthcare.healthcareproject.repository;

import com.healthcare.healthcareproject.model.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, UUID> {
    List<ProviderAvailability> findByProviderId(UUID providerId);
} 
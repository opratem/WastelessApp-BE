package com.wasteless.backend.repository;

import com.wasteless.backend.model.ExpirationSettings;
import com.wasteless.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpirationSettingsRepository extends JpaRepository<ExpirationSettings, Long> {
    Optional<ExpirationSettings> findByUser(User user);
    Optional<ExpirationSettings> findByUserId(Long user);
}

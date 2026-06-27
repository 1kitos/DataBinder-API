package com.databinder.config.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.databinder.config.entities.DbVersion;
import com.databinder.core.entities.CardSet.Game;

public interface DbVersionRepository extends JpaRepository<DbVersion, Long> {
    Optional<DbVersion> findByGame(Game game);
}

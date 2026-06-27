package com.databinder.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.databinder.core.entities.CardSet;

import java.util.Optional;

public interface SetRepository extends JpaRepository<CardSet, Long> {

    Optional<CardSet> findByCodeIgnoreCase(String code);

    Optional<CardSet> findByNameIgnoreCase(String name);
    
    Optional<CardSet> findByCode(String code);
}
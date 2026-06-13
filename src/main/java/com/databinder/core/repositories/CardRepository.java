package com.databinder.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.databinder.core.entities.Card;

import java.util.Optional;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByNameIgnoreCase(String name);

    List<Card> findByNameContainingIgnoreCase(String name);
}
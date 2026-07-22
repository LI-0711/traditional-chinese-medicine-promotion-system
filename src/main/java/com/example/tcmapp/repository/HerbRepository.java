package com.example.tcmapp.repository;

import com.example.tcmapp.entity.Herb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HerbRepository extends JpaRepository<Herb, Long> {

    List<Herb> findByNameContainingOrEnglishNameContaining(String name, String englishName);
}
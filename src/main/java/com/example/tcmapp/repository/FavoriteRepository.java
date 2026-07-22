package com.example.tcmapp.repository;

import com.example.tcmapp.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUsername(String username);

    Favorite findByUsernameAndHerbName(
            String username,
            String herbName);

}
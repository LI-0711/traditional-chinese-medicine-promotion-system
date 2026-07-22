package com.example.tcmapp.service;

import com.example.tcmapp.entity.Herb;
import com.example.tcmapp.repository.HerbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HerbService {

    @Autowired
    private HerbRepository herbRepository;

    public List<Herb> getAllHerbs() {
        return herbRepository.findAll();
    }

    public List<Herb> searchHerbs(String keyword) {
        return herbRepository.findByNameContainingOrEnglishNameContaining(keyword, keyword);
    }
}
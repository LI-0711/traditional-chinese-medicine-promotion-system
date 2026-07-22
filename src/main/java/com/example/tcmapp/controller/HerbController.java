package com.example.tcmapp.controller;

import com.example.tcmapp.entity.Herb;
import com.example.tcmapp.service.HerbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/herb")
public class HerbController {

    private final HerbService herbService;

    public HerbController(HerbService herbService) {
        this.herbService = herbService;
    }

    @GetMapping("/all")
    public List<Herb> getAllHerbs() {
        return herbService.getAllHerbs();
    }

    @GetMapping("/search")
    public List<Herb> searchHerbs(@RequestParam String keyword) {
        return herbService.searchHerbs(keyword);
    }
}
package com.example.tcmapp.controller;

import com.example.tcmapp.service.ExternalHerbService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/herb/external")
public class ExternalHerbController {

    private final ExternalHerbService externalHerbService;

    public ExternalHerbController(ExternalHerbService externalHerbService) {
        this.externalHerbService = externalHerbService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<ExternalHerbService.ExternalHerb>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "en") String lang) {
        if (keyword == null || keyword.isBlank() || keyword.length() > 80) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(externalHerbService.search(keyword.trim(), lang));
    }
}

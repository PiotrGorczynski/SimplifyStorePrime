package com.example.simplifyStorePrime.controller;

import com.example.simplifyStorePrime.dto.AnalyticsDTO;
import com.example.simplifyStorePrime.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsDTO> getSummary() {
        return ResponseEntity.ok(analyticsService.getSummary());
    }
}
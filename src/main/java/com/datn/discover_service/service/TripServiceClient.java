package com.datn.discover_service.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TripServiceClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String TRIP_SERVICE_URL = "http://localhost:8080/api/trips/";

    public Map<String, Object> getTripDetail(String tripId) {
        String url = TRIP_SERVICE_URL + tripId;
        return restTemplate.getForObject(url, Map.class);
    }
}

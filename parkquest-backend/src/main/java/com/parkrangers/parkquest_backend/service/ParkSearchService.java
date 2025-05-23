package com.parkrangers.parkquest_backend.service;

import com.parkrangers.parkquest_backend.model.response.Park;
import com.parkrangers.parkquest_backend.model.response.ParkSearchResponse;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
public class ParkSearchService {

    @Value("${api.key}")
    private String apiKey;

    private final String API_URL = "https://developer.nps.gov/api/v1/parks";

    public List<Park> getParks(String stateCode) throws JSONException {
        String url = String.format("%s?stateCode=%s&limit=100&api_key=%s",
                API_URL, stateCode, apiKey);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ParkSearchResponse> response = restTemplate.getForEntity(url, ParkSearchResponse.class);

        return response.getBody().getData();
    }

    public Park getParkByCode(String parkCode) throws JSONException {
        String url = String.format("%s?parkCode=%s&limit=100&api_key=%s",
                API_URL, parkCode, apiKey);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ParkSearchResponse> response = restTemplate.getForEntity(url, ParkSearchResponse.class);

        if (response.getBody() != null && !response.getBody().getData().isEmpty()) {
            return response.getBody().getData().get(0);
        } else {
            throw new IllegalArgumentException("No park found with the given parkCode: " + parkCode);
        }
    }



    public List<Park> getParksByName(String parkName) throws JSONException {
        String url = String.format("%s?q=%s&limit=100&api_key=%s",
                API_URL, parkName, apiKey);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ParkSearchResponse> response = restTemplate.getForEntity(url, ParkSearchResponse.class);

        // Filter the results to include only exact matches in the fullName field
        return response.getBody().getData().stream()
                .filter(park -> park.getFullName().toLowerCase().contains(parkName.toLowerCase()))
                .toList();
    }
}

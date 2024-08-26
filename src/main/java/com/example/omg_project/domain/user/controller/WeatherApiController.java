package com.example.omg_project.domain.user.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * 캘린더와 연동해서 위도와 경도 추가 후 날씨 표시하기
 */
@RestController
@RequiredArgsConstructor
public class WeatherApiController {

    @Value("${weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 위치 이름으로 날씨 가져오기
    @GetMapping("/api/weather")
    public ResponseEntity<String> getWeather(@RequestParam String location) {
        String geocodeUrl = String.format(
                "http://api.openweathermap.org/geo/1.0/direct?q=%s&limit=1&appid=%s",
                location, apiKey);

        try {
            ResponseEntity<String> geocodeResponse = restTemplate.getForEntity(geocodeUrl, String.class);
            if (geocodeResponse.getStatusCode() != HttpStatus.OK || geocodeResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("위치 정보를 찾을 수 없습니다.");
            }

            JsonNode geocodeJsonArray = objectMapper.readTree(geocodeResponse.getBody());
            if (geocodeJsonArray.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("위치 정보를 찾을 수 없습니다.");
            }

            JsonNode locationJson = geocodeJsonArray.get(0);
            double lat = locationJson.get("lat").asDouble();
            double lon = locationJson.get("lon").asDouble();

            return getForecastResponse(lat, lon);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("날씨 정보를 가져오는 데 실패했습니다.");
        }
    }

    // 위도와 경도로 날씨 가져오기
    @GetMapping("/api/weather/coords")
    public ResponseEntity<String> getWeatherByCoords(@RequestParam double lat, @RequestParam double lon) {
        try {
            return getForecastResponse(lat, lon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("날씨 정보를 가져오는 데 실패했습니다.");
        }
    }

    private ResponseEntity<String> getForecastResponse(double lat, double lon) {
        String forecastUrl = String.format(
                "https://api.openweathermap.org/data/2.5/forecast?lat=%s&lon=%s&appid=%s&units=metric",
                lat, lon, apiKey);

        String forecastResponse = restTemplate.getForObject(forecastUrl, String.class);
        return ResponseEntity.ok(forecastResponse);
    }
}
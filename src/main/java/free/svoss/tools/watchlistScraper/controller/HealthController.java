package free.svoss.tools.watchlistScraper.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@Slf4j
@Tag(name = "Health API", description = "Health check endpoints")
public class HealthController {

    @Operation(summary = "Health check endpoint")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now().toString());
        healthInfo.put("service", "Watchlist Scraper");
        healthInfo.put("version", "1.0.0");

        log.info("Health check called - service is running");
        return ResponseEntity.ok(healthInfo);
    }
}

package free.svoss.tools.watchlistScraper.scheduler;

import free.svoss.tools.watchlistScraper.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CacheCleanupScheduler {

    private final CacheService cacheService;

    @Value("${app.cache.retention-days:3}")
    private int retentionDays;

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000) // Every 24 hours
    public void cleanupOldCacheEntries() {
        try {
            log.info("Starting cache cleanup...");
            cacheService.cleanupOldCache(retentionDays);
            log.info("Cache cleanup completed");
        } catch (Exception e) {
            log.error("Error during cache cleanup", e);
        }
    }
}
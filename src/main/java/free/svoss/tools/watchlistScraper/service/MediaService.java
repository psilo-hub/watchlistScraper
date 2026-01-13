package free.svoss.tools.watchlistScraper.service;

import free.svoss.tools.watchlistScraper.dto.TorrentResultDto;
import free.svoss.tools.watchlistScraper.dto.*;
import free.svoss.tools.watchlistScraper.service.CacheService;
import free.svoss.tools.watchlistScraper.service.scraper.ScraperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MediaService {

    private final Map<String, ScraperService> scraperServices;
    private final CacheService cacheService;

    @Autowired
    public MediaService(List<ScraperService> scrapers, CacheService cacheService) {
        this.cacheService = cacheService;
        this.scraperServices = new HashMap<>();
        for (ScraperService scraper : scrapers) {
            scraperServices.put(scraper.getSourceName().toLowerCase(), scraper);
        }
        log.info("Loaded {} scraper services: {}", scraperServices.size(), scraperServices.keySet());
    }

    public List<SearchResultDto> search(String query, String source, String type) {
        ScraperService scraper = getScraper(source);
        try {
            return scraper.search(query, type);
        } catch (Exception e) {
            log.error("Search failed", e);
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }

    public MovieInfoDto getMovieInfo(String id, String source) {
        ScraperService scraper = getScraper(source);
        try {
            return scraper.getMovieInfo(id);
        } catch (Exception e) {
            log.error("Failed to get movie info", e);
            throw new RuntimeException("Failed to get movie info: " + e.getMessage(), e);
        }
    }

    public TvShowInfoDto getTvShowInfo(String id, String source) {
        ScraperService scraper = getScraper(source);
        try {
            return scraper.getTvShowInfo(id);
        } catch (Exception e) {
            log.error("Failed to get TV show info", e);
            throw new RuntimeException("Failed to get TV show info: " + e.getMessage(), e);
        }
    }

    public TvShowSeasonInfoDto getTvShowSeasonInfo(String id, String source) {
        ScraperService scraper = getScraper(source);
        try {
            return scraper.getTvShowSeasonInfo(id);
        } catch (Exception e) {
            log.error("Failed to get season info", e);
            throw new RuntimeException("Failed to get season info: " + e.getMessage(), e);
        }
    }

    public TvShowEpisodeInfoDto getTvShowEpisodeInfo(String id, String source) {
        ScraperService scraper = getScraper(source);
        try {
            return scraper.getTvShowEpisodeInfo(id);
        } catch (Exception e) {
            log.error("Failed to get episode info", e);
            throw new RuntimeException("Failed to get episode info: " + e.getMessage(), e);
        }
    }

    public List<TorrentResultDto> findMovieTorrents(String title, int releaseYear) {
        // Todo: Implement a default torrent scraper or make this abstract
        throw new UnsupportedOperationException("Torrent search not implemented");
    }

    public List<TorrentResultDto> findTvShowTorrents(String tvShowName, int seasonNr, int episodeNr) {
        // Todo: Implement a default torrent scraper or make this abstract
        throw new UnsupportedOperationException("Torrent search not implemented");
    }

    private ScraperService getScraper(String source) {
        ScraperService scraper = scraperServices.get(source.toLowerCase());
        if (scraper == null) {
            throw new IllegalArgumentException("Unsupported source: " + source);
        }
        return scraper;
    }
}
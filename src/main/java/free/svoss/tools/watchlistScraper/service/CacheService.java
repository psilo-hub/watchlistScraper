package free.svoss.tools.watchlistScraper.service;

import free.svoss.tools.watchlistScraper.model.HtmlCache;
import free.svoss.tools.watchlistScraper.repository.HtmlCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheService {

    private final HtmlCacheRepository cacheRepository;

    public String getCachedOrFetch(String url) throws IOException {
        // Check cache
        HtmlCache cached = cacheRepository.findById(url).orElse(null);

        if (cached != null) {
            log.info("Cache hit for URL: {}", url);
            try {
                return HtmlCache.decompress(cached.getContent());
            } catch (DataFormatException e) {
                log.error("decompression failed",e);
            }
        }

        String cleanedHtml=fetchAndCleanHtml(url);

        // Cache it
        saveToCache(url, cleanedHtml);

        return cleanedHtml;
    }
    public static String fetchAndCleanHtml(String url) throws IOException {
        // Fetch from web
        log.info("Fetching from web: {}", url);
        org.jsoup.nodes.Document doc = Jsoup.connect(url)
                .timeout(60000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:146.0) Gecko/20100101 Firefox/146.0")
                .get();

        //clean by removing scripts and style
        doc.select("style").remove();
        doc.select("script").remove();

        // Clean HTML (basic cleaning)
        // String html = doc.html();
        // String cleanedHtml = html.replaceAll("\\s+", " ").trim();

        return doc.html();

    }

    @Transactional
    public void saveToCache(String url, String html) throws IOException {
        if (html != null && !html.isEmpty()) {
            HtmlCache cache = new HtmlCache();
            cache.setUrl(url);
            cache.setCreated(LocalDateTime.now());
            cache.setContent(HtmlCache.compress(html));
            cacheRepository.save(cache);
            log.info("Cached URL: {}", url);
        }
    }

    @Transactional
    public void cleanupOldCache(int retentionDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        cacheRepository.deleteOlderThan(cutoff);
        log.info("Cleaned up cache entries older than {}", cutoff);
    }
}
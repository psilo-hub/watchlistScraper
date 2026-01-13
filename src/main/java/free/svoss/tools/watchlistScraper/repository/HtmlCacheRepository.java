package free.svoss.tools.watchlistScraper.repository;

import free.svoss.tools.watchlistScraper.model.HtmlCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Repository
public interface HtmlCacheRepository extends JpaRepository<HtmlCache, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM HtmlCache h WHERE h.created < ?1")
    void deleteOlderThan(LocalDateTime dateTime);
}
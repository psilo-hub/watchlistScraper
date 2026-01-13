package free.svoss.tools.watchlistScraper.service.scraper;

import free.svoss.tools.watchlistScraper.dto.*;
import java.io.IOException;
import java.util.List;

public interface ScraperService {

    // Search methods
    List<SearchResultDto> search(String query, String type) throws IOException;

    // Detail methods
    MovieInfoDto getMovieInfo(String id) throws IOException;
    TvShowInfoDto getTvShowInfo(String id) throws IOException;
    TvShowSeasonInfoDto getTvShowSeasonInfo(String id) throws IOException;
    TvShowEpisodeInfoDto getTvShowEpisodeInfo(String id) throws IOException;

    // Torrent methods
    List<TorrentResultDto> findMovieTorrents(String title, int releaseYear) throws IOException;
    List<TorrentResultDto> findTvShowTorrents(String tvShowName, int seasonNr, int episodeNr) throws IOException;

    // Source identifier
    String getSourceName();
}
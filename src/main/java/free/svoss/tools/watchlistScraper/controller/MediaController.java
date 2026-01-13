package free.svoss.tools.watchlistScraper.controller;

import free.svoss.tools.watchlistScraper.dto.*;
import free.svoss.tools.watchlistScraper.service.MediaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Media API", description = "API for fetching movie and TV show information")
public class MediaController extends BaseController {

    private final MediaService mediaService;

    @Operation(summary = "Search for movies or TV shows by name")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SearchResultDto>>> search(
            @RequestParam String query,
            @RequestParam String source,
            @RequestParam(required = false) String type) {
        try {
            List<SearchResultDto> results = mediaService.search(query, source, type);
            return createSuccessResponse(results);
        } catch (Exception e) {
            log.error("Search failed for query: {}", query, e);
            return createErrorResponse("Search failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get detailed movie information")
    @GetMapping("/movie/{id}")
    public ResponseEntity<ApiResponse<MovieInfoDto>> getMovieInfo(
            @PathVariable String id,
            @RequestParam String source) {
        try {
            MovieInfoDto movieInfo = mediaService.getMovieInfo(id, source);
            return createSuccessResponse(movieInfo);
        } catch (Exception e) {
            log.error("Failed to get movie info for ID: {}", id, e);
            return createErrorResponse("Failed to fetch movie info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get detailed TV show information")
    @GetMapping("/tvshow/{id}")
    public ResponseEntity<ApiResponse<TvShowInfoDto>> getTvShowInfo(
            @PathVariable String id,
            @RequestParam String source) {
        try {
            TvShowInfoDto tvShowInfo = mediaService.getTvShowInfo(id, source);
            return createSuccessResponse(tvShowInfo);
        } catch (Exception e) {
            log.error("Failed to get TV show info for ID: {}", id, e);
            return createErrorResponse("Failed to fetch TV show info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get detailed TV show season information")
    @GetMapping("/tvshow/season/{id}")
    public ResponseEntity<ApiResponse<TvShowSeasonInfoDto>> getTvShowSeasonInfo(
            @PathVariable String id,
            @RequestParam String source) {
        try {
            TvShowSeasonInfoDto seasonInfo = mediaService.getTvShowSeasonInfo(id, source);
            return createSuccessResponse(seasonInfo);
        } catch (Exception e) {
            log.error("Failed to get season info for ID: {}", id, e);
            return createErrorResponse("Failed to fetch season info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get detailed TV show episode information")
    @GetMapping("/tvshow/episode/{id}")
    public ResponseEntity<ApiResponse<TvShowEpisodeInfoDto>> getTvShowEpisodeInfo(
            @PathVariable String id,
            @RequestParam String source) {
        try {
            TvShowEpisodeInfoDto episodeInfo = mediaService.getTvShowEpisodeInfo(id, source);
            return createSuccessResponse(episodeInfo);
        } catch (Exception e) {
            log.error("Failed to get episode info for ID: {}", id, e);
            return createErrorResponse("Failed to fetch episode info: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find torrent magnet links for movies")
    @GetMapping("/torrent/movie")
    public ResponseEntity<ApiResponse<List<TorrentResultDto>>> findMovieTorrents(
            @RequestParam String title,
            @RequestParam Integer releaseYear) {
        try {
            List<TorrentResultDto> torrents = mediaService.findMovieTorrents(title, releaseYear);
            return createSuccessResponse(torrents);
        } catch (Exception e) {
            log.error("Failed to find torrents for movie: {} ({})", title, releaseYear, e);
            return createErrorResponse("Failed to find torrents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Find torrent magnet links for TV show episodes")
    @GetMapping("/torrent/tvshow")
    public ResponseEntity<ApiResponse<List<TorrentResultDto>>> findTvShowTorrents(
            @RequestParam String tvShowName,
            @RequestParam Integer seasonNr,
            @RequestParam Integer episodeNr) {
        try {
            List<TorrentResultDto> torrents = mediaService.findTvShowTorrents(tvShowName, seasonNr, episodeNr);
            return createSuccessResponse(torrents);
        } catch (Exception e) {
            log.error("Failed to find torrents for TV show: {} S{}E{}", tvShowName, seasonNr, episodeNr, e);
            return createErrorResponse("Failed to find torrents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
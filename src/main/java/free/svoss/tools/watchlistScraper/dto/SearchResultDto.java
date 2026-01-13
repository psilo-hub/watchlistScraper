package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDto {
    private String id;
    private String title;
    private String source;
    private String type;       // "movie" or "tvshow"
    private String rating;
    private Integer year;      // For movies
    private Integer startYear; // For TV shows
    private Integer endYear;   // For TV shows
}
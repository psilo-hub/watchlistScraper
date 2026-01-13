package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpisodeInfoDto {
    private Integer number;
    private String id;
    private String title;
    private String plot;
    private String episodeImageUrl;
    private String releaseDate;
}
package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvShowEpisodeInfoDto {
    private String infoSource;
    private String id;
    private String tvShowId;
    private String seasonId;
    private Integer seasonNr;
    private String episodeId;
    private Integer episodeNr;
    private String releaseDate;
    private String rating;
    private String posterUrl;
    private String description;
    private List<PersonDto> cast;
}
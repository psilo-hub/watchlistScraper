package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TvShowInfoDto {
    private String infoSource;
    private String id;
    private String title;
    private String startDate;
    private List<String> genres;
    private String runtime;
    private List<PersonDto> creators;
    private List<PersonDto> cast;
    private List<PersonDto> writers;
    private String description;
    private String rating;
    private String posterUrl;
    private Integer nrSeasons;
    private Integer nrEpisodes;
    private List<SeasonInfoDto> seasonList;
}


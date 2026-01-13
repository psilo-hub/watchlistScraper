package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieInfoDto {
    private String infoSource;
    private String id;
    private String title;
    private String runtime;
    private String releaseDate;
    private List<String> genres;
    private List<PersonDto> directors;
    private List<PersonDto> cast;
    private List<PersonDto> writers;
    private String plot;
    private String rating;
    private String posterUrl;
}


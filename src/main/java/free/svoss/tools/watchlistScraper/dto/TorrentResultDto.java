package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TorrentResultDto {
    private String name;
    private String magnetLink;
    private Double sizeMb;
    private Integer seeds;
}
package free.svoss.tools.watchlistScraper.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    private String id;
    private String name;
    private String role; // For actors/actresses
}
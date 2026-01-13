package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.EpisodeInfoDto;
import free.svoss.tools.watchlistScraper.dto.TvShowSeasonInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

@Slf4j
public class ImdbTvShowSeasonPageScraper {
    public static TvShowSeasonInfoDto getTvShowSeasonInfoFromDoc(Document doc, String url, String tvShowId, int seasonNr) {

        if (doc == null || url == null || tvShowId == null) return null;

        TvShowSeasonInfoDto dto = new TvShowSeasonInfoDto();

        dto.setInfoSource(url);
        dto.setTvShowId(tvShowId);
        dto.setSeasonNr(seasonNr);
        dto.setPosterUrl(parsePosterUrl(doc));
        dto.setEpisodeList(parseEpisodeList(doc));
        dto.setNrEpisodes(parseNrEpisodes(doc, dto.getEpisodeList()));
        dto.setStartDate(parseStartDate(doc, dto.getEpisodeList()));

        //dto.setRating(parseRating(doc));
        //log.info("Rating      : "+dto.getRating());

        /*//
        log.info("InfoSource  : " + dto.getInfoSource());
        log.info("TvShowId    : " + dto.getTvShowId());
        log.info("SeasonNr    : " + dto.getSeasonNr());
        log.info("PosterUrl   : " + dto.getPosterUrl());
        log.info("EpisodeList : " + dto.getEpisodeList());
        log.info("NrEpisodes  : " + dto.getNrEpisodes());
        log.info("StartDate   : " + dto.getStartDate());
        //*/

        return dto;
    }

    private static String parseStartDate(Document doc, List<EpisodeInfoDto> episodeList) {
        if(episodeList==null||episodeList.isEmpty())return null;

        Integer minEpisodeNumber=null;
        EpisodeInfoDto minEpisode=null;

        for(EpisodeInfoDto dto : episodeList)if(dto!=null){
            Integer dtoNr = dto.getNumber();
            if(dtoNr!=null&&(minEpisodeNumber==null||minEpisodeNumber>dtoNr)){
                minEpisodeNumber=dtoNr;
                minEpisode=dto;
            }
        }

        return minEpisode==null?null:minEpisode.getReleaseDate();
    }


    private static String parseRating(Document doc) {
        return null; // no season ratings on imdb
    }

    private static String parsePosterUrl(Document doc) {
        return ImdbMoviePageScraper.parsePosterUrl(doc);
    }

    private static List<EpisodeInfoDto> parseEpisodeList(Document doc) {

        Elements episodeWrappers = doc.select("article[class*=episode-item-wrapper]");
        if (episodeWrappers.isEmpty()) return null;

        LinkedList<EpisodeInfoDto> episodes = new LinkedList<>();
        for (Element episodeWrapper : episodeWrappers) {
            EpisodeInfoDto dto = episodeInfoDtoFromWrapper(episodeWrapper);
            if (dto != null) episodes.add(dto);
        }

        return episodes.isEmpty() ? null : episodes;
    }

    private static EpisodeInfoDto episodeInfoDtoFromWrapper(Element episodeWrapper) {

        String id = parseEpidodeId(episodeWrapper);
        if (id == null) return null;

        EpisodeInfoDto dto = new EpisodeInfoDto();

        dto.setId(id);
        dto.setNumber(parseEpisodeNr(episodeWrapper));
        dto.setTitle(parseEpisodeTitle(episodeWrapper, dto.getId()));
        dto.setPlot(parseEpisodePlot(episodeWrapper));
        dto.setEpisodeImageUrl(parseEpisodeImageUrl(episodeWrapper));
        dto.setReleaseDate(parseEpisodeReleaseDate(episodeWrapper));

        /*//
        log.info("EpisodeId   : " + dto.getId());
        log.info("EpisodeNr   : " + dto.getNumber());
        log.info("Title       : " + dto.getTitle());
        log.info("Plot        : " + dto.getPlot());
        log.info("ImageUrl    : " + dto.getEpisodeImageUrl());
        log.info("ReleaseDate : " + dto.getReleaseDate());
        //*/

        return dto;
    }

    private static String parseEpisodeReleaseDate(Element episodeWrapper) {

        Element span = episodeWrapper.selectFirst("h4[data-testid=slate-list-card-title] + span");
        if(span==null)return null;
        String text = span.text().trim();
        return text.isEmpty()?null:text;

    }

    private static String parseEpisodeImageUrl(Element episodeWrapper) {

        Element img=episodeWrapper.select("img[src*=media-amazon.com/images/]").first();
        if(img==null)return null;

        String src = img.attr("src").trim();
        if(src.isEmpty())src=img.attr("srcset").trim();

        return src.isEmpty()?null:src;

    }

    private static String parseEpisodePlot(Element episodeWrapper) {

        Element div = episodeWrapper.select("div[class=ipc-html-content-inner-div]").first();
        if(div==null)return null;

        String text = div.text().trim();

        return text.isEmpty()?null:text;
    }

    private static String parseEpisodeTitle(Element episodeWrapper, String episodeId) {
        if (episodeId == null) return null;

        Element a = episodeWrapper.select("a[href^=/title/" + episodeId + "/]").last();
        if (a == null) return null;
        String text = a.text().trim();
        if (!text.contains("∙")) return null;

        while (text.contains("∙")) text = text.substring(1).trim();

        return text.isEmpty() ? null : text;
    }

    private static Integer parseEpisodeNr(Element episodeWrapper) {
        Element a = episodeWrapper.select("a[href^=/title/tt]").last();
        if (a == null) return null;
        String text = a.text().trim();
        if (text.isBlank()) return null;
        Integer episodeNr = null;

        if (text.toLowerCase(Locale.ROOT).startsWith("s")) {
            //remove season string
            while (!text.isEmpty() && !Character.isDigit(text.charAt(0))) text = text.substring(1).trim();
            // remove season digits
            while (!text.isEmpty() && Character.isDigit(text.charAt(0))) text = text.substring(1).trim();

            //remove everything till the 'E'
            while (!text.isEmpty() && !text.toLowerCase(Locale.ROOT).startsWith("e")) text = text.substring(1).trim();

            //remove prefix till we find a digit
            while (!text.isEmpty() && !Character.isDigit(text.charAt(0))) text = text.substring(1).trim();

            while (episodeNr == null && !text.isEmpty()) {
                try {
                    episodeNr = Integer.parseInt(text);
                } catch (NumberFormatException ignored) {
                    text = text.substring(0, text.length() - 1);
                }
            }
        }
        return episodeNr;
    }

    private static String parseEpidodeId(Element episodeWrapper) {

        Element a = episodeWrapper.selectFirst("a[href^=/title/tt]");
        if (a == null) return null;
        String href = a.attr("href").trim();
        href = href.replace("/title/tt", "tt");
        while (href.contains("/") || href.contains("?") || href.contains("="))
            href = href.substring(0, href.length() - 1);
        return href;
    }

    private static Integer parseNrEpisodes(Document doc, List<EpisodeInfoDto> episodeList) {

        if (episodeList != null && !episodeList.isEmpty()) return episodeList.size();

        Elements episodeWrappers = doc.select("article[class*=episode-item-wrapper]");
        if (episodeWrappers.isEmpty()) return null;

        return episodeWrappers.size();
    }


}

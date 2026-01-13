package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.PersonDto;
import free.svoss.tools.watchlistScraper.dto.TvShowEpisodeInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Locale;

@Slf4j
public class ImdbTvShowEpisodePageScraper {
    public static TvShowEpisodeInfoDto getTvShowEpisodeInfoFromDoc(Document doc, String url, String episodeId) {
        if (doc == null) return null;

        String tvShowId = parseTvShowId(doc);
        if (tvShowId == null) return null;

        TvShowEpisodeInfoDto dto = new TvShowEpisodeInfoDto();
        dto.setInfoSource(url);
        dto.setEpisodeId(episodeId);
        dto.setTvShowId(tvShowId);
        dto.setSeasonNr(parseSeasonNr(doc));
        dto.setEpisodeNr(parseEpisodeNr(doc));
        dto.setReleaseDate(parseReleaseDate(doc));
        dto.setRating(parseRating(doc));
        dto.setPosterUrl(parsePosterUrl(doc));
        dto.setDescription(parseDescription(doc));
        dto.setCast(parseCast(doc));

        /*//
        log.info("SeasonNr    : " + dto.getSeasonNr());
        log.info("EpisodeNr   : " + dto.getEpisodeNr());
        log.info("ReleaseDate : " + dto.getReleaseDate());
        log.info("Rating      : " + dto.getRating());
        log.info("PosterUrl   : " + dto.getPosterUrl());
        log.info("Description : " + dto.getDescription());
        log.info("Cast        : " + dto.getCast());
        //*/

        return dto;
    }

    private static List<PersonDto> parseCast(Document doc) {
        return ImdbMoviePageScraper.parseCast(doc);
    }


    private static String parseDescription(Document doc) {
        return ImdbMoviePageScraper.parsePlot(doc);
    }

    private static String parsePosterUrl(Document doc) {
        return ImdbMoviePageScraper.parsePosterUrl(doc);
    }

    private static String parseRating(Document doc) {
        Element ratingDiv = doc.selectFirst("div[data-testid=hero-rating-bar__aggregate-rating__score]");
        if(ratingDiv==null)return null;
        Element firstSpan = ratingDiv.selectFirst("span");
        return firstSpan==null?null:firstSpan.text();
    }

    private static String parseReleaseDate(Document doc) {

        Element headerUl = doc.selectFirst("h1[data-testid=hero__pageTitle] + ul[class*=ipc-inline-list]");

        if(headerUl==null)return null;
        Element firstLi = headerUl.selectFirst("li");
        if(firstLi==null)return null;
        String text = firstLi.text().trim();
        if(text.isEmpty())return null;
        text=text.replace("Episode aired","").trim();
        return text;
    }


    private static String parseTvShowId(Document doc) {

        Element titleA = doc.selectFirst("div[class^=ipc-page-content-container] a[href^=/title/tt]");

        if (titleA == null) return null;

        String href = titleA.attr("href").trim();
        if (href.isEmpty()) return null;
        href = href.replace("/title/tt", "tt");
        while (href.contains("/") || href.contains("=") || href.contains("?"))
            href = href.substring(0, href.length() - 1);

        return href;
    }

    private static Integer parseEpisodeNr(Document doc) {
        Element div = doc.selectFirst("div[data-testid*=season-episode-numbers-section]");
        if(div==null)return null;

        String text = div.text().trim();
        if(text.isEmpty())return null;

        text=text.toLowerCase(Locale.ROOT);
        while (text.contains("s")||text.contains("e")||text.contains("."))
            text=text.substring(1).trim();

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Integer parseSeasonNr(Document doc) {
        Element div = doc.selectFirst("div[data-testid*=season-episode-numbers-section]");
        if(div==null)return null;

        String text = div.text().trim();
        if(text.isEmpty())return null;

        text=text.toLowerCase(Locale.ROOT).trim();
        if(!text.startsWith("s"))return null;
        while (!Character.isDigit(text.charAt(0)))text=text.substring(1);
        while (text.contains("e")||text.contains("."))text=text.substring(0,text.length()-1);

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}

package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.PersonDto;
import free.svoss.tools.watchlistScraper.dto.SeasonInfoDto;
import free.svoss.tools.watchlistScraper.dto.TvShowInfoDto;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ImdbTvShowPageScraper {
    public static TvShowInfoDto getTvShowInfoFromDoc(Document doc, String url, String id) {
        if (doc == null || id == null || url == null) return null;
        else {

            String title = parseTitle(doc);
            if (title == null) return null;

            TvShowInfoDto dto = new TvShowInfoDto();

            dto.setId(id);
            dto.setTitle(title);
            dto.setInfoSource(url);
            dto.setStartDate(parseStartDate(doc));
            dto.setGenres(parseGenres(doc));
            dto.setRuntime(parseRuntime(doc));
            dto.setCreators(parseCreators(doc));
            dto.setCast(parseCast(doc));
            dto.setWriters(parseWriters(doc));
            dto.setDescription(parseDescription(doc));
            dto.setRating(parseRating(doc));
            dto.setPosterUrl(parsePosterUrl(doc));
            dto.setNrSeasons(parseNrSeasons(doc));
            dto.setNrEpisodes(parseNrEpisodes(doc));
            dto.setSeasonList(parseSeasonList(doc));

            /*//
            log.info("Title       : " + dto.getTitle());
            log.info("Id          : " + dto.getId());
            log.info("InfoSource  : " + dto.getInfoSource());
            log.info("StartDate   : " + dto.getStartDate());
            log.info("Genres      : " + dto.getGenres());
            log.info("Runtime     : " + dto.getRuntime());
            log.info("Creators    : " + dto.getCreators());
            log.info("Cast        : " + dto.getCast());
            log.info("Writers     : " + dto.getWriters());
            log.info("Description : " + dto.getDescription());
            log.info("Rating      : " + dto.getRating());
            log.info("PosterUrl   : " + dto.getPosterUrl());
            log.info("NrSeasons   : " + dto.getNrSeasons());
            log.info("NrEpisodes  : " + dto.getNrEpisodes());
            log.info("SeasonList  : " + dto.getSeasonList());
            //*/

            return dto;
        }
    }

    private static List<SeasonInfoDto> parseSeasonList(Document doc) {
        return null; // seasons don't have ids on imdb
    }

    private static Integer parseNrEpisodes(Document doc) {

        Elements spansInH3 = doc.select("h3[class=ipc-title__text] > span[class=ipc-title__subtext]");
        if (spansInH3.isEmpty()) return null;

        Element episodesParent = null;
        for (Element span : spansInH3)
            if (episodesParent == null) {
                Element parent = span.parent();
                if (parent != null && parent.ownText().startsWith("Episode")) episodesParent = parent;
            }

        if (episodesParent == null) return null;

        Element episodeNrSpan = episodesParent.selectFirst("span[class=ipc-title__subtext]");
        if (episodeNrSpan == null) return null;

        String text = episodeNrSpan.ownText().trim();
        if (text.isEmpty()) return null;

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static Integer parseNrSeasons(Document doc) {

        Element browseEpisodesDiv = doc.selectFirst("div[data-testid=episodes-browse-episodes]");
        if (browseEpisodesDiv == null) return null;

        Element seasonLabel = browseEpisodesDiv.selectFirst("label[for=browse-episodes-season]");
        if (seasonLabel == null) return null;

        String text = seasonLabel.ownText().trim();
        if (text.isEmpty() || !Character.isDigit(text.charAt(0))) return null;

        text = text.replace("seasons", "");
        text = text.replace("season", "");
        text = text.trim();

        Integer seasons = null;
        while (seasons == null && !text.isEmpty()) {
            try {
                seasons = Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
                text = text.substring(0, text.length() - 1).trim();
            }
        }

        return seasons;
    }

    private static String parsePosterUrl(Document doc) {
        return ImdbMoviePageScraper.parsePosterUrl(doc);
    }

    private static String parseRating(Document doc) {
        return ImdbMoviePageScraper.parseRating(doc);
    }

    private static List<PersonDto> parseWriters(Document doc) {
        return ImdbMoviePageScraper.parseWriters(doc);
    }

    private static List<PersonDto> parseCast(Document doc) {
        return ImdbMoviePageScraper.parseCast(doc);
    }

    private static List<PersonDto> parseCreators(Document doc) {

        Elements titlePcList = doc.select("div > ul[data-testid=title-pc-list]");

        if (titlePcList.isEmpty()) return null;

        Element creatorSpan = null;
        Elements spans = titlePcList.select("span[class^=ipc-metadata-list-item]");
        if (spans.isEmpty()) return null;
        for (Element span : spans)
            if (creatorSpan == null && span.ownText().trim().startsWith("Creator")) creatorSpan = span;

        if (creatorSpan == null) return null;

        Element parent = creatorSpan.parent();
        if (parent == null) return null;

        Elements nameA = parent.select("a[href^=/name/nm]");
        LinkedList<PersonDto> creators = new LinkedList<>();
        for (Element a : nameA) {
            PersonDto personDto = ImdbMoviePageScraper.nameAToPersonDto(a);
            if (personDto != null) creators.add(personDto);
        }

        return creators.isEmpty() ? null : creators;
    }


    private static String parseDescription(@NotNull Document doc) {
        return ImdbMoviePageScraper.parsePlot(doc);
    }

    private static String parseStartDate(@NotNull Document doc) {
        Element headerUl = doc.selectFirst("h1[data-testid*=pageTitle] + ul");
        if (headerUl == null) return null;
        Element releaseInfoA = headerUl.selectFirst("li > a[href*=/releaseinfo/]");
        if (releaseInfoA == null) return null;
        String rangeString = releaseInfoA.text().trim();
        if (rangeString.isEmpty()) return null;

        while (rangeString.contains("-") || rangeString.contains("–"))
            rangeString = rangeString.substring(0, rangeString.length() - 1).trim();

        return rangeString.isEmpty() ? null : rangeString;

    }

    private static String parseTitle(@NotNull Document doc) {
        Element h1 = doc.selectFirst("h1[data-testid*=pageTitle]");

        if (h1 == null) return null;
        String text = h1.text().trim();

        return text.isEmpty() ? null : text;
    }

    private static String parseRuntime(@NotNull Document doc) {
        Element headerUl = doc.selectFirst("h1[data-testid*=pageTitle] + ul");
        if (headerUl == null) return null;

        Element lastLi = headerUl.select("li[role=presentation]").last();

        if (lastLi == null) return null;

        String ot = lastLi.ownText().trim();
        if (ot.isEmpty() || !Character.isDigit(ot.charAt(0))) return null;
        return ot;
    }

    private static List<String> parseGenres(@NotNull Document doc) {
        Element scrollerDiv = doc.selectFirst("div[class=ipc-chip-list__scroller]");
        if (scrollerDiv == null) return null;

        Elements aSpans = scrollerDiv.select("a[class*=ipc-chip] > span[class=ipc-chip__text]");
        if (aSpans.isEmpty()) return null;

        LinkedList<String> genres = new LinkedList<>();
        for (Element span : aSpans) {
            String ot = span.ownText().trim();
            if (!ot.isEmpty()) genres.add(ot);
        }

        return genres.isEmpty() ? null : genres;
    }
}

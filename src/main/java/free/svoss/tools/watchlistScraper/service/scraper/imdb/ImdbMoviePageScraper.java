package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.MovieInfoDto;
import free.svoss.tools.watchlistScraper.dto.PersonDto;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public class ImdbMoviePageScraper {


    public static MovieInfoDto getMovieInfoFromDoc(@NotNull Document doc, @NotNull String movieUrl, @NotNull String id) {

        MovieInfoDto movieInfo = new MovieInfoDto();
        movieInfo.setInfoSource(movieUrl);
        movieInfo.setId(id);

        String title = parseTitle(doc);
        if (title == null) return null;
        else movieInfo.setTitle(title);

        movieInfo.setRuntime(parseRuntime(doc));
        movieInfo.setReleaseDate(parseReleaseDate(doc));
        movieInfo.setGenres(parseGenres(doc));
        movieInfo.setDirectors(parseDirectors(doc));
        movieInfo.setCast(parseCast(doc));
        movieInfo.setWriters(parseWriters(doc));
        movieInfo.setPlot(parsePlot(doc));
        movieInfo.setRating(parseRating(doc));
        movieInfo.setPosterUrl(parsePosterUrl(doc));

        /*//
        log.info("Title       : " + title);
        log.info("Runtime     : " + movieInfo.getRuntime());
        log.info("ReleaseDate : " + movieInfo.getReleaseDate());
        log.info("Genres      : " + movieInfo.getGenres());
        log.info("Directors   : " + movieInfo.getDirectors());
        log.info("Cast        : " + movieInfo.getCast());
        log.info("Writers     : " + movieInfo.getWriters());
        log.info("Plot        : " + movieInfo.getPlot());
        log.info("Rating      : " + movieInfo.getRating());
        log.info("PosterUrl   : " + movieInfo.getPosterUrl());
        //*/

        return movieInfo;
    }

    public static String parsePosterUrl(@NotNull Document doc) {
        Element imgMeta = doc.selectFirst("head > meta[property=og:image]");
        if (imgMeta == null) return null;

        String content = imgMeta.attr("content").trim();
        return content.isEmpty() ? null : content;
    }

    public static String parseRating(@NotNull Document doc) {
        Element ratingDiv = doc.selectFirst("div[data-testid*=aggregate-rating__score]");
        if (ratingDiv == null) return null;

        Element firstSpan = ratingDiv.selectFirst("span");
        return firstSpan == null ? null : firstSpan.ownText();
    }

    public static List<PersonDto> parseWriters(@NotNull Document doc) {

        Element titlePcUl = doc.selectFirst("ul[data-testid=title-pc-list]");

        if (titlePcUl == null) return null;
        Elements listItems = titlePcUl.select("li > a");
        Element writersListItem = null;
        for (Element li : listItems)
            if (writersListItem == null && li.ownText().trim().equalsIgnoreCase("writers")) writersListItem = li;
        if (writersListItem == null) for (Element li : listItems)
            if (writersListItem == null && li.ownText().trim().equalsIgnoreCase("writer")) writersListItem = li;

        if (writersListItem == null) return null;
        writersListItem = writersListItem.parent();
        if (writersListItem == null) return null;

        Elements nameAs = writersListItem.select("li > a[href^=/name/nm]");
        if (nameAs.isEmpty()) return null;

        LinkedList<PersonDto> people = new LinkedList<>();
        for (Element nameA : nameAs) {
            PersonDto dto = nameAToPersonDto(nameA);
            if (dto != null) people.add(dto);
        }

        return people.isEmpty() ? null : people;
    }

    private static List<PersonDto> parseDirectors(@NotNull Document doc) {

        Element titlePcUl = doc.selectFirst("ul[data-testid=title-pc-list]");

        if (titlePcUl == null) return null;
        Elements listItems = titlePcUl.select("li > a");
        Element directorsListItem = null;
        for (Element li : listItems)
            if (directorsListItem == null && li.ownText().trim().equalsIgnoreCase("directors")) directorsListItem = li;
        if (directorsListItem == null) for (Element li : listItems)
            if (directorsListItem == null && li.ownText().trim().equalsIgnoreCase("director")) directorsListItem = li;

        if (directorsListItem == null) return null;
        directorsListItem = directorsListItem.parent();

        Elements nameAs = directorsListItem.select("li > a[href^=/name/nm]");
        if (nameAs.isEmpty()) return null;

        LinkedList<PersonDto> people = new LinkedList<>();
        for (Element nameA : nameAs) {
            PersonDto dto = nameAToPersonDto(nameA);
            if (dto != null) people.add(dto);
        }

        return people.isEmpty() ? null : people;
    }

    public static PersonDto nameAToPersonDto(@NotNull Element nameA) {
        String name = nameA.ownText().trim();
        if (name.isEmpty()) return null;
        PersonDto dto = new PersonDto();
        dto.setName(name);
        String href = nameA.attr("href").trim();
        if (!href.isEmpty()) {
            while (href.contains("/nm")) href = href.substring(1);
            while (href.contains("/")) href = href.substring(0, href.length() - 1);
            dto.setId(href);
        }
        return dto;
    }

    private static List<String> parseGenres(@NotNull Document doc) {

        Elements divsContainer = doc.select("div[data-testid=shoveler-items-container]");
        if (divsContainer.isEmpty()) return null;


        Elements titleTextDivs = divsContainer.select("div[class^=ipc-slate-card__title-text]");
        if (titleTextDivs.isEmpty()) return null;

        LinkedList<String> genres = new LinkedList<>();
        for (Element div : titleTextDivs)
            genres.add(div.ownText().trim());

        return genres;
    }

    private static String parseReleaseDate(@NotNull Document doc) {
        Element h1 = doc.selectFirst("h1[data-testid]");
        if (h1 == null) return null;

        Element infoDiv = h1.parent();
        if (infoDiv == null) return null;

        Element a = infoDiv.selectFirst("ul > li > a[href*=/releaseinfo/]");
        return a == null ? null : a.ownText();
    }

    public static String parsePlot(@NotNull Document doc) {
        Element plotP = doc.selectFirst("p[data-testid=plot]");
        if (plotP == null) return null;

        Element span = plotP.selectFirst("span");
        if (span == null) return null;

        String plot = span.ownText().trim();
        return plot.isEmpty() ? null : plot;
    }

    private static String parseRuntime(@NotNull Document doc) {
        Element runtimeLi = doc.selectFirst("li[data-testid=title-techspec_runtime]");
        if (runtimeLi == null) return null;

        Element innerLi = runtimeLi.children().select("li[role=presentation]").first();
        if (innerLi == null) return null;

        Elements spans = innerLi.select("span");
        if (spans.isEmpty()) return null;

        // 2h 6m for first
        // 126 min for last
        Element span = spans.first();
        if (span == null) return null;
        String text = span.ownText().trim();

        if (text.startsWith("(")) text = text.substring(1).trim();
        if (text.endsWith(")")) text = text.substring(0, text.length() - 1).trim();

        return text;
    }

    private static String parseTitle(@NotNull Document doc) {
        Element h1 = doc.selectFirst("h1[data-testid]");
        if (h1 == null) return null;

        String t = h1.text().trim();
        return t.isEmpty() ? null : t;
    }

    public static List<PersonDto> parseCast(@NotNull Document doc) {
        Elements h3s = doc.select("h3[class=ipc-title__text]");
        if (h3s.isEmpty()) return null;

        Element topCastH3 = null;
        for (Element h3 : h3s)
            if (h3 != null && topCastH3 == null && h3.text().startsWith("Top Cast")) topCastH3 = h3;

        if (topCastH3 == null) return null;

        Element h3Parent = topCastH3.parent();
        h3Parent = h3Parent == null ? null : h3Parent.parent();
        h3Parent = h3Parent == null ? null : h3Parent.parent();
        h3Parent = h3Parent == null ? null : h3Parent.parent();
        if (h3Parent == null) return null;

        Elements castDivs = h3Parent.select("div[data-testid=title-cast-item]");
        if (castDivs.isEmpty()) return null;

        LinkedList<PersonDto> cast = new LinkedList<>();
        for (Element castDiv : castDivs) {
            PersonDto actor = parseActorAndRoleFromCastDiv(castDiv);
            if (actor != null) cast.add(actor);
        }

        return cast.isEmpty()?null:cast;
    }

    private static PersonDto parseActorAndRoleFromCastDiv(Element castDiv) {
        String name = parseNameFromCastDiv(castDiv);
        if (name == null) return null;

        String id = parseIdFromCastDiv(castDiv);
        if (id == null) return null;

        PersonDto dto = new PersonDto();

        dto.setName(name);
        dto.setId(id);
        dto.setRole(parseRoleFromCastDiv(castDiv));

        return dto;
    }

    private static String parseRoleFromCastDiv(Element castDiv) {
        Element castItemDiv = castDiv.selectFirst("div[class=title-cast-item__characters-list]");
        if (castItemDiv == null) return null;

        Element li = castItemDiv.selectFirst("li");
        if (li == null) return null;

        Element span = li.selectFirst("a > span");
        if (span == null) return null;

        String role = span.ownText().trim();
        return role.isEmpty() ? null : role;
    }

    private static String parseIdFromCastDiv(Element castDiv) {
        Element a = castDiv.selectFirst("a[href^=/name/nm]");
        if (a == null) return null;

        String href = a.attr("href");
        while (href.contains("/nm")) href = href.substring(1);
        while (href.contains("/") || href.contains("?")) href = href.substring(0, href.length() - 1);
        return href;
    }

    private static String parseNameFromCastDiv(Element castDiv) {
        Element img = castDiv.selectFirst("img");
        if (img == null) return null;

        String name = img.attr("alt").trim();
        return name.isEmpty() ? null : name;
    }
}

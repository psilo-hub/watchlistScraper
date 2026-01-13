package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.SearchResultDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Locale;

@Slf4j
public class SearchLiScraper {
    final Element e;
    String type = null;

    public SearchLiScraper(Element e, String type) {
        this.e = e;
        this.type = type;
    }

    public SearchResultDto getDto() {
        //log.info("getDto called");

        if (type == null) getTypeFromElement();
        if (type == null) {
            // String type; // "movie" or "tvshow" if not passed
            log.warn("Failed to determine type");

            // let's assume "movie"
            type = "movie";
            //return null;
        }

        if (type == null) return null; // disables at the moment

        if ("movie".equalsIgnoreCase(type)) {

            Elements aHrefTitleTT = e.select("a[href*=/title/tt]");

            String id = parseId(aHrefTitleTT);
            String title = parseTitle(aHrefTitleTT);
            String source = "imdb";
            String rating = parseRating();
            Integer year, startYear, endYear;

            startYear = null;
            endYear = null;
            year = parseMovieYear();


            return new SearchResultDto(id, title, source, type, rating, year, startYear, endYear);

        } else if ("tvshow".equalsIgnoreCase(type)) {

            Elements aHrefTitleTT = e.select("a[href*=/title/tt]");

            String id = parseId(aHrefTitleTT);
            String title = parseTitle(aHrefTitleTT);
            String source = "imdb";
            String rating = parseRating();

            Integer year, startYear, endYear;
            Integer[] startAndEndYear = parseStartAndEndYear();
            year = null;
            startYear = (startAndEndYear == null || startAndEndYear.length != 2) ? null : startAndEndYear[0];
            endYear = (startAndEndYear == null || startAndEndYear.length != 2) ? null : startAndEndYear[1];

            return new SearchResultDto(id, title, source, type, rating, year, startYear, endYear);

        } else {
            log.error("unexpected type : " + type);
            return null;
        }
    }

    private Integer[] parseStartAndEndYear() {
        Element metadataDiv = e.selectFirst("div[class*=dli-title-metadata]");
        if (metadataDiv == null) return null;
        Element firstSpan = metadataDiv.selectFirst("span");
        if (firstSpan == null) return null;
        String ot = firstSpan.ownText().trim();
        if (ot.isEmpty()) return null;
        String start = ot;
        String end = ot;
        while (start.contains("–") || start.contains("-")) start = start.substring(0, start.length() - 1).trim();
        while (end.contains("–") || end.contains("-")) end = end.substring(1).trim();


        Integer s = null, e = null;
        try {
            s = Integer.parseInt(start);
        } catch (NumberFormatException ignored) {

        }
        try {
            e = Integer.parseInt(end);
        } catch (NumberFormatException ignored) {

        }

        return new Integer[]{s, e};
    }

    private Integer parseMovieYear() {

        Element metaDataDiv = e.selectFirst("div[class*=dli-title-metadata]");
        Element firstSpan = metaDataDiv == null ? null : metaDataDiv.selectFirst("span");

        String yearText = firstSpan == null ? null : firstSpan.ownText();
        if (yearText == null) return null;
        Integer year = null;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException ignored) {

        }

        return (year == null || year < 1800 || year > 2099) ? null : year;
    }

    private String parseRating() {
        Element ratingSpan = e.selectFirst("span[class^=ipc-rating-star]");
        if (ratingSpan == null) return null;
        String label = ratingSpan.attr("aria-label").trim().toLowerCase(Locale.ROOT);
        if (label.isEmpty()) return null;
        label = label.replace("imdb", "").trim();
        label = label.replace("rating", "").trim();
        label = label.replace(":", "").trim();

        return label.isEmpty() ? null : label;
    }

    private String parseTitle(Elements aHrefTitleTT) {
        if (aHrefTitleTT == null || aHrefTitleTT.isEmpty()) return null;

        Element last = aHrefTitleTT.last();
        if (last == null) return null;
        String text = last.text().trim();
        while (!text.isEmpty() && Character.isDigit(text.charAt(0))) text = text.substring(1).trim();
        if (text.startsWith(".")) text = text.substring(1).trim();
        return text.isEmpty() ? null : text;
    }

    private String parseId(Elements as) {
        if (as == null || as.isEmpty()) return null;
        Element a = as.first();
        String href = a == null ? null : a.attr("href");
        if (href == null || href.isEmpty()) return null;
        while (!href.startsWith("/title/tt")) href = href.substring(1);
        href = href.substring(7);
        while (href.contains("/") || href.contains("?")) href = href.substring(0, href.length() - 1);
        return href;
    }

    private void getTypeFromElement() {
        Element metadataDiv = e.selectFirst("div[class*=dli-title-metadata]");
        if (metadataDiv == null) {
            log.warn("metadata div not found");
            //System.out.println(e);
            return;
        }
        Element typeSpan = metadataDiv.selectFirst("span[class*=dli-title-type-data]");
        if (typeSpan == null) {
            log.debug("typeSpan not found ... assuming movie");
            type = "movie";
            return;
        }
        String ot = typeSpan.ownText().trim().toLowerCase(Locale.ROOT);
        if (!ot.isEmpty()) {
            if (ot.contains("series")) type = "tvshow";
            else if (ot.contains("movie")) type = "movie";
            else log.error("type string not recognized : " + ot);
        } else {
            log.warn("no ownText in typeSpan:\n" + typeSpan + "\n");
            //todo shall we assume it's a movie?
        }
    }
}

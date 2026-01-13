package free.svoss.tools.watchlistScraper;

import free.svoss.tools.watchlistScraper.dto.*;
import free.svoss.tools.watchlistScraper.service.CacheService;
import free.svoss.tools.watchlistScraper.service.scraper.ImdbScraperService;
import free.svoss.tools.watchlistScraper.service.scraper.imdb.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

@Slf4j
public class ScrapeTestImdb { // for quick testing
    public static void main(String[] args) {

        testUrlBuilding(
                "Forrest Gump",
                "movie",
                "https://www.imdb.com/search/title/?title=Forrest%20Gump&title_type=feature,tv_movie"
        );
        testUrlBuilding(
                "Big Bang Theory",
                "tvshow",
                "https://www.imdb.com/search/title/?title=Big%20Bang%20Theory&title_type=tv_series,tv_miniseries"
        );

        //testHtmlDownload("Forrest Gump","movie");
        //testHtmlDownload("Big Bang Theory","tvshow");

        testSearchResultParsing("Forrest Gump","movie");

        testSearchResultParsing("Big Bang Theory","tvshow");

        // test fetching details by id

        testFetchMovieDetails("tt10268488");
        testFetchMovieDetails("tt0113101");

        testFetchTvShowDetails("tt3530232");
        testFetchTvShowSeasonDetails("tt3530232",5);
        testFetchTvShowEpisodeDetails("tt7628928");

        testFetchPersonDetails("nm1056659");

    }

    private static void testFetchPersonDetails(String imdbPersonId) {
        String url = ImdbScraperService.getPersonUrl(imdbPersonId);
        String html = DummyPageFetcher.getPageHtml(url);
        Document doc = Jsoup.parse(html,url);
        System.out.println("\nPARSING "+url+"\n");

        PersonDto dto = ImdbPersonPageScraper.getPersonInfoFromDoc(doc);
        System.out.println(dto);
    }

    private static void testFetchTvShowEpisodeDetails(String episodeId) {
        String url = ImdbScraperService.getTvShowEpisodeUrl(episodeId);
        String html = DummyPageFetcher.getPageHtml(url);
        Document doc = Jsoup.parse(html,url);
        System.out.println("\nPARSING "+url+"\n");

        TvShowEpisodeInfoDto dto = ImdbTvShowEpisodePageScraper.getTvShowEpisodeInfoFromDoc(doc,url,episodeId);
        System.out.println(dto);

    }
    private static void testFetchTvShowSeasonDetails(String id,int seasonNr) {
        String url = ImdbScraperService.getTvShowSeasonUrl(id,seasonNr);
        String html = DummyPageFetcher.getPageHtml(url);
        Document doc = Jsoup.parse(html,url);
        System.out.println("\nPARSING "+url+"\n");

        TvShowSeasonInfoDto dto = ImdbTvShowSeasonPageScraper.getTvShowSeasonInfoFromDoc(doc,url,id,seasonNr);
        System.out.println(dto);
    }

    private static void testFetchTvShowDetails(String id) {
        String url = ImdbScraperService.getTvShowUrl(id);
        String html = DummyPageFetcher.getPageHtml(url);
        Document doc = Jsoup.parse(html,url);
        System.out.println("\nPARSING "+url+"\n");

        TvShowInfoDto dto = ImdbTvShowPageScraper.getTvShowInfoFromDoc(doc,url,id);
        System.out.println(dto);

    }

    private static void testFetchMovieDetails(String id) {
        String url = ImdbScraperService.getMovieUrl(id);
        String html = DummyPageFetcher.getPageHtml(url);
        Document doc = Jsoup.parse(html,url);
        System.out.println("\nPARSING "+url+"\n");
        MovieInfoDto dto = ImdbMoviePageScraper.getMovieInfoFromDoc(doc,url,id);
        System.out.println(dto);

    }


    private static void testSearchResultParsing(String query, String type) {
        String resultUrl = ImdbScraperService.buildSearchUrl(query, type);
        String html;
        try {
            html = CacheService.fetchAndCleanHtml(resultUrl);
            List<SearchResultDto> dtoList =
                    ImdbScraperService.extractSearchResults(query, type, resultUrl, Jsoup.parse(html));
            log.info("Parsed " + dtoList.size() + " items");
        } catch (IOException e) {
            log.error("Failed to fetch page",e);
        }
    }

    private static void testHtmlDownload(String query, String type) {
        String url = ImdbScraperService.buildSearchUrl(query, type);
        try {
            String html = CacheService.fetchAndCleanHtml(url);
            System.out.println(html);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void testUrlBuilding(String query, String type, String url) {
        String resultUrl = ImdbScraperService.buildSearchUrl(query, type);
        if (resultUrl.equalsIgnoreCase(url))
            log.info("Url correct for type \"{}\" and query \"{}\"", type, query);
        else
            log.error(
                    "Url incorrect for type \"{}\" and query \"{}\"\nexpected : {}\nreceived : {}\n",
                    type, query, url, resultUrl
            );
    }
}

package free.svoss.tools.watchlistScraper.service.scraper;

import free.svoss.tools.watchlistScraper.dto.*;
import free.svoss.tools.watchlistScraper.service.CacheService;
import free.svoss.tools.watchlistScraper.service.scraper.imdb.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImdbScraperService implements ScraperService {

    private final CacheService cacheService;

    public static String getMovieUrl(String id) {
        return "https://www.imdb.com/title/"+id;
    }

    public static String getTvShowEpisodeUrl(String episodeId) {
        return episodeId==null?null:("https://www.imdb.com/title/"+episodeId);
    }

    public static String getTvShowSeasonUrl(String id, int seasonNr) {
        if(id==null||seasonNr<0)return null;
        return "https://www.imdb.com/title/"+id+"/episodes/?season="+seasonNr+"&ref_=tt_eps_sn_"+seasonNr;
    }

    public static String getTvShowUrl(String id) {
        return id==null?null:("https://www.imdb.com/title/"+id);
    }

    public static String getPersonUrl(String imdbPersonId) {
        return imdbPersonId==null?null:("https://www.imdb.com/name/"+imdbPersonId+"/");
    }

    @Override
    public String getSourceName() {
        return "imdb";
    }

    @Override
    public List<SearchResultDto> search(String query, String type) throws IOException {
        // Build search URL
        String searchUrl = buildSearchUrl(query, type);

        // Get HTML from cache or web
        String html = cacheService.getCachedOrFetch(searchUrl);

        // Parse with JSoup
        Document doc = Jsoup.parse(html);

        return extractSearchResults(query,type,searchUrl,doc);
    }
    public static List<SearchResultDto> extractSearchResults(String query,String type,String searchUrl,Document doc){
        List<SearchResultDto> results = new ArrayList<>();
        if(doc==null)return results;

        // Extract search results
        Element ul=doc.selectFirst("ul[class^=ipc-metadata-list]");
        if(ul==null){
            log.error("Failed to find ul");
            return results;
        }//else log.info("ul found");
        Elements listItems =ul.select("li[class=ipc-metadata-list-summary-item]");
        //log.info("Search resulted in "+listItems.size()+" items");
        if(listItems.isEmpty())
            System.out.println(ul);
        for(Element li : listItems){
            SearchResultDto searchResultDto = searchResultDtoFromListItem(li,type);
            if(searchResultDto!=null)results.add(searchResultDto);
        }

        return results;
    }

    public static SearchResultDto searchResultDtoFromListItem(Element li,String type) {
        if(li==null)return null;

        Element e = li;
        while (e!=null&&e.childrenSize()==1)e=e.firstElementChild();

        SearchLiScraper liScraper=new SearchLiScraper(e,type);
        return liScraper.getDto();

    }

    @Override
    public MovieInfoDto getMovieInfo(String id) throws IOException {
        String movieUrl = "https://www.imdb.com/title/" + id;
        String html = cacheService.getCachedOrFetch(movieUrl);
        Document doc = Jsoup.parse(html);

        return ImdbMoviePageScraper.getMovieInfoFromDoc(doc,movieUrl,id);

    }

    public static String buildSearchUrl(String query, String type) {
        if(query==null)query="";
        //if(type==null||!type.equalsIgnoreCase("movie"))type="tvshow";

        String baseUrl = "https://www.imdb.com/search/title/?title=";
        String encodedQuery = query.trim().toLowerCase(Locale.ROOT).replace(" ", "%20");

        if ("movie".equalsIgnoreCase(type)) {
            return baseUrl +  encodedQuery + "&title_type=feature,tv_movie";
        } else if ("tvshow".equalsIgnoreCase(type)) {
            return baseUrl +  encodedQuery + "&title_type=tv_series,tv_miniseries";
        } else {
            return baseUrl +  encodedQuery ;
        }
    }

    @Override
    public TvShowInfoDto getTvShowInfo(String id) throws IOException {
        String url = getTvShowUrl(id);
        if(url==null)return null;
        String html = cacheService.getCachedOrFetch(url);//CacheService.fetchAndCleanHtml(url);
        if(html.isEmpty())return null;

        Document doc;
        try {
            doc = Jsoup.parse(html, url);
        } catch (Exception e) {
            log.error("Failed to parse html for "+url+"\n"+e.getMessage());
            return null;
        }

        return ImdbTvShowPageScraper.getTvShowInfoFromDoc(doc,url,id);
    }

    @Override
    public TvShowSeasonInfoDto getTvShowSeasonInfo(String seasonId) throws IOException {
        // IMDb doesn't have seasonIds, so this id is {tvShowId}-{seasonNr}
        if(seasonId==null||!seasonId.contains("-")||seasonId.indexOf("-")!=seasonId.lastIndexOf("-"))return null;
        String tvShowId = seasonId;
        String seasonNrString = seasonId;
        while (tvShowId.contains("-"))tvShowId=tvShowId.substring(0,tvShowId.length()-1);
        seasonNrString=seasonNrString.replace(tvShowId+"-","");

        Integer seasonNr=null;
        try {
            seasonNr=Integer.parseInt(seasonNrString);
        } catch (NumberFormatException e) {
            return null;
        }
        return getTvShowSeasonInfo(tvShowId,seasonNr);
    }

    //@Override //todo add to MediaService etc
    public TvShowSeasonInfoDto getTvShowSeasonInfo(String tvShowId,int seasonNr) throws IOException {
        String url =getTvShowSeasonUrl(tvShowId, seasonNr);
        if(url==null)return null;
        String html = cacheService.getCachedOrFetch(url);//CacheService.fetchAndCleanHtml(url);
        if(html.isEmpty())return null;

        Document doc;
        try {
            doc = Jsoup.parse(html, url);
        } catch (Exception e) {
            log.error("Failed to parse html for "+url+"\n"+e.getMessage());
            return null;
        }

        return ImdbTvShowSeasonPageScraper.getTvShowSeasonInfoFromDoc(doc,url,tvShowId,seasonNr);
    }

    @Override
    public TvShowEpisodeInfoDto getTvShowEpisodeInfo(String episodeId) throws IOException {
        String url = getTvShowEpisodeUrl(episodeId);
        if(url==null)return null;
        String html = cacheService.getCachedOrFetch(url);//CacheService.fetchAndCleanHtml(url);
        if(html.isEmpty())return null;

        Document doc;
        try {
            doc = Jsoup.parse(html, url);
        } catch (Exception e) {
            log.error("Failed to parse html for "+url+"\n"+e.getMessage());
            return null;
        }
        return ImdbTvShowEpisodePageScraper.getTvShowEpisodeInfoFromDoc(doc,url,episodeId);
    }

    @Override
    public List<TorrentResultDto> findMovieTorrents(String title, int releaseYear) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findMovieTorrents'");
    }

    @Override
    public List<TorrentResultDto> findTvShowTorrents(String tvShowName, int seasonNr, int episodeNr) throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findTvShowTorrents'");
    }
}
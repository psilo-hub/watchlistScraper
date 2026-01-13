package free.svoss.tools.watchlistScraper.service.scraper.imdb;

import free.svoss.tools.watchlistScraper.dto.PersonDto;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ImdbPersonPageScraper {
    public static PersonDto getPersonInfoFromDoc(Document doc) {
        if (doc == null) return null;
        String url = doc.baseUri();
        if (!url.contains("/name/nm")) return null;
        String id = url;
        while (id.contains("/name/nm")) id = id.substring(1);
        id = id.replace("name/", "");
        while (id.contains("/")) id = id.substring(0, id.length() - 1);

        String name = parseName(doc);
        if(name==null||name.isEmpty())return null;
        return new PersonDto(id,name,null);
    }

    private static String parseName(Document doc) {
        Element title = doc.selectFirst("head > title");
        if(title==null)return null;
        String text = title.ownText();
        text=text.replace("IMDb","").trim();
        return text.replace("-","").trim();

    }
}

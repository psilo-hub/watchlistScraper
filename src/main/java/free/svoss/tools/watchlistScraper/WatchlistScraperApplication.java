package free.svoss.tools.watchlistScraper;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WatchlistScraperApplication {
    public final static String LIGHT_GREEN_FG = "\033[92m";
    public final static String BOLD = "\033[1m";
    public final static String RESET = "\033[0m";
    public final static String CLS = "\033[2J";
    public final static String BLINK = "\033[5m";
    public final static String CL = "\r                                          \r";


    public static void main(String[] args) {
        System.out.print(LIGHT_GREEN_FG + CL + BOLD + CL + CLS + CL + BLINK + CL);
        System.out.println("watchlistScraper by Stefan V");
        System.out.println(RESET + CL);


        SpringApplication.run(WatchlistScraperApplication.class, args);

    }
}
package free.svoss.tools.watchlistScraper;

import free.svoss.tools.watchlistScraper.service.CacheService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
class DummyPageFetcher {
    private final static File tempFolder=new File(System.getProperty("user.home")+ File.separator+"watchlistScraper"+File.separator+"temp");
    public static String getPageHtml(String url){
        if(url==null)return null;
        File tempFile = getTempFileForUrl(url);
        String html =null;
        if(!tempFile.exists()){
            try {
                html = CacheService.fetchAndCleanHtml(url);
            } catch (IOException e) {
                log.error("Failed to fetch page ",e);
            }
            if(html!=null)
                saveHtml(tempFile,html);
        }else html=loadHtml(tempFile);

        return html;
    }

    private static String loadHtml(File tempFile) {
        try {
            return new String(Files.readAllBytes(tempFile.toPath()),StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading html file",e);
        }
        return null;
    }

    private static void saveHtml(File tempFile, String html) {
        File parent = tempFile.getParentFile();
        if(!parent.exists()&&!parent.mkdirs())log.error("Failed to create folder "+parent);

        try {
            Files.write(tempFile.toPath(),html.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("Failed to save page",e);
        }
    }

    private static File getTempFileForUrl(String url) {
        return new File(tempFolder+File.separator+getHexHash(url)+".html");
    }

    private static String getHexHash(String s) {
        byte[] hashBytes= null;
        try {
            hashBytes = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        String hex = bytesToHex(hashBytes);
        return hex.substring(0,24);
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}

package bg.sofia.uni.fmi.mjt.bookmarks.server.shortener;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class UrlShortener {
    private static final Path PATH_TO_API_KEY =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "resources", "apikey.txt");
    private static String apiKey;
    private static final URI API_URL = URI.create("https://api-ssl.bitly.com/v4/shorten");

    static {
        try (BufferedReader in = new BufferedReader(new FileReader(PATH_TO_API_KEY.toString()))) {
            apiKey = in.readLine();
        } catch (IOException e) {
            Logger.log(e, "UrlShortener encountered a problem requiring the API key");
            System.out.println("Could not acquire the api key");
        }
    }

    public static String shorten(String url) {
        if (url == null || url.isEmpty()) {
            RuntimeException thrown = new IllegalArgumentException("Could not shorten url because it is null");
            Logger.log(thrown, "UrlShortener encountered a problem in method shorten.");
            System.out.println("Could not shorten link");
            return url;
        }

        String body = String.format("{ \"long_url\": \"%s\", \"domain\": \"bit.ly\" }", url);
        HttpClient client = HttpClient.newBuilder().build();

        var request = HttpRequest.newBuilder()
            .uri(API_URL)
            .header("Authorization", apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .version(HttpClient.Version.HTTP_2)
            .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            Logger.log(e, "UrlShortener encountered a problem in method shorten.");
            System.out.println("API may be down. Could not send request");
            System.exit(0);
        }
        final int[] successCode = {200, 201};
        if (response.statusCode() != successCode[0] && response.statusCode() != successCode[1]) {
            Logger.log(new IllegalArgumentException("API received a bad request"),
                "UrlShortener encountered a problem in method shorten.");
            System.out.println("Bad request");
            return url;
        }

        Gson gson = new Gson();
        Map<?, ?> bodyOfResponse = gson.fromJson(response.body(), Map.class);
        return bodyOfResponse.get("link").toString();
    }
}

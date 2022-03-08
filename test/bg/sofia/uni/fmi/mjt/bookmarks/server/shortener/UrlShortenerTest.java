package bg.sofia.uni.fmi.mjt.bookmarks.server.shortener;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UrlShortenerTest {

    private final Path logFilePath =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");


    @BeforeAll
    @AfterAll
    void setup() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @Test
    void shorten() {
        String validUrl = "https://facebook.com/";
        String invalidUrl = "test";
        assertNull(UrlShortener.shorten(null));
        assertEquals(invalidUrl,UrlShortener.shorten(invalidUrl));
        assertNotEquals(invalidUrl,UrlShortener.shorten(validUrl));

    }
}
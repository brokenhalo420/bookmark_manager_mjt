package bg.sofia.uni.fmi.mjt.bookmarks.server.parser;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ParserTest {

    private final String validUrl = "https://facebook.com";
    private final String invalidUrl = "test";
    private final Path
        pathToLog = Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");

    @BeforeAll
    void preparations() {
        try {
            Files.deleteIfExists(pathToLog);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @AfterAll
    void cleanup() {
        try {
            Files.deleteIfExists(pathToLog);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @Test
    void getPageData() {
        List<String> empty = Parser.getPageData(null);
        try {
            assert empty == null &&
                Files.lines(pathToLog).collect(Collectors.joining(" "))
                    .contains("Error occured in Parser class with url:");
        }
        catch(IOException e){
            assert false;
        }

        List<String> invalid = Parser.getPageData(invalidUrl);
        try {
            assert invalid == null &&
                Files.lines(pathToLog).collect(Collectors.joining(" "))
                    .contains("Parsing failed. Url does not respond to request");
        }
        catch(IOException e){
            assert false;
        }

        List<String> valid = Parser.getPageData(validUrl);
        assert valid != null && valid.size() == 6 && valid.get(0).equals("Facebook");
    }
}
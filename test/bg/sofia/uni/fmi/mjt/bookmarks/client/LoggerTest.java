package bg.sofia.uni.fmi.mjt.bookmarks.client;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoggerTest {

    private final Path logFilePath =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "client", "logs", "log.txt");

    @BeforeAll
    void preparations() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @AfterAll
    void cleanup() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @Test
    void log() {
        RuntimeException testException = new IllegalArgumentException("test");
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.out.println("No file to cleanup");
        }
        Logger.log(testException,"Test message");
        String expectedOutput = "Test message: java.lang.IllegalArgumentException: test";
        try {
            assert Files.lines(logFilePath).collect(Collectors.joining("\n")).contains(expectedOutput);
        } catch (IOException e) {
            System.out.println("No log file created");
            assert false;
        }

    }
}
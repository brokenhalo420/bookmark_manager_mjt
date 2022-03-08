package bg.sofia.uni.fmi.mjt.bookmarks.server.components.group;

import bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark.Bookmark;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GroupTest {

    private final Path logFilePath =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");
    private Group group;
    private Bookmark testBookmark;

    @BeforeAll
    void setup() {
        group = new Group("testGroup");
        testBookmark = new Bookmark("https://facebook.com/");
    }

    @AfterAll
    void cleanLogs() {
        try {
            Files.deleteIfExists(logFilePath);
        } catch (IOException e) {
            System.out.println("Test could not delete log file");
        }
    }

    @AfterEach
    void cleanup() {
        group = new Group("testGroup");
    }

    @Test
    void getTitle() {
        assertEquals("testGroup", group.getTitle());
    }

    @Test
    void getBookmarks() {
        assertEquals(0, group.getBookmarks().size());
        group.add(testBookmark);
        assertEquals("Facebook", group.getBookmarks().stream().findFirst().get().getTitle());
    }

    @Test
    void add() {
        assertFalse(group.add(null));
        group.add(testBookmark);
        assertEquals("Facebook", group.getBookmarks().stream().findFirst().get().getTitle());
    }

    @Test
    void remove() {
        assertNull(group.remove(null));
        group.add(testBookmark);

        assertNull(group.remove("https://twitter.com/"));

        group.remove("https://facebook.com/");
        assertEquals(0, group.getBookmarks().size());

        assertNull(group.remove(testBookmark.getUrl()));
    }

    @Test
    void testToString() {
        String output = "testGroup:" + System.lineSeparator() + System.lineSeparator();
        assertEquals(output, group.toString());

        group.add(testBookmark);
        String bookmarks =
            "testGroup:" + System.lineSeparator() + "\t" + testBookmark.toString() + System.lineSeparator() +
                System.lineSeparator();
        assertEquals(bookmarks,group.toString());
    }
}
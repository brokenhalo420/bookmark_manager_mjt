package bg.sofia.uni.fmi.mjt.bookmarks.server.components.user;

import bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark.Bookmark;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.group.Group;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private final Path logFilePath =
        Paths.get("src", "bg", "sofia", "uni", "fmi", "mjt", "bookmarks", "server", "logs", "log.txt");
    private Group group;
    private Bookmark testBookmark;
    private User user;

    @BeforeAll
    void setup() {
        user = new User("administrator", "administrator");
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
    void clean() {
        group = new Group("testGroup");
        user = new User("administrator", "administrator");
    }

    @Test
    void createGroup() {
        assertEquals("Invalid group title", user.createGroup(null));
        assertEquals("Group created successfully", user.createGroup("testGroup"));
        assertEquals("Could not create group. It already exists", user.createGroup("testGroup"));
    }

    @Test
    void addBookmark() {
        String groupNameErrorMessage = "Could not add bookmark. Group name is null or empty.";
        String urlErrorMessage = "Could not add bookmark. Url is null or empty.";
        String noGroupFoundMessage = "Could not add bookmark. No such group exists.";
        String successMessage = "Bookmark successfully added to group";

        assertEquals(groupNameErrorMessage, user.addBookmark(null, testBookmark.getUrl()));
        assertEquals(urlErrorMessage, user.addBookmark("test", null));
        assertEquals(noGroupFoundMessage, user.addBookmark("test", testBookmark.getUrl()));

        user.createGroup(group.getTitle());
        assertEquals(successMessage, user.addBookmark(group.getTitle(), testBookmark.getUrl()));

    }

    @Test
    void removeBookmark() {
        String groupNameErrorMessage = "Could not remove bookmark. Group name is null or empty";
        String urlErrorMessage = "Could not remove bookmark. URL is null or empty";
        String noGroupFoundMessage = "Could not remove bookmark. No such group exists.";
        String noSuchBookmarkExistsMessage = "Cannot remove bookmark. No such bookmark exists.";
        String successMessage = "Bookmark removed successfully";

        assertEquals(groupNameErrorMessage, user.removeBookmark(null, testBookmark.getUrl()));
        assertEquals(urlErrorMessage, user.removeBookmark("test", null));
        assertEquals(noGroupFoundMessage, user.removeBookmark("test", testBookmark.getUrl()));

        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        assertEquals(noSuchBookmarkExistsMessage, user.removeBookmark(group.getTitle(), "https://twitter.com/"));
        assertEquals(successMessage, user.removeBookmark(group.getTitle(), testBookmark.getUrl()));
    }

    @Test
    void getAllBookmarksByGroup() {
        assertNull(user.getAllBookmarks(null));
        assertNull(user.getAllBookmarks("test"));

        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        assertEquals(1, user.getAllBookmarks(group.getTitle()).size());

    }

    @Test
    void GetAllBookmarks() {
        assertNull(user.getAllBookmarks());

        user.createGroup(group.getTitle());
        assertEquals(0, user.getAllBookmarks().size());

        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        assertEquals(1, user.getAllBookmarks().size());
    }

    @Test
    void findBookmarksByTitle() {
        assertEquals(0, user.findBookmarksByTitle(null).size());
        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        assertEquals(0, user.findBookmarksByTitle("Twitter").size());
        assertEquals(1, user.findBookmarksByTitle("Facebook").size());

    }

    @Test
    void findBookmarksByTags() {
        assertEquals(0, user.findBookmarksByTags((String[]) null).size());
        String[] empty = {};
        assertEquals(0, user.findBookmarksByTags(empty).size());

        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        String[] invalidTags = {"twitter", "fmi", "java"};
        String[] validTags = {"facebook", "регистрация"};
        String[] emptyTags = {"",""};
        assertEquals(0,user.findBookmarksByTags(emptyTags).size());
        assertEquals(0, user.findBookmarksByTags(invalidTags).size());
        assertEquals(1, user.findBookmarksByTags(validTags).size());
    }

    @Test
    void cleanup() {
        user.createGroup(group.getTitle());
        user.cleanup();
        assertEquals(0, user.getAllBookmarks().size());

        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(), testBookmark.getUrl());
        user.cleanup();
        assertEquals(1, user.getAllBookmarks().size());

        testBookmark.getKeywords().forEach(s -> {
            assertEquals(1, user.getKeywords().get(s).size());
        });

    }

    @Test
    void importFromChrome() {
        assertTrue(user.importFromChrome());
        assertEquals(1, user.getGroups().size());
    }

    @Test
    void getUsername() {
        assertEquals("administrator", user.getUsername());
    }

    @Test
    void getPassword() {
        assertEquals("administrator", user.getPassword());
    }

    @Test
    void getGroups() {
        assertEquals(0, user.getGroups().size());
        user.createGroup(group.getTitle());
        assertEquals(1, user.getGroups().size());
    }

    @Test
    void getKeywords() {
        assertEquals(0,user.getKeywords().size());
        user.createGroup(group.getTitle());
        user.addBookmark(group.getTitle(),testBookmark.getUrl());

        assertEquals(5, user.getKeywords().size());
    }
}
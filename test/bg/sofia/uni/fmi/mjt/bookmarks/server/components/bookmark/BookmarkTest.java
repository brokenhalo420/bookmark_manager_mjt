package bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookmarkTest {

    private Bookmark bookmark;

    @BeforeAll
    void setup() {
        bookmark = new Bookmark("https://facebook.com/");
    }

    @Test
    void getTitle() {
        assertEquals("Facebook", bookmark.getTitle());
    }

    @Test
    void getUrl() {
        assertEquals("https://facebook.com/", bookmark.getUrl());
    }

    @Test
    void getKeywords() {
        String[] keywords = {"заявка","facebook", "страница", "регистрация", "обработена"};
        assertArrayEquals(keywords, bookmark.getKeywords().toArray(String[]::new));
    }

    @Test
    void testEquals() {
        Bookmark copy = new Bookmark(bookmark.getUrl());
        assertEquals(bookmark, copy);
    }

    @Test
    void testHashCode() {
        Bookmark copy = new Bookmark(bookmark.getUrl());
        assertEquals(copy.hashCode(),bookmark.hashCode());
    }

    @Test
    void testToString() {
        assertEquals("Facebook | https://facebook.com/",bookmark.toString());
    }
}
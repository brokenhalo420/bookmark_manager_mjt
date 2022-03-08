package bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark;

import bg.sofia.uni.fmi.mjt.bookmarks.server.parser.Parser;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Bookmark {
    private String title;
    private String url;
    private Set<String> keywords;

    public Bookmark(String url) {
        this.url = url;
        List<String> data = Parser.getPageData(url);
        this.title = data.get(0);
        this.keywords = new LinkedHashSet<>(data.stream().skip(1).toList());
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Set<String> getKeywords() {
        return keywords;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bookmark bookmark = (Bookmark) o;
        return title.equals(bookmark.title) && url.equals(bookmark.url) && keywords.equals(bookmark.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, url, keywords);
    }

    @Override
    public String toString() {
        return title + " | " + url ;
    }
}

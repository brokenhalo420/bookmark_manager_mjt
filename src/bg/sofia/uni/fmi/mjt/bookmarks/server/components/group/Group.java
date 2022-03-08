package bg.sofia.uni.fmi.mjt.bookmarks.server.components.group;

import bg.sofia.uni.fmi.mjt.bookmarks.server.logger.Logger;
import bg.sofia.uni.fmi.mjt.bookmarks.server.components.bookmark.Bookmark;

import java.util.HashSet;
import java.util.Set;

public class Group {
    private final String title;
    private final Set<Bookmark> bookmarks;

    public Group(String title) {
        this.title = title;
        bookmarks = new HashSet<>();
    }

    public String getTitle() {
        return title;
    }

    public Set<Bookmark> getBookmarks() {
        return bookmarks.size() == 0 ? new HashSet<>() : bookmarks;
    }

    public boolean add(Bookmark bookmark) {
        if (bookmark == null) {
            RuntimeException exception = new IllegalArgumentException("Group cannot add bookmark. It is null");
            Logger.log(exception, "Group class encountered an error in method add");
            return false;
        }

        bookmarks.add(bookmark);
        return true;
    }

    public Bookmark remove(String url) {
        if (url == null || url.isEmpty()) {
            RuntimeException exception =
                new IllegalArgumentException("Group cannot remove bookmark. URL provided is null");
            Logger.log(exception, "Group class encountered an error in method remove");
            return null;
        }

        Bookmark item = null;
        for (Bookmark bookmark : bookmarks) {
            if (bookmark.getUrl().equals(url)) {
                item = bookmark;
                break;
            }
        }

        if (item == null) {
            return null;
        }

        bookmarks.remove(item);
        return item;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder(title + ":" + System.lineSeparator());
        for (Bookmark item : bookmarks) {
            output.append("\t").append(item.toString()).append(System.lineSeparator());
        }
        output.append(System.lineSeparator());
        return output.toString();
    }
}

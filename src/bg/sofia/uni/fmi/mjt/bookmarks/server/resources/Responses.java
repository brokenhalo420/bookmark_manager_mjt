package bg.sofia.uni.fmi.mjt.bookmarks.server.resources;

public enum Responses {
    SUCCESS(0),
    FAIL(1),
    DISCONNECT(2);

    private final int response;

    private Responses(int response) {
        this.response = response;
    }

    public int get() {
        return response;
    }
}

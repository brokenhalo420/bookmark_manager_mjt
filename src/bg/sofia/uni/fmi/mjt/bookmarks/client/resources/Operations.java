package bg.sofia.uni.fmi.mjt.bookmarks.client.resources;

public enum Operations {
    HELP(1),
    REGISTER(2),
    LOGIN(3),
    LOGOUT(4),
    CREATE_GROUP(5),
    ADD_TO_GROUP(6),
    ADD_TO_GROUP_SHORT(7),
    REMOVE_FROM(8),
    LIST(9),
    LIST_GROUP(10),
    SEARCH_BY_TITLE(11),
    SEARCH_BY_TAGS(12),
    CLEANUP(13),
    IMPORT_FROM_CHROME(14),
    QUIT(15),
    NONE(16);

    private final int command;

    Operations(int command) {
        this.command = command;
    }

    public int get() {
        return command;
    }


}

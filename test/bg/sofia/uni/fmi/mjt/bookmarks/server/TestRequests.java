package bg.sofia.uni.fmi.mjt.bookmarks.server;

import bg.sofia.uni.fmi.mjt.bookmarks.server.resources.Operations;

public class TestRequests {
    public static final String LOGIN_FROM_UNKNOWN_USER_REQUEST = Operations.LOGIN.get() + System.lineSeparator()
        + "login unknown unknown" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String LOGOUT_UNLOGGED_REQUEST = Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String CREATE_GROUP_UNLOGGED_REQUEST = Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String ADD_TO_GROUP_UNLOGGED_REQUEST = Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup https://facebook.com/" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String REMOVE_FROM_GROUP_UNLOGGED_REQUEST =
        Operations.REMOVE_FROM.get() + System.lineSeparator()
            + "remove-from testGroup https://facebook.com/" + System.lineSeparator()
            + Operations.QUIT.get() + System.lineSeparator()
            + "quit" + System.lineSeparator();

    public static final String LIST_ALL_AND_GROUP_UNLOGGED_REQUEST = Operations.LIST.get() + System.lineSeparator()
        + "list" + System.lineSeparator()
        + Operations.LIST_GROUP.get() + System.lineSeparator()
        + "list --group-name testGroup" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String SEARCH_UNLOGGED_REQUEST = Operations.SEARCH_BY_TITLE.get() + System.lineSeparator()
        + "search --title Facebook" + System.lineSeparator()
        + Operations.SEARCH_BY_TAGS.get() + System.lineSeparator()
        + "search --tags facebook заявка" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String CLEANUP_UNLOGGED_REQUEST = Operations.CLEANUP.get() + System.lineSeparator()
        + "cleanup" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String IMPORT_FROM_CHROME_UNLOGGED_REQUEST =
        Operations.IMPORT_FROM_CHROME.get() + System.lineSeparator()
            + "import-from-chrome" + System.lineSeparator()
            + Operations.QUIT.get() + System.lineSeparator()
            + "quit" + System.lineSeparator();

    public static final String QUIT_REQUEST = Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String REGISTER_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String REGISTER_SAME_USER_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String LOGIN_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String LOGGED_IN_REGISTER_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String LOGIN_WHEN_LOGGED_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String CREATE_GROUP_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String ADD_TO_GROUP_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.ADD_TO_GROUP_SHORT.get() + System.lineSeparator()
        + "add-to --shorten testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String REMOVE_FROM_GROUP_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.REMOVE_FROM.get() + System.lineSeparator()
        + "remove-from testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String LIST_ALL_AND_GROUP_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.LIST.get() + System.lineSeparator()
        + "list" + System.lineSeparator()
        + Operations.LIST_GROUP.get() + System.lineSeparator()
        + "list --group-name testGroup1" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String SEARCH_BY_TITLE_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.SEARCH_BY_TITLE.get() + System.lineSeparator()
        + "search --title Facebook" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String SEARCH_BY_TAGS_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.ADD_TO_GROUP.get() + System.lineSeparator()
        + "add-to testGroup1 https://facebook.com/" + System.lineSeparator()
        + Operations.SEARCH_BY_TAGS.get() + System.lineSeparator()
        + "search --tags facebook регистрация" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String CLEANUP_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.CREATE_GROUP.get() + System.lineSeparator()
        + "new-group testGroup1" + System.lineSeparator()
        + Operations.CLEANUP.get() + System.lineSeparator()
        + "cleanup" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();

    public static final String IMPORT_FROM_CHROME_REQUEST = Operations.REGISTER.get() + System.lineSeparator()
        + "register administrator administrator" + System.lineSeparator()
        + Operations.LOGIN.get() + System.lineSeparator()
        + "login administrator administrator" + System.lineSeparator()
        + Operations.IMPORT_FROM_CHROME.get() + System.lineSeparator()
        + "import-from-chrome" + System.lineSeparator()
        + Operations.LOGOUT.get() + System.lineSeparator()
        + "logout" + System.lineSeparator()
        + Operations.QUIT.get() + System.lineSeparator()
        + "quit" + System.lineSeparator();
}

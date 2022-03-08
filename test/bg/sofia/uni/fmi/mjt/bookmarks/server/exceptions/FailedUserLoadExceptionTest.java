package bg.sofia.uni.fmi.mjt.bookmarks.server.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FailedUserLoadExceptionTest {

    @Test
    void testFailedUserLoadException() {
        FailedUserLoadException test = new FailedUserLoadException("test");
        FailedUserLoadException test2 = new FailedUserLoadException("test", new IllegalArgumentException("testArgument"));
        assertEquals("test", test.getMessage());
        assertThrows(RuntimeException.class,() -> {throw test;});
        assertEquals("test", test2.getMessage());
        assertThrows(RuntimeException.class,() -> {throw test;});
    }

}
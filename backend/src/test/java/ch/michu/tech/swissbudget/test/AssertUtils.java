package ch.michu.tech.swissbudget.test;

import static org.junit.jupiter.api.Assertions.fail;

public class AssertUtils {

    private AssertUtils() {

    }

    public static void niceAssertInSecond(long expected, long actual) {
        if (expected == actual || expected + 1 == actual) {
            return;
        }

        fail("second is not in given or next second: e:%s a:%s".formatted(expected, actual));
    }
}

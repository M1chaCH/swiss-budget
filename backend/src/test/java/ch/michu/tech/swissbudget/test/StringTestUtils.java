package ch.michu.tech.swissbudget.test;

public class StringTestUtils {

    private StringTestUtils() {
    }

    public static int countStringOccurences(String origin, String query) {
        int count = 0;
        int index = origin.indexOf(query);

        while (index != -1) {
            count++;
            index = origin.indexOf(origin, index + 1);
        }

        return count;
    }
}

package ch.michu.tech.swissbudget.framework.data.loading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.michu.tech.swissbudget.framework.utils.DateBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class DataLoaderTest {

    private static final String TEST_PATTERN = "yyyy-MM-dd";

    @Test
    void dataLoader_TestBuildStatement() {
        List<String> lines = List.of(
            "::test_table",
            "column1,column2,column3,,,,,",
            "value,morevalue,mostvalue",
            "second,evenmore,line,,,,",
            "third,ehhe,test",
            ",,",
            "",
            ",,",
            "::second_table",
            "column1",
            "value"
        );

        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());
        assertEquals(2, statements.size());
        String firstQuery = statements.poll();
        assertNotNull(firstQuery);
        assertEquals(
            "insert into test_table (column1,column2,column3) values ('value','morevalue','mostvalue'), ('second','evenmore','line'), ('third','ehhe','test');",
            firstQuery.toLowerCase(Locale.ROOT));

        String secondQuery = statements.poll();
        assertNotNull(secondQuery);
        assertEquals("insert into second_table (column1) values ('value');", secondQuery.toLowerCase(Locale.ROOT));
    }

    @Test
    void dataLoader_TestGenGuid() {
        List<String> lines = List.of(
            "::test_table",
            "column1,column2,column3,,,,,",
            "{{useGuid(id)}},morevalue,mostvalue",
            "second,{{genGuid()}},{{genGuid(id)}},,,,",
            ",,",
            "",
            ",,",
            "::second_table",
            "column1",
            "{{useGuid(id)}}"
        );

        String randomUuid = UUID.randomUUID().toString();
        String idUuid = UUID.randomUUID().toString();

        DataLoader dataLoader = mock(DataLoader.class);
        when(dataLoader.genGuid(isNull())).thenReturn(randomUuid);
        when(dataLoader.genGuid(isNotNull())).thenReturn(idUuid);
        when(dataLoader.useGuid(isNotNull())).thenReturn(idUuid);

        // TODO will need to wait for mockito to run with java 21 https://github.com/mockito/mockito/issues/3037
//        Map<String, String> statements = dataLoader.buildStatements(lines, Map.of());
        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());
        String firstStatement = statements.poll();
        assertNotNull(firstStatement);
        assertFalse(firstStatement.contains("genGuid("));
        assertFalse(firstStatement.contains("useGuid("));
        assertEquals(211, firstStatement.length());
//        assertEquals(1, countStringOccurences(firstStatement, randomUuid));
//        assertEquals(2, countStringOccurences(firstStatement, idUuid));

        String secondStatement = statements.poll();
        assertNotNull(secondStatement);
        assertFalse(secondStatement.contains("useGuid("));
        assertEquals(83, secondStatement.length());
//        assertEquals(0, countStringOccurences(firstStatement, randomUuid));
//        assertEquals(1, countStringOccurences(firstStatement, idUuid));
    }

    @Test
    void dataLoader_TestHashString() {
        List<String> lines = List.of(
            "::test_table",
            "column1,column2,column3,,,,,",
            "{{hashWithSalt(cool-string)}},{{salt()}},mostvalue"
        );

        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());
        String firstStatement = statements.poll();
        assertNotNull(firstStatement);
        assertFalse(firstStatement.contains("hashWithSalt"));
        assertFalse(firstStatement.contains("salt"));
    }

    @Test
    void dataLoader_TestDate() {
        List<String> lines = List.of(
            "::today",
            "column1",
            "{{date(today)}}",
            "::lastofmonth",
            "column1",
            "{{date(lastofmonth)}}",
            "::firstofmonth",
            "column1",
            "{{date(firstofmonth)}}",
            "::lastofyear",
            "column1",
            "{{date(lastofyear)}}",
            "::firstofyear",
            "column1",
            "{{date(firstofyear)}}"
        );

        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());
        String todayStatement = statements.poll();
        assertNotNull(todayStatement);
        final String expectedToday = DateBuilder.today().formatted(TEST_PATTERN);
        if (!todayStatement.contains(expectedToday)) {
            fail("today is incorrect in: e: " + expectedToday + " statement: " + todayStatement);
        }

        String lastOfMonthStatement = statements.poll();
        assertNotNull(lastOfMonthStatement);
        final String expectedLoM = DateBuilder.today().lastDayOfMonth().formatted(TEST_PATTERN);
        if (!lastOfMonthStatement.contains(expectedLoM)) {
            fail("last day of month is incorrect in: e: " + expectedLoM + " statement: " + lastOfMonthStatement);
        }

        String firstOfMonthStatement = statements.poll();
        assertNotNull(firstOfMonthStatement);
        final String expectedFoM = DateBuilder.today().firstDayOfMonth().formatted(TEST_PATTERN);
        if (!firstOfMonthStatement.contains(expectedFoM)) {
            fail("first day of month is incorrect in: e: " + expectedFoM + " statement: " + firstOfMonthStatement);
        }

        String lastOfYearStatement = statements.poll();
        assertNotNull(lastOfYearStatement);
        final String expectedLoY = DateBuilder.today().lastDayOfYear().formatted(TEST_PATTERN);
        if (!lastOfYearStatement.contains(expectedLoY)) {
            fail("last day of year is incorrect in: e: " + expectedLoY + " statement: " + lastOfYearStatement);
        }

        String firstDayOfYearStatement = statements.poll();
        assertNotNull(firstDayOfYearStatement);
        final String expectedFoY = DateBuilder.today().firstDayOfYear().formatted(TEST_PATTERN);
        if (!firstDayOfYearStatement.contains(expectedFoY)) {
            fail("first day of year is incorrect in: e: " + expectedFoY + " statement: " + firstDayOfYearStatement);
        }
    }

    @Test
    void dataLoader_TestDateWithParams() {
        List<String> lines = List.of(
            "::day",
            "column1",
            "{{date(5&&day)}}",
            "::week",
            "column1",
            "{{date(5&&week)}}",
            "::month",
            "column1",
            "{{date(5&&month)}}",
            "::year",
            "column1",
            "{{date(5&&year)}}"
        );
        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());

        String dayStatement = statements.poll();
        assertNotNull(dayStatement);
        final String expectedDay = DateBuilder.today().addDays(5).formatted(TEST_PATTERN);
        if (!dayStatement.contains(expectedDay)) {
            fail("day stmt is incorrect e: " + expectedDay + " stmt: " + dayStatement);
        }

        String weekStatement = statements.poll();
        assertNotNull(weekStatement);
        final String expectedWeek = DateBuilder.today().addWeeks(5).formatted(TEST_PATTERN);
        if (!weekStatement.contains(expectedWeek)) {
            fail("week stmt is incorrect e: " + expectedWeek + " stmt: " + weekStatement);
        }

        String monthStmt = statements.poll();
        assertNotNull(monthStmt);
        final String expectedMonth = DateBuilder.today().addMonths(5).formatted(TEST_PATTERN);
        if (!monthStmt.contains(expectedMonth)) {
            fail("month stmt is incorrect e: " + expectedMonth + " stmt: " + monthStmt);
        }

        String yearStmt = statements.poll();
        assertNotNull(yearStmt);
        final String expectedYear = DateBuilder.today().addYears(5).formatted(TEST_PATTERN);
        if (!yearStmt.contains(expectedYear)) {
            fail("year stmt is incorrect e: " + expectedYear + " stmt: " + yearStmt);
        }
    }

    @Test
    void dataLoader_TestDateChains() {
        List<String> lines = List.of(
            "::chain",
            "column1",
            "{{date(1&&year)->date(4&&month)->date(firstofmonth)->date(10&&day)->date(-1&&week)}}"
        );
        Queue<String> statements = new DataLoader(null).buildStatements(lines, Map.of());

        String stmt = statements.poll();
        assertNotNull(stmt);
        final String expected = DateBuilder.today().addYears(1).addMonths(4).firstDayOfMonth().addDays(3).formatted(TEST_PATTERN);
        if (!stmt.contains(expected)) {
            fail("date chain did not work: e: " + expected + " statement: " + stmt);
        }
    }
}
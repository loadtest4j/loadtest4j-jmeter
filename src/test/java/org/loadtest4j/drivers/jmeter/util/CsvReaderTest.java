package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class CsvReaderTest {

    private static URL fixture(String name) {
        return CsvReaderTest.class.getClassLoader().getResource(name);
    }

    @Test
    public void testParse() {
        final CsvReader csvReader = new CsvReader(fixture("csv/valid.csv"));

        final List<Map<String, String>> result = new ArrayList<>();
        csvReader.forEach(result::add);

        final Map<String, String> expected = new LinkedHashMap<String, String>() {{
            put("a", "foo");
            put("b", "bar");
        }};

        assertThat(result)
                .containsExactly(expected);
    }

    @Test(expected = CsvException.class)
    public void testParseInvalidCsv() {
        final CsvReader csvReader = new CsvReader(fixture("csv/invalid.csv"));

        csvReader.forEach(line -> {});
    }
}
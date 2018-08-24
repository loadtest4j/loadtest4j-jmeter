package org.loadtest4j.drivers.jmeter.parser;

import jdk.nashorn.api.scripting.URLReader;
import org.junit.Test;

import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CsvStreamTest {

    private static Reader fixture(String name) {
        return new URLReader(CsvStreamTest.class.getClassLoader().getResource(name));
    }

    @Test
    public void testParse() {
        final List<Map<String, String>> result = CsvStream.stream(fixture("csv/valid.csv"))
                .collect(Collectors.toList());

        final Map<String, String> expected = new LinkedHashMap<String, String>() {{
            put("a", "foo");
            put("b", "bar");
        }};

        assertThat(result)
                .containsExactly(expected);
    }

    @Test(expected = CsvException.class)
    public void testParseInvalidCsv() {
        CsvStream.stream(fixture("csv/invalid.csv"))
                .collect(Collectors.toList());
    }
}

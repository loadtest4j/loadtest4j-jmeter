package org.loadtest4j.drivers.jmeter.util;

import jdk.nashorn.api.scripting.URLReader;
import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

public class CsvReader {

    private final URL csv;

    public CsvReader(URL csv) {
        this.csv = csv;
    }

    public void forEach(Consumer<Map<String, String>> consumer) {
        try (ICsvMapReader csvReader = new CsvMapReader(new URLReader(csv), CsvPreference.STANDARD_PREFERENCE)) {
            final String[] header = csvReader.getHeader(true);

            Map<String, String> line;
            while ((line = csvReader.read(header)) != null) {
                consumer.accept(line);
            }
        } catch (IOException | SuperCsvException e) {
            throw new CsvException(e);
        }
    }
}

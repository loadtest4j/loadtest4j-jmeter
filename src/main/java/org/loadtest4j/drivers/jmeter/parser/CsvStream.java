package org.loadtest4j.drivers.jmeter.parser;

import org.supercsv.exception.SuperCsvException;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

final class CsvStream {
    private CsvStream() {}

    public static Stream<Map<String, String>> stream(Reader csv) {
        final CsvMapIterator iterator = CsvMapIterator.fromReader(csv);

        return streamFromIterator(iterator);
    }

    private static Stream<Map<String, String>> streamFromIterator(CsvMapIterator iterator) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(iterator, 0), false)
                .onClose(() -> {
                    try {
                        iterator.close();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /*
     * Adapted from https://www.yegor256.com/2015/04/30/iterating-adapter.html
     */
    private static class CsvMapIterator implements Iterator<Map<String, String>>, AutoCloseable {

        private final ICsvMapReader reader;
        private final String[] header;

        private final Queue<Map<String, String>> buffer = new LinkedList<>();

        private CsvMapIterator(ICsvMapReader reader, String[] header) {
            this.reader = reader;
            this.header = header;
        }

        private static CsvMapIterator fromReader(Reader reader) {
            try {
                final ICsvMapReader csvReader = new CsvMapReader(reader, CsvPreference.STANDARD_PREFERENCE);
                final String[] header = csvReader.getHeader(true);
                return new CsvMapIterator(csvReader, header);
            } catch (IOException e) {
                throw new CsvException(e);
            }
        }

        private Optional<Map<String, String>> read() {
            try {
                final Map<String, String> thing = reader.read(header);

                return Optional.ofNullable(thing);
            } catch (IOException | SuperCsvException e) {
                throw new CsvException(e);
            }
        }

        @Override
        public boolean hasNext() {
            if (this.buffer.isEmpty()) {
                this.read().ifPresent(this.buffer::add);
            }
            return !this.buffer.isEmpty();
        }

        @Override
        public Map<String, String> next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }

            return this.buffer.poll();
        }

        @Override
        public void close() throws Exception {
            this.reader.close();
        }
    }
}

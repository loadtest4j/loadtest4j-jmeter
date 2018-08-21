package org.loadtest4j.drivers.jmeter.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class StreamReader {
    public static String streamToString(InputStream is) {
        // From https://stackoverflow.com/a/5445161
        final Scanner s = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static InputStream stringToStream(String str) {
        return new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
    }
}

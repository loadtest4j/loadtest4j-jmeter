package org.loadtest4j.drivers.jmeter.util;

import java.util.Map;
import java.util.stream.Collectors;

public class QueryString {
    private final Map<String, String> queryParams;

    private QueryString(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public String toString() {
        if (queryParams.isEmpty()) {
            return "";
        }

        return "?" + queryParams.entrySet()
                .stream()
                .map((entry) -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("&"));
    }

    public static String fromMap(Map<String, String> queryParams) {
        return new QueryString(queryParams).toString();
    }
}

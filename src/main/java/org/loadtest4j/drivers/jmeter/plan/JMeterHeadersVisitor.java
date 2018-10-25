package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JMeterHeadersVisitor implements Body.Visitor<Map<String, String>> {

    private final Map<String, String> headers;

    JMeterHeadersVisitor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Map<String, String> string(String body) {
        return fixHeaders(this.headers);
    }

    @Override
    public Map<String, String> parts(List<BodyPart> body) {
        return this.headers;
    }

    /**
     * When you do not give jmeter a Content-Type header, it uses x-www-form-urlencoded and attaches the request body as
     * a form parameter, NOT an actual post body. Apply a default Content-Type header if one is not found.
     *
     * @param headers the user's headers
     * @return the fixed headers
     */
    static Map<String, String> fixHeaders(Map<String, String> headers) {
        final boolean hasContentType = headers.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase("Content-Type"));

        if (hasContentType) {
            return headers;
        } else {
            return concatMaps(headers, Collections.singletonMap("Content-Type", "text/plain"));
        }
    }

    private static Map<String, String> concatMaps(Map<String, String> a, Map<String, String> b) {
        return Stream.concat(a.entrySet().stream(), b.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

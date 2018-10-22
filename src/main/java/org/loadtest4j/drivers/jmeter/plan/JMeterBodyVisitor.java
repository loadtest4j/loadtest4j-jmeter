package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.util.ContentTypes;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class JMeterBodyVisitor implements Body.Visitor<TestPlan.HttpSampler> {

    private final String domain;
    private final Map<String, String> headers;
    private final String method;
    private final String name;
    private final String path;
    private final int port;
    private final String protocol;

    JMeterBodyVisitor(String domain, Map<String, String> headers, String method, String name, String path, int port, String protocol) {
        this.domain = domain;
        this.headers = headers;
        this.method = method;
        this.name = name;
        this.path = path;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public TestPlan.HttpSampler string(String content) {
        final List<TestPlan.Header> headers = headers(fixHeaders(this.headers));
        return new TestPlan.HttpSampler(content, domain, headers, method, name, path, port, protocol);
    }

    @Override
    public TestPlan.HttpSampler parts(List<BodyPart> body) {
        final List<TestPlan.Header> headers = headers(this.headers);
        final List<TestPlan.File> files = body.stream()
                .map(part -> part.accept(new JMeterBodyPartVisitor()))
                .collect(Collectors.toList());
        return new TestPlan.HttpSampler(domain, files, headers, method, name, path, port, protocol);
    }

    private static List<TestPlan.Header> headers(Map<String, String> headerMap) {
        return headerMap.entrySet().stream()
                .map(entry -> new TestPlan.Header(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
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

    private static class JMeterBodyPartVisitor implements BodyPart.Visitor<TestPlan.File> {

        @Override
        public TestPlan.File stringPart(String name, String content) {
            throw new UnsupportedOperationException("This driver does not support string parts in multipart requests.");
        }

        @Override
        public TestPlan.File filePart(Path content) {
            final String name = Optional.ofNullable(content.getFileName()).orElseThrow(() -> new NullPointerException("Path did not have a filename.")).toString();
            final String path = content.toAbsolutePath().toString();
            final String contentType = ContentTypes.detect(content);

            return new TestPlan.File(contentType, name, path);
        }
    }
}

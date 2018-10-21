package org.loadtest4j.drivers.jmeter.plan;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.drivers.jmeter.util.QueryString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Does not have any clue about JMeter's internal data structures, just renders XML externally.
 */
public class BlackBoxTestPlanFactory implements TestPlanFactory {

    private final String domain;

    private final int numThreads;

    private final int port;

    private final String protocol;

    private final int rampTime;

    public BlackBoxTestPlanFactory(String domain, int numThreads, int port, String protocol, int rampTime) {
        this.domain = domain;
        this.numThreads = numThreads;
        this.port = port;
        this.protocol = protocol;
        this.rampTime = rampTime;
    }

    @Override
    public File create(List<DriverRequest> driverRequests) {
        try {
            final File jmxFile = File.createTempFile("loadtest4j", ".jmx");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(jmxFile), StandardCharsets.UTF_8)) {
                create(driverRequests, writer);
            }
            return jmxFile;
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    void create(List<DriverRequest> driverRequests, Writer writer) throws IOException {
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile("loadtest4j.jmx.mustache");
        mustache.execute(writer, testPlan(driverRequests)).flush();
    }

    private TestPlan testPlan(List<DriverRequest> driverRequests) {
        final List<TestPlan.HttpSampler> httpSamplers = driverRequests.stream()
                .map(req -> {
                    final List<TestPlan.Header> headers = headers(fixHeaders(req.getHeaders()));
                    final String name = req.getMethod() + " " + req.getPath();
                    final String path = req.getPath() + QueryString.fromMap(req.getQueryParams());
                    final String method = req.getMethod();
                    return req.getBody().accept(new JMeterBodyVisitor(domain, headers, method, name, path, port, protocol));
                })
                .collect(Collectors.toList());

        final TestPlan.ThreadGroup threadGroup = new TestPlan.ThreadGroup(httpSamplers, numThreads, rampTime);

        return new TestPlan(threadGroup);
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

    private static class JMeterBodyVisitor implements Body.Visitor<TestPlan.HttpSampler> {

        private final String domain;
        private final List<TestPlan.Header> headers;
        private final String method;
        private final String name;
        private final String path;
        private final int port;
        private final String protocol;

        private JMeterBodyVisitor(String domain, List<TestPlan.Header> headers, String method, String name, String path, int port, String protocol) {
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
            return new TestPlan.HttpSampler(content, domain, headers, method, name, path, port, protocol);
        }

        @Override
        public TestPlan.HttpSampler parts(List<BodyPart> body) {
            final List<TestPlan.File> files = body.stream()
                    .map(part -> part.accept(new JMeterBodyPartVisitor()))
                    .collect(Collectors.toList());
            return new TestPlan.HttpSampler(domain, files, headers, method, name, path, port, protocol);
        }
    }

    private static class JMeterBodyPartVisitor implements BodyPart.Visitor<TestPlan.File> {

        @Override
        public TestPlan.File stringPart(String name, String content) {
            throw new UnsupportedOperationException("This driver does not support string parts in multipart requests.");
        }

        @Override
        public TestPlan.File filePart(Path content) {
            final String name = content.getFileName().toString();
            final String path = content.toAbsolutePath().toString();
            // FIXME this is broken, it does not correctly sense the content type
            final String contentType = "text/plain";
            //try {
           //     contentType = Files.probeContentType(content);
          //  } catch (IOException e) {
            //    throw new LoadTesterException(e);
           // }
            return new TestPlan.File(contentType, name, path);
        }
    }

    private static Map<String, String> concatMaps(Map<String, String> a, Map<String, String> b) {
        return Stream.concat(a.entrySet().stream(), b.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

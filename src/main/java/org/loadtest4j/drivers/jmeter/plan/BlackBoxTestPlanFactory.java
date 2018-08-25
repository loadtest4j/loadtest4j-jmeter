package org.loadtest4j.drivers.jmeter.plan;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverRequest;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
        final MustacheFactory mf = new DefaultMustacheFactory();
        final Mustache mustache = mf.compile("loadtest4j.jmx.mustache");

        final File jmxFile;
        try {
            jmxFile = File.createTempFile("loadtest4j", ".jmx");
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }

        try (Writer writer = createWriter(jmxFile)) {
            mustache.execute(writer, testPlan(driverRequests)).flush();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }

        return jmxFile;
    }

    private static Writer createWriter(File file) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
    }

    private TestPlan testPlan(List<DriverRequest> driverRequests) {
        final List<TestPlan.HttpSampler> httpSamplers = driverRequests.stream()
                .map(req -> {
                    final List<TestPlan.Header> headers = headers(fixHeaders(req.getHeaders()));
                    final String name = req.getMethod() + " " + req.getPath();
                    return new TestPlan.HttpSampler(req.getBody(), domain, headers, req.getMethod(), name, req.getPath(), port, protocol);
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

    private static Map<String, String> concatMaps(Map<String, String> a, Map<String, String> b) {
        return Stream.concat(a.entrySet().stream(), b.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

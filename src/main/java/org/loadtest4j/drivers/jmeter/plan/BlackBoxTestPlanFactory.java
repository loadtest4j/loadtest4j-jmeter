package org.loadtest4j.drivers.jmeter.plan;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.drivers.jmeter.util.QueryString;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                    final String name = req.getMethod() + " " + req.getPath();
                    final String path = req.getPath() + QueryString.fromMap(req.getQueryParams());

                    final List<TestPlan.Header> headers = headers(req.getBody().accept(new JMeterHeadersVisitor(req.getHeaders())));
                    final String body = req.getBody().accept(new JMeterBodyVisitor());
                    final List<TestPlan.File> files = req.getBody().accept(new JMeterFilesVisitor());
                    return new TestPlan.HttpSampler(body, domain, files, headers, req.getMethod(), name, path, port, protocol);
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
}

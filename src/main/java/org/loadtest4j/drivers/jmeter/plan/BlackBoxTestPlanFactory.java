package org.loadtest4j.drivers.jmeter.plan;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverRequest;

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
                    final List<TestPlan.Header> headers = headers(req.getHeaders());
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
}

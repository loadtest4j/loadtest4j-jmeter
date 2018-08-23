package org.loadtest4j.drivers.jmeter;

import org.apache.jmeter.util.JMeterUtils;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.jmeter.engine.Engine;
import org.loadtest4j.drivers.jmeter.engine.ShellEngine;
import org.loadtest4j.drivers.jmeter.util.Resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

class JMeter implements Driver {

    private final String domain;

    private final int numThreads;

    private final int port;

    private final String protocol;

    private final int rampUp;

    JMeter(String domain, int numThreads, int port, String protocol, int rampUp) {
        this.domain = domain;
        this.numThreads = numThreads;
        this.port = port;
        this.protocol = protocol;
        this.rampUp = rampUp;
    }

    @Override
    public DriverResult run(List<DriverRequest> requests) {
        validateNotEmpty(requests);

        loadJmeterProperties();

        final File testPlan = createTestPlan(requests);

        final File resultFile = runJmeter(testPlan);

        return readResult(resultFile);
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
        }
    }

    /**
     * Run this before constructing jmeter API objects because they may read these properties.
     */
    private static void loadJmeterProperties() {
        // FIXME shift this into NativeEngine, and only create saveservice.properties here
        final File jmeterHome = createTempDirectory("jmeter_home");
        extractConfigSettings(jmeterHome);

        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());

        final Path jmeterProperties = jmeterHome.toPath().resolve("bin").resolve("jmeter.properties");
        JMeterUtils.loadJMeterProperties(jmeterProperties.toString());
    }

    private File createTestPlan(List<DriverRequest> driverRequests) {
        final JMeterTestPlan testPlan = new JMeterTestPlan(domain, numThreads, port, protocol, rampUp);
        return testPlan.create(driverRequests);
    }

    private static File runJmeter(File testPlan) {
        final Engine engine = ShellEngine.standard();
        return engine.runJmeter(testPlan);
    }

    private static JMeterResult readResult(File resultFile) {
        final JMeterResultParser resultParser = new JMeterResultParser();
        return resultParser.parse(resultFile);
    }

    private static File createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix).toFile();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static void extractConfigSettings(File jmeterHome) {
        try {
            Resources.copy("bin", new File(jmeterHome, "bin"));
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}

package org.loadtest4j.drivers.jmeter;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSampler;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.jmeter.util.Resources;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;

class JMeter implements Driver {

    private final String domain;

    private final int port;

    private final String protocol;

    JMeter(String domain, int port, String protocol) {
        this.domain = domain;
        this.port = port;
        this.protocol = protocol;
    }

    @Override
    public DriverResult run(List<DriverRequest> requests) {
        validateNotEmpty(requests);

        loadJmeterProperties();

        final HashTree hashTree = createHashTree(requests);
        final File jmxFile = saveTree(hashTree);

        final File resultFile = ShellRunner.standard().runJmeter(jmxFile);

        return readResult(resultFile);
    }

    // Run this before constructing jmeter API objects because they may read these properties.
    // FIXME shift this into NativeRunner, and only create saveservice.properties here
    private static void loadJmeterProperties() {
        final File jmeterHome = createTempDirectory("jmeter_home");
        extractConfigSettings(jmeterHome);

        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());

        final Path jmeterProperties = jmeterHome.toPath().resolve("bin").resolve("jmeter.properties");
        JMeterUtils.loadJMeterProperties(jmeterProperties.toString());
    }

    private static File saveTree(HashTree hashTree) {
        // FIXME make this not hardcoded
        final File jmxFile = new File(System.getProperty("user.dir") + "/example.jmx");
        try {
            final FileOutputStream os = new FileOutputStream(jmxFile);
            SaveService.saveTree(hashTree, os);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
        return jmxFile;
    }

    private static JMeterResult readResult(File resultFile) {
        // FIXME use SaveService.loadTestResults(resultFileReader, new ResultCollectorHelper(...));

        final long ok = 0;
        final long ko = 0;
        final Duration actualDuration = Duration.ZERO;
        final String reportUrl = resultFile.getAbsolutePath();
        final DriverResponseTime responseTime = new JMeterResponseTime();
        return new JMeterResult(actualDuration, ok, ko, reportUrl, responseTime);
    }

    private HashTree createHashTree(List<DriverRequest> driverRequests) {
        // FIXME loop over requests, not just the first one
        final HTTPSampler httpSampler = createHttpSampler(protocol, domain, port, driverRequests.get(0));

        final LoopController loopCtrl = new LoopController();
        loopCtrl.setName("Loadtest4j Loop Controller");
        loopCtrl.setLoops(1);
        loopCtrl.addTestElement(httpSampler);
        loopCtrl.setFirst(true);
        loopCtrl.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopCtrl.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopCtrl.initialize();

        final ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Loadtest4j Thread Group");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(1);
        threadGroup.setSamplerController(loopCtrl);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        final TestPlan testPlan = new TestPlan("Loadtest4j Test Plan");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        final HashTree testPlanTree = new HashTree();
        testPlanTree.add("testPlan", testPlan);
        testPlanTree.add("loopController", loopCtrl);
        testPlanTree.add("threadGroup", threadGroup);
        testPlanTree.add("httpSampler", httpSampler);

        return testPlanTree;
    }

    private static File createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix).toFile();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static HTTPSampler createHttpSampler(String protocol, String domain, int port, DriverRequest driverRequest) {
        final HTTPSampler httpSampler = new HTTPSampler();
        httpSampler.setName(driverRequest.getMethod() + " " + protocol + "://" + domain + driverRequest.getPath());
        httpSampler.setProtocol(protocol);
        httpSampler.setDomain(domain);
        httpSampler.setPort(port);
//        httpSampler.setArguments(createArguments(driverRequest.getQueryParams()));
//        httpSampler.setHeaderManager(createHeaderManager(driverRequest.getHeaders()));
        httpSampler.setMethod(driverRequest.getMethod());
        httpSampler.setPath(driverRequest.getPath());
//  FIXME add request body (this code does not work)   httpSampler.addNonEncodedArgument("", driverRequest.getBody(), "");
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSampler.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        return httpSampler;
    }

    private static Arguments createArguments(Map<String, String> queryParams) {
        final Arguments arguments = new Arguments();

        queryParams.forEach(arguments::addArgument);

        return arguments;
    }

    private static HeaderManager createHeaderManager(Map<String, String> headers) {
        final HeaderManager headerManager = new HeaderManager();

        headers.forEach((key, value) -> headerManager.add(new Header(key, value)));

        return headerManager;
    }

    private static <T> void validateNotEmpty(Collection<T> requests) {
        if (requests.size() < 1) {
            throw new LoadTesterException("No requests were specified for the load test.");
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

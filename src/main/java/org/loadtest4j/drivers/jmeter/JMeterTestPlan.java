package org.loadtest4j.drivers.jmeter;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

class JMeterTestPlan {

    private final String domain;

    private final int numThreads;

    private final int port;

    private final String protocol;

    private final int rampUp;

    JMeterTestPlan(String domain, int numThreads, int port, String protocol, int rampUp) {
        this.domain = domain;
        this.numThreads = numThreads;
        this.port = port;
        this.protocol = protocol;
        this.rampUp = rampUp;
    }

    File create(List<DriverRequest> driverRequests) {
        final HashTree hashTree = createHashTree(driverRequests);

        final File jmxFile;
        try {
            jmxFile = File.createTempFile("example", ".jmx");
            final FileOutputStream os = new FileOutputStream(jmxFile);
            SaveService.saveTree(hashTree, os);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
        return jmxFile;
    }

    private HashTree createHashTree(List<DriverRequest> driverRequests) {
        final List<HTTPSamplerProxy> httpSamplers = createRequests(driverRequests);

        final LoopController loopController = new LoopController();
        loopController.setName("Loadtest4j Loop Controller");
        loopController.setLoops(1);
        loopController.setFirst(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        final ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Loadtest4j Thread Group");
        threadGroup.setNumThreads(numThreads);
        threadGroup.setRampUp(rampUp);
        threadGroup.setSamplerController(loopController);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        final TestPlan testPlan = new TestPlan("Loadtest4j Test Plan");
        testPlan.addThreadGroup(threadGroup);
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());

        // FIXME test technically runs but GUI error on viewing the TestPlan entry - construct them in the right GUI order
        final HashTree testPlanTree = new HashTree();
        testPlanTree.add(testPlan);

        final HashTree threadGroupTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupTree.add(loopController);

        httpSamplers.forEach(threadGroupTree::add);

        return testPlanTree;
    }

    private List<HTTPSamplerProxy> createRequests(List<DriverRequest> driverRequests) {
        return driverRequests.stream()
                .map(this::createRequest)
                .collect(Collectors.toList());
    }

    private HTTPSamplerProxy createRequest(DriverRequest driverRequest) {
        final HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setName(driverRequest.getMethod() + " " + driverRequest.getPath());
        httpSampler.setProtocol(protocol);
        httpSampler.setDomain(domain);
        httpSampler.setPort(port);
        httpSampler.setPath(driverRequest.getPath());
        httpSampler.setMethod(driverRequest.getMethod());
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        return httpSampler;
    }
}

package org.loadtest4j.drivers.jmeter.engine;

import java.io.File;

public interface Engine {
    /**
     * Run jmeter 'somehow'.
     *
     * @param testPlan The JMX file containing the test plan
     * @return The JTL result file
     */
    File runJmeter(File testPlan);
}

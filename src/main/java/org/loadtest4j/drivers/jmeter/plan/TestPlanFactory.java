package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.driver.DriverRequest;

import java.io.File;
import java.util.List;

public interface TestPlanFactory {
    /**
     * Create a JMeter test plan 'somehow'.
     *
     * @param driverRequests the loadtest4j scenario to build the test plan from
     * @return the jmx test plan file
     */
    File create(List<DriverRequest> driverRequests);
}

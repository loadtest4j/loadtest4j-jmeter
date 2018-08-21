package org.loadtest4j.drivers.jmeter;

import java.io.File;

public interface Runner {
    /**
     * Run jmeter 'somehow'.
     *
     * @param jmxFile The JMX file containing configuration
     * @return The JTL result file
     */
    File runJmeter(File jmxFile);
}

package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;

import java.io.File;
import java.time.Duration;

class JMeterResultParser {
    JMeterResult parse(File result) {
        // FIXME use SaveService.loadTestResults(resultFileReader, new ResultCollectorHelper(...));

        final long ok = 0;
        final long ko = 0;
        final Duration actualDuration = Duration.ZERO;
        final String reportUrl = result.getAbsolutePath();
        final DriverResponseTime responseTime = new JMeterResponseTime();
        return new JMeterResult(actualDuration, ok, ko, reportUrl, responseTime);
    }
}

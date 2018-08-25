package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;

import java.time.Duration;

public class JMeterResponseTime implements DriverResponseTime {
    @Override
    public Duration getPercentile(int percentile) {
        // FIXME do the real thing
        throw new UnsupportedOperationException("The JMeter driver does not yet provide response time data.");
    }
}

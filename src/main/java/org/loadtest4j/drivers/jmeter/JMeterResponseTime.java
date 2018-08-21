package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;

import java.time.Duration;

class JMeterResponseTime implements DriverResponseTime {
    @Override
    public Duration getPercentile(int percentile) {
        // FIXME do the real thing
        return Duration.ZERO;
    }
}

package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.util.Histogram;

import java.time.Duration;

public class JMeterResponseTime implements DriverResponseTime {

    private final Histogram histogram;

    public JMeterResponseTime(Histogram histogram) {
        this.histogram = histogram;
    }

    @Override
    public Duration getPercentile(double percentile) {
        final long durationMillis = histogram.getValue(percentile);
        return Duration.ofMillis(durationMillis);
    }
}

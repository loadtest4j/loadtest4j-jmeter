package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.drivers.jmeter.util.Histogram;

import java.time.Duration;

public class JMeterApdex {

    private final Histogram okHistogram;

    public JMeterApdex(Histogram okHistogram) {
        this.okHistogram = okHistogram;
    }

    public long getOkRequestsBetween(Duration min, Duration max) {
        final long minMillis = min.toMillis();
        final long maxMillis = max.toMillis();
        return okHistogram.getCountBetweenValues(minMillis, maxMillis);
    }
}

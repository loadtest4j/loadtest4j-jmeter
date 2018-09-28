package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;

import java.time.Duration;

public class JMeterResult implements DriverResult {

    private final Duration actualDuration;

    private final long ok;

    private final long ko;

    private final DriverResponseTime responseTime;

    public JMeterResult(Duration actualDuration, long ok, long ko, DriverResponseTime responseTime) {
        this.actualDuration = actualDuration;
        this.ok = ok;
        this.ko = ko;
        this.responseTime = responseTime;
    }

    @Override
    public long getOk() {
        return ok;
    }

    @Override
    public long getKo() {
        return ko;
    }

    @Override
    public Duration getActualDuration() {
        return actualDuration;
    }

    @Override
    public DriverResponseTime getResponseTime() {
        return responseTime;
    }
}

package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.driver.DriverResult;

import java.time.Duration;
import java.util.Optional;

class JMeterResult implements DriverResult {

    private final Duration actualDuration;

    private final long ok;

    private final long ko;

    private final String reportUrl;

    private final DriverResponseTime responseTime;

    JMeterResult(Duration actualDuration, long ok, long ko, String reportUrl, DriverResponseTime responseTime) {
        this.actualDuration = actualDuration;
        this.ok = ok;
        this.ko = ko;
        this.reportUrl = reportUrl;
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

    @Override
    public Optional<String> getReportUrl() {
        return Optional.of(reportUrl);
    }
}

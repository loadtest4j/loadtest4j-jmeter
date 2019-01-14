package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.Driver;

public class JMeterBuilder {

    private final String domain;

    private final int numThreads;

    private final int port;

    private final String protocol;

    private final int rampUp;

    private JMeterBuilder(String domain, int numThreads, int port, String protocol, int rampUp) {
        this.domain = domain;
        this.numThreads = numThreads;
        this.port = port;
        this.protocol = protocol;
        this.rampUp = rampUp;
    }

    public static JMeterBuilder withUrl(String protocol, String domain, int port) {
        return new JMeterBuilder(domain, 1, port, protocol, 1);
    }

    public JMeterBuilder withNumThreads(int numThreads) {
        return new JMeterBuilder(domain, numThreads, port, protocol, rampUp);
    }

    public JMeterBuilder withRampUp(int rampUp) {
        return new JMeterBuilder(domain, numThreads, port, protocol, rampUp);
    }

    protected Driver buildDriver() {
        return new JMeter(domain, numThreads, port, protocol, rampUp);
    }
}
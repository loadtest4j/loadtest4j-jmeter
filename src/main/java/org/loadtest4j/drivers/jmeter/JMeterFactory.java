package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class JMeterFactory implements DriverFactory {
    @Override
    public Set<String> getMandatoryProperties() {
        return setOf("domain", "numThreads", "port", "protocol", "rampUp");
    }

    @Override
    public Driver create(Map<String, String> properties) {
        final String domain = properties.get("domain");

        final int numThreads = Integer.valueOf(properties.get("numThreads"));

        final int port = Integer.valueOf(properties.get("port"));

        final String protocol = properties.get("protocol");

        final int rampUp = Integer.valueOf(properties.get("rampUp"));

        return new JMeter(domain, numThreads, port, protocol, rampUp);
    }

    private static Set<String> setOf(String... values) {
        // This utility method can be replaced when Java 9+ is more widely adopted
        final Set<String> internalSet = new LinkedHashSet<>(Arrays.asList(values));
        return Collections.unmodifiableSet(internalSet);
    }
}

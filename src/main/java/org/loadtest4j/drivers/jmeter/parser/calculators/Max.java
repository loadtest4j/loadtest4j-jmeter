package org.loadtest4j.drivers.jmeter.parser.calculators;

/**
 * Gets the maximum of all whole numbers it sees. Default 0.
 */
public class Max implements Calculator<Long> {

    private long max = 0;

    @Override
    public synchronized void add(Long value) {
        if (max == 0) {
            max = value;
        }
        max = Math.max(value, max);
    }

    public long calculate() {
        return max;
    }
}

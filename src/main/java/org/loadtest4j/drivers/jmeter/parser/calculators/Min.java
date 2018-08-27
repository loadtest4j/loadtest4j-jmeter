package org.loadtest4j.drivers.jmeter.parser.calculators;

/**
 * Gets the minimum of all whole numbers it sees. Default 0.
 */
public class Min implements Calculator<Long> {

    private long min = 0;

    @Override
    public synchronized void add(Long value) {
        if (min == 0) {
            min = value;
        }
        min = Math.min(value, min);
    }

    public long calculate() {
        return min;
    }
}

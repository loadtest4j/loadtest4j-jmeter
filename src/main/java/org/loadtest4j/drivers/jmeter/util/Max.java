package org.loadtest4j.drivers.jmeter.util;

import java.util.function.Consumer;

/**
 * Gets the maximum of all whole numbers it sees. Default 0.
 */
public class Max implements Consumer<Long> {

    private long max = 0;

    @Override
    public synchronized void accept(Long value) {
        if (max == 0) {
            max = value;
        }
        max = Math.max(value, max);
    }

    public long calculate() {
        return max;
    }
}

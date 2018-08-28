package org.loadtest4j.drivers.jmeter.util;

import java.util.function.Consumer;

/**
 * Gets the minimum of all whole numbers it sees. Default 0.
 */
public class Min implements Consumer<Long> {

    private long min = 0;

    @Override
    public synchronized void accept(Long value) {
        if (min == 0) {
            min = value;
        }
        min = Math.min(value, min);
    }

    public long calculate() {
        return min;
    }
}

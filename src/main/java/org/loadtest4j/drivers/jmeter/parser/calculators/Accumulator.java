package org.loadtest4j.drivers.jmeter.parser.calculators;

public class Accumulator implements Calculator<Long> {

    private long acc = 0;

    @Override
    public synchronized void add(Long value) {
        acc += value;
    }

    public long calculate() {
        return acc;
    }
}

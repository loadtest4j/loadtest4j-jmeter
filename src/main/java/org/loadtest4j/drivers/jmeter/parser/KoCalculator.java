package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.drivers.jmeter.parser.calculators.Accumulator;
import org.loadtest4j.drivers.jmeter.parser.calculators.Calculator;

class KoCalculator implements Calculator<Sample> {

    private final Accumulator accumulator = new Accumulator();

    @Override
    public void add(Sample sample) {
        final boolean success = Boolean.parseBoolean(sample.getSuccess());

        if (!success) {
            accumulator.add(1L);
        }
    }

    public Long calculate() {
        return accumulator.calculate();
    }
}

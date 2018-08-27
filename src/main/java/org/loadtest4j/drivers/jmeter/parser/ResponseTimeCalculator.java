package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.drivers.jmeter.parser.calculators.Calculator;

class ResponseTimeCalculator implements Calculator<Sample> {

    private final Histogram responseTimes = Histogram.standard();

    @Override
    public void add(Sample sample) {
        final long elapsed = Long.parseLong(sample.getElapsed());
        responseTimes.add(elapsed);
    }

    public Histogram calculate() {
        return responseTimes;
    }
}

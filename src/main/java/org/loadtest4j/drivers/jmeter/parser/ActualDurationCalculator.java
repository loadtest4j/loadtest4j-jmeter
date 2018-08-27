package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.drivers.jmeter.parser.calculators.Calculator;
import org.loadtest4j.drivers.jmeter.parser.calculators.Max;
import org.loadtest4j.drivers.jmeter.parser.calculators.Min;

import java.time.Duration;

class ActualDurationCalculator implements Calculator<Sample> {

    private final Min start = new Min();

    private final Max end = new Max();

    @Override
    public synchronized void add(Sample sample) {
        final long sampleStart = Long.parseLong(sample.getTimeStamp());
        final long sampleEnd = sampleStart + Long.parseLong(sample.getElapsed());

        start.add(sampleStart);
        end.add(sampleEnd);
    }

    public Duration calculate() {
        final long start = this.start.calculate();
        final long end = this.end.calculate();

        return Duration.ofMillis(end - start);
    }
}

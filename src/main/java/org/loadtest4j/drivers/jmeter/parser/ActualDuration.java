package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.drivers.jmeter.util.Max;
import org.loadtest4j.drivers.jmeter.util.Min;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ActualDuration implements Consumer<Map<String, String>>, Supplier<Duration> {

    private final Min start = new Min();

    private final Max end = new Max();

    @Override
    public synchronized void accept(Map<String, String> sample) {
        final long sampleStart = Long.parseLong(sample.get("timeStamp"));
        final long sampleEnd = sampleStart + Long.parseLong(sample.get("elapsed"));

        start.accept(sampleStart);
        end.accept(sampleEnd);
    }

    @Override
    public Duration get() {
        final long start = this.start.calculate();
        final long end = this.end.calculate();

        return Duration.ofMillis(end - start);
    }
}

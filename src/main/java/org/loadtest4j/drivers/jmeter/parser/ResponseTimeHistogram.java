package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.drivers.jmeter.util.Histogram;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

class ResponseTimeHistogram implements Consumer<Map<String, String>>, Supplier<Histogram> {

    private final Histogram responseTimes = Histogram.standard();

    @Override
    public void accept(Map<String, String> sample) {
        final long elapsed = Long.parseLong(sample.get("elapsed"));
        responseTimes.add(elapsed);
    }

    @Override
    public Histogram get() {
        return responseTimes;
    }
}

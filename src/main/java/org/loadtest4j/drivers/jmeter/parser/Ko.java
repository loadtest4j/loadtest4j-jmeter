package org.loadtest4j.drivers.jmeter.parser;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;

class Ko implements Consumer<Map<String, String>>, Supplier<Long> {

    private final AtomicLong accumulator = new AtomicLong();

    @Override
    public void accept(Map<String, String> sample) {
        final boolean success = Boolean.parseBoolean(sample.get("success"));

        if (!success) {
            accumulator.incrementAndGet();
        }
    }

    @Override
    public Long get() {
        return accumulator.get();
    }
}

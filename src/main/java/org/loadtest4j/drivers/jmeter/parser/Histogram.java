package org.loadtest4j.drivers.jmeter.parser;

import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.ConcurrentHistogram;

public class Histogram {

    private final AbstractHistogram histogram;

    private Histogram(AbstractHistogram histogram) {
        this.histogram = histogram;
    }

    public static Histogram standard() {
        // FIXME make num significant digits user-configurable
        final AbstractHistogram underlying = new ConcurrentHistogram(5);
        return new Histogram(underlying);
    }

    public void add(long value) {
        histogram.recordValue(value);
    }

    public long getValue(double percentile) {
        return histogram.getValueAtPercentile(percentile);
    }
}

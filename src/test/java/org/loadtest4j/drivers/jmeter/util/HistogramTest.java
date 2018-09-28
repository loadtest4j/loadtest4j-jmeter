package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class HistogramTest {
    @Test
    public void testGetValue() {
        final Histogram histogram = Histogram.of(1, 2, 3);

        final long max = histogram.getValue(100);

        assertThat(max).isEqualTo(3L);
    }

    @Test
    public void testGetValueWithNoSamples() {
        final Histogram histogram = Histogram.standard();

        final long max = histogram.getValue(100);

        assertThat(max).isEqualTo(0L);
    }

    @Test
    public void testGetTooHighPercentile() {
        final Histogram histogram = Histogram.of(1, 2, 3);

        final long time = histogram.getValue(101);

        assertThat(time).isEqualTo(3L);
    }

    @Test
    public void testGetTooLowPercentile() {
        final Histogram histogram = Histogram.of(1, 2, 3);

        final long time = histogram.getValue(-1);

        assertThat(time).isEqualTo(1L);
    }
}

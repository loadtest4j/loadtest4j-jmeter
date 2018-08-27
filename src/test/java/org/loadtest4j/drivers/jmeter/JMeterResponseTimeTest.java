package org.loadtest4j.drivers.jmeter;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;
import org.loadtest4j.drivers.jmeter.parser.Histogram;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterResponseTimeTest {

    @Test
    public void testGetPercentile() {
        final Histogram histogram = histogramOf(1, 2, 3);
        final DriverResponseTime responseTime = new JMeterResponseTime(histogram);

        final Duration maxResponseTime = responseTime.getPercentile(100);

        assertThat(maxResponseTime).isEqualTo(Duration.ofMillis(3));
    }

    @Test
    public void testGetPercentileWithNoSamples() {
        final DriverResponseTime responseTime = new JMeterResponseTime(Histogram.standard());

        final Duration maxResponseTime = responseTime.getPercentile(100);

        assertThat(maxResponseTime).isEqualTo(Duration.ZERO);
    }

    private static Histogram histogramOf(long... values) {
        final Histogram histogram = Histogram.standard();
        for (long value: values) {
            histogram.add(value);
        }
        return histogram;
    }
}

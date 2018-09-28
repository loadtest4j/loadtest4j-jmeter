package org.loadtest4j.drivers.jmeter;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;
import org.loadtest4j.drivers.jmeter.util.Histogram;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterResponseTimeTest {

    @Test
    public void testGetPercentile() {
        final Histogram histogram = Histogram.of(1, 2, 3);
        final DriverResponseTime responseTime = new JMeterResponseTime(histogram);

        final Duration maxResponseTime = responseTime.getPercentile(100);

        assertThat(maxResponseTime).isEqualTo(Duration.ofMillis(3));
    }

    @Test
    public void testGetDecimalPercentile() {
        final Histogram histogram = Histogram.of(1, 2, 3);
        final DriverResponseTime responseTime = new JMeterResponseTime(histogram);

        assertThat(responseTime.getPercentile(50.511)).isEqualTo(Duration.ofMillis(2));
    }

    @Test
    public void testGetPercentileWithNoSamples() {
        final DriverResponseTime responseTime = new JMeterResponseTime(Histogram.standard());

        final Duration maxResponseTime = responseTime.getPercentile(100);

        assertThat(maxResponseTime).isEqualTo(Duration.ZERO);
    }
}

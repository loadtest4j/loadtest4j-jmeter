package org.loadtest4j.drivers.jmeter;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterBuilderTest {

    @Test
    public void shouldHaveDefaultValues() {
        final JMeter jmeter = (JMeter) JMeterBuilder.withUrl("https", "example.com", 443).build();

        assertThat(jmeter.domain).isEqualTo("example.com");
        assertThat(jmeter.numThreads).isEqualTo(1);
        assertThat(jmeter.port).isEqualTo(443);
        assertThat(jmeter.protocol).isEqualTo("https");
        assertThat(jmeter.rampUp).isEqualTo(1);
    }

    @Test
    public void shouldSetCustomValues() {
        final JMeter jmeter = (JMeter) JMeterBuilder.withUrl("https", "example.com", 443)
                .withNumThreads(2)
                .withRampUp(2)
                .build();

        assertThat(jmeter.numThreads).isEqualTo(2);
        assertThat(jmeter.rampUp).isEqualTo(2);
    }

    @Test
    public void shouldBeImmutable() {
        final JMeterBuilder builder = JMeterBuilder.withUrl("https", "example.com", 443);

        final Driver before = builder.build();

        builder.withNumThreads(2);
        builder.withRampUp(2);

        final Driver after = builder.build();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}

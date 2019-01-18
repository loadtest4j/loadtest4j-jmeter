package org.loadtest4j.drivers.jmeter;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterBuilderTest {

    private final JMeterBuilder builder = JMeterBuilder.withUrl("https", "example.com", 443);

    @Test
    public void shouldRequireProtocol() {
        final JMeter jmeter = builder.buildDriver();

        assertThat(jmeter.protocol).isEqualTo("https");
    }

    @Test
    public void shouldRequireDomain() {
        final JMeter jmeter = builder.buildDriver();

        assertThat(jmeter.domain).isEqualTo("example.com");
    }

    @Test
    public void shouldRequirePort() {
        final JMeter jmeter = builder.buildDriver();

        assertThat(jmeter.port).isEqualTo(443);
    }

    @Test
    public void shouldSetNumThreads() {
        final JMeter jmeter = builder
                .withNumThreads(2)
                .buildDriver();

        assertThat(jmeter.numThreads).isEqualTo(2);
    }

    @Test
    public void shouldSetNumThreadsTo1ByDefault() {
        final JMeter jmeter = builder.buildDriver();

        assertThat(jmeter.numThreads).isEqualTo(1);
    }

    @Test
    public void shouldSetRampUp() {
        final JMeter jmeter = builder
                .withRampUp(2)
                .buildDriver();

        assertThat(jmeter.rampUp).isEqualTo(2);
    }

    @Test
    public void shouldSetRampUpTo1SecondByDefault() {
        final JMeter jmeter = builder.buildDriver();

        assertThat(jmeter.rampUp).isEqualTo(1);
    }

    @Test
    public void shouldBeImmutable() {
        final Driver before = builder.buildDriver();

        builder.withNumThreads(2);
        builder.withRampUp(2);

        final Driver after = builder.buildDriver();

        assertThat(after).isEqualToComparingFieldByField(before);
    }
}

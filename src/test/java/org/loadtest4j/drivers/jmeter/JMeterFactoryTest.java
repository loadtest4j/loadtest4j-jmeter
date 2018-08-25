package org.loadtest4j.drivers.jmeter;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverFactory;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterFactoryTest {

    private DriverFactory sut() {
        return new JMeterFactory();
    }

    @Test
    public void testGetMandatoryProperties() {
        final DriverFactory sut = sut();

        final Set<String> mandatoryProperties = sut.getMandatoryProperties();

        assertThat(mandatoryProperties).containsExactly("domain", "numThreads", "port", "protocol", "rampUp");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMandatoryPropertiesIsImmutable() {
        final DriverFactory sut = sut();

        sut.getMandatoryProperties().add("foobarbaz123");
    }

    @Test
    public void testCreate() {
        final DriverFactory sut = sut();

        final Map<String, String> properties = new HashMap<>();
        properties.put("domain", "example.com");
        properties.put("numThreads", "2");
        properties.put("port", "443");
        properties.put("protocol", "https");
        properties.put("rampUp", "10");

        final Driver driver = sut.create(properties);

        assertThat(driver).isInstanceOf(JMeter.class);
    }
}

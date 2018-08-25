package org.loadtest4j.drivers.jmeter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

@Category(UnitTest.class)
public class JMeterResponseTimeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetPercentile() {
        final DriverResponseTime responseTime = new JMeterResponseTime();

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("The JMeter driver does not yet provide response time data.");

        responseTime.getPercentile(100);
    }
}

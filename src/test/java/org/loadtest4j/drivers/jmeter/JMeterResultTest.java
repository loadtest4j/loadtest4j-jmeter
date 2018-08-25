package org.loadtest4j.drivers.jmeter;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterResultTest {

    private static final long OK = 1;
    private static final long KO = 2;
    private static final Duration DURATION = Duration.ofSeconds(1);
    private static final String REPORT_URL = "file:/tmp/loadtest4j.jtl";
    private static final Duration RESPONSE_TIME = Duration.ofMillis(100);

    private JMeterResult result;

    @Before
    public void setUp() {
        final DriverResponseTime fn = i -> RESPONSE_TIME;
        result = new JMeterResult(DURATION, OK, KO, REPORT_URL, fn);
    }

    @Test
    public void testGetActualDuration() {
        assertThat(result.getActualDuration())
                .isEqualTo(DURATION);
    }

    @Test
    public void testGetOk() {
        assertThat(result.getOk())
                .isEqualTo(OK);
    }

    @Test
    public void testGetKo() {
        assertThat(result.getKo())
                .isEqualTo(KO);
    }

    @Test
    public void testGetReportUrl() {
        assertThat(result.getReportUrl())
                .isPresent()
                .contains(REPORT_URL);
    }

    @Test
    public void testGetResponseTime() {
        assertThat(result.getResponseTime().getPercentile(100))
                .isEqualTo(RESPONSE_TIME);
    }
}

package org.loadtest4j.drivers.jmeter.plan;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.drivers.jmeter.DriverRequests;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;
import org.loadtest4j.drivers.jmeter.junit.XmlValidator;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class BlackBoxTestPlanFactoryTest {
    @Test
    public void testFixHeaders() {
        final Map<String, String> input = Collections.singletonMap("foo", "bar");

        assertThat(BlackBoxTestPlanFactory.fixHeaders(input))
                .containsEntry("foo", "bar")
                .containsEntry("Content-Type", "text/plain");
    }

    @Test
    public void testFixHeadersPassthrough() {
        final Map<String, String> input = Collections.singletonMap("Content-Type", "application/json");

        assertThat(BlackBoxTestPlanFactory.fixHeaders(input))
                .isEqualTo(input);
    }

    @Test
    public void testFixHeadersPassthroughCaseInsensitive() {
        final Map<String, String> input = Collections.singletonMap("CoNtENt-TYPe", "application/json");

        assertThat(BlackBoxTestPlanFactory.fixHeaders(input))
                .isEqualTo(input);
    }

    @Test
    public void testThatPlanIsValidXml() throws Exception {
        final BlackBoxTestPlanFactory factory = new BlackBoxTestPlanFactory("example.com", 1, 443, "https", 1);

        final List<DriverRequest> driverRequests = Collections.singletonList(DriverRequests.getWithBodyAndHeaders("/", "foo", Collections.singletonMap("foo", "bar")));

        try (StringWriter writer = new StringWriter()) {
            factory.create(driverRequests, writer);
            XmlValidator.validateXml(writer.toString());
        }
    }


}

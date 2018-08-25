package org.loadtest4j.drivers.jmeter.plan;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

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
}

package org.loadtest4j.drivers.jmeter.parser;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class ActualDurationTest {

    private static final Map<String, String> A = new LinkedHashMap<String, String>() {{
        put("timeStamp", "1000000000000");
        put("elapsed", "1000");
    }};

    private static final Map<String, String> B = new LinkedHashMap<String, String>() {{
        put("timeStamp", "1000000002000");
        put("elapsed", "2000");
    }};

    @Test
    public void testEmpty() {
        final ActualDuration actualDuration = new ActualDuration();

        assertThat(actualDuration.get()).isEqualTo(Duration.ZERO);
    }

    @Test
    public void testAddSingle() {
        final ActualDuration actualDuration = new ActualDuration();

        actualDuration.accept(A);

        assertThat(actualDuration.get()).isEqualTo(Duration.ofSeconds(1));
    }

    @Test
    public void testAddMultiple() {
        final ActualDuration actualDuration = new ActualDuration();

        actualDuration.accept(A);
        actualDuration.accept(B);

        assertThat(actualDuration.get()).isEqualTo(Duration.ofSeconds(4));
    }
}

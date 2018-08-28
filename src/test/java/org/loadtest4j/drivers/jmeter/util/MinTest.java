package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class MinTest {
    @Test
    public void testEmpty() {
        final Min min = new Min();

        assertThat(min.calculate()).isEqualTo(0);
    }

    @Test
    public void testPositive() {
        final Min min = new Min();

        min.accept(1L);

        assertThat(min.calculate()).isEqualTo(1);
    }

    @Test
    public void testPositiveSeries() {
        final Min min = new Min();

        min.accept(1L);
        min.accept(2L);

        assertThat(min.calculate()).isEqualTo(1);
    }

    @Test
    public void testNegative() {
        final Min min = new Min();

        min.accept(-1L);

        assertThat(min.calculate()).isEqualTo(-1);
    }

    @Test
    public void testNegativeSeries() {
        final Min min = new Min();

        min.accept(-1L);
        min.accept(-2L);

        assertThat(min.calculate()).isEqualTo(-2);
    }
}

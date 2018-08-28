package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class MaxTest {
    @Test
    public void testEmpty() {
        final Max max = new Max();

        assertThat(max.calculate()).isEqualTo(0);
    }

    @Test
    public void testPositive() {
        final Max max = new Max();

        max.accept(1L);

        assertThat(max.calculate()).isEqualTo(1);
    }

    @Test
    public void testPositiveSeries() {
        final Max max = new Max();

        max.accept(1L);
        max.accept(2L);

        assertThat(max.calculate()).isEqualTo(2);
    }

    @Test
    public void testNegative() {
        final Max max = new Max();

        max.accept(-1L);

        assertThat(max.calculate()).isEqualTo(-1);
    }

    @Test
    public void testNegativeSeries() {
        final Max max = new Max();

        max.accept(-1L);
        max.accept(-2L);

        assertThat(max.calculate()).isEqualTo(-1);
    }
}

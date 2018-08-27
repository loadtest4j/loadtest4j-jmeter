package org.loadtest4j.drivers.jmeter.parser.calculators;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class AccumulatorTest {
    @Test
    public void testEmpty() {
        final Accumulator acc = new Accumulator();

        assertThat(acc.calculate()).isEqualTo(0);
    }

    @Test
    public void testAdd() {
        final Accumulator acc = new Accumulator();

        acc.add(1L);

        assertThat(acc.calculate()).isEqualTo(1);
    }

    @Test
    public void testAddMultiple() {
        final Accumulator acc = new Accumulator();

        acc.add(1L);
        acc.add(2L);

        assertThat(acc.calculate()).isEqualTo(3);
    }
}

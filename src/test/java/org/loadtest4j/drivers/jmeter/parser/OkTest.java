package org.loadtest4j.drivers.jmeter.parser;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class OkTest {
    @Test
    public void testEmpty() {
        final Ok ok = new Ok();

        assertThat(ok.get()).isEqualTo(0);
    }

    @Test
    public void testAddSuccess() {
        final Ok ok = new Ok();

        ok.accept(Collections.singletonMap("success", "true"));

        assertThat(ok.get()).isEqualTo(1);
    }

    @Test
    public void testAddFailure() {
        final Ok ok = new Ok();

        ok.accept(Collections.singletonMap("success", "false"));

        assertThat(ok.get()).isEqualTo(0);
    }

    @Test
    public void testAddMultiple() {
        final Ok ok = new Ok();

        ok.accept(Collections.singletonMap("success", "true"));
        ok.accept(Collections.singletonMap("success", "true"));

        assertThat(ok.get()).isEqualTo(2);
    }
}

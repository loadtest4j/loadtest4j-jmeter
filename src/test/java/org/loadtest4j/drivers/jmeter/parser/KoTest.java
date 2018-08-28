package org.loadtest4j.drivers.jmeter.parser;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class KoTest {
    @Test
    public void testEmpty() {
        final Ko ko = new Ko();

        assertThat(ko.get()).isEqualTo(0);
    }

    @Test
    public void testAddSuccess() {
        final Ko ko = new Ko();

        ko.accept(Collections.singletonMap("success", "true"));

        assertThat(ko.get()).isEqualTo(0);
    }

    @Test
    public void testAddFailure() {
        final Ko ko = new Ko();

        ko.accept(Collections.singletonMap("success", "false"));

        assertThat(ko.get()).isEqualTo(1);
    }

    @Test
    public void testAddMultiple() {
        final Ko ko = new Ko();

        ko.accept(Collections.singletonMap("success", "false"));
        ko.accept(Collections.singletonMap("success", "false"));

        assertThat(ko.get()).isEqualTo(2);
    }
}

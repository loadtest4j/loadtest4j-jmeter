package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class ConsumersTest {
    @Test
    public void testSingleConsumer() {
        final StubConsumer stubConsumer = new StubConsumer();
        final Consumer<Integer> sut = Consumers.compose(stubConsumer);

        sut.accept(1);
        sut.accept(2);

        assertThat(stubConsumer.calls).containsExactly(1, 2);
    }

    @Test
    public void testMultipleConsumers() {
        final StubConsumer c1 = new StubConsumer();
        final StubConsumer c2 = new StubConsumer();
        final Consumer<Integer> sut = Consumers.compose(c1, c2);

        sut.accept(1);
        sut.accept(2);

        assertThat(c1.calls).containsExactly(1, 2);
        assertThat(c2.calls).containsExactly(1, 2);
    }

    private static class StubConsumer implements Consumer<Integer> {

        private final List<Integer> calls = new ArrayList<>();

        @Override
        public void accept(Integer integer) {
            calls.add(integer);
        }
    }
}

package org.loadtest4j.drivers.jmeter.util;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class Consumers {
    private Consumers() {}

    public static <T> Consumer<T> compose(Consumer<T>... consumers) {
        return new ConsumerChain<>(Arrays.asList(consumers));
    }

    private static class ConsumerChain<T> implements Consumer<T> {

        private final List<Consumer<T>> consumers;

        private ConsumerChain(List<Consumer<T>> consumers) {
            this.consumers = consumers;
        }

        @Override
        public void accept(T t) {
            consumers.forEach(c -> c.accept(t));
        }
    }
}

package org.loadtest4j.drivers.jmeter.parser;

import jdk.nashorn.api.scripting.URLReader;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResult;

import java.net.URL;
import java.time.Duration;
import java.util.Map;

public class Parser {
    public JMeterResult parse(URL results) {
        final String reportUrl = results.toString();

        final CombiResult result = CsvStream.stream(new URLReader(results))
                .map(Parser::toCombiResult)
                .reduce(Parser::reducer)
                .orElse(CombiResult.ZERO);

        final long ok = result.ok;

        final long ko = result.ko;

        final long startTime = result.startTime;
        final long endTime = result.endTime;
        final Duration actualDuration = Duration.ofMillis(endTime - startTime);

        // FIXME generate this
        final DriverResponseTime responseTime = new JMeterResponseTime();

        return new JMeterResult(actualDuration, ok, ko, reportUrl, responseTime);
    }

    private static CombiResult reducer(CombiResult acc, CombiResult newResult) {
        final long ok = acc.ok + newResult.ok;
        final long ko = acc.ko + newResult.ko;
        final long startTime = Math.min(acc.startTime, newResult.startTime);
        final long endTime = Math.max(acc.endTime, newResult.endTime);
        return new CombiResult(ok, ko, startTime, endTime);
    }

    private static CombiResult toCombiResult(Map<String, String> sample) {
        final boolean success = Boolean.valueOf(sample.get("success"));

        final long ok = success ? 1 : 0;
        final long ko = success ? 0 : 1;
        final long startTime = Long.valueOf(sample.get("timeStamp"));
        final long endTime = startTime + Long.valueOf(sample.get("elapsed"));
        return new CombiResult(ok, ko, startTime, endTime);
    }

    private static class CombiResult {
        private final long ok;
        private final long ko;
        private final long startTime;
        private final long endTime;

        private CombiResult(long ok, long ko, long startTime, long endTime) {
            this.ok = ok;
            this.ko = ko;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        private static CombiResult ZERO = new CombiResult(0, 0, 0, 0);
    }
}

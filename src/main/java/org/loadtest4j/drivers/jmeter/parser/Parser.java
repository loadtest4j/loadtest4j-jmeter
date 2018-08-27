package org.loadtest4j.drivers.jmeter.parser;

import jdk.nashorn.api.scripting.URLReader;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResult;
import org.loadtest4j.drivers.jmeter.parser.calculators.Accumulator;
import org.loadtest4j.drivers.jmeter.parser.calculators.Calculator;
import org.loadtest4j.drivers.jmeter.parser.calculators.Max;
import org.loadtest4j.drivers.jmeter.parser.calculators.Min;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class Parser {
    public JMeterResult parse(URL results) {
        final String reportUrl = results.toString();

        final ActualDurationCalculator actualDurationCalculator = new ActualDurationCalculator();
        final OkCalculator okCalculator = new OkCalculator();
        final KoCalculator koCalculator = new KoCalculator();
        final ResponseTimeCalculator responseTimeCalculator = new ResponseTimeCalculator();

        final List<Calculator<Sample>> sampleCalculators = Arrays.asList(actualDurationCalculator, okCalculator, koCalculator, responseTimeCalculator);

        read(results, sampleCalculators);

        final long ok = okCalculator.calculate();

        final long ko = koCalculator.calculate();

        final Duration actualDuration = actualDurationCalculator.calculate();

        final Histogram histogram = responseTimeCalculator.calculate();
        final DriverResponseTime responseTime = new JMeterResponseTime(histogram);

        return new JMeterResult(actualDuration, ok, ko, reportUrl, responseTime);
    }

    private static void read(URL results, List<Calculator<Sample>> sampleCalculators) {
        try (ICsvBeanReader csvReader = new CsvBeanReader(new URLReader(results), CsvPreference.STANDARD_PREFERENCE)) {
            final String[] header = csvReader.getHeader(true);

            Sample sample;
            while ((sample = csvReader.read(Sample.class, header)) != null) {
                for (Calculator<Sample> calc: sampleCalculators) {
                    calc.add(sample);
                }
            }
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static class ActualDurationCalculator implements Calculator<Sample> {

        private final Min start = new Min();

        private final Max end = new Max();

        @Override
        public synchronized void add(Sample sample) {
            final long sampleStart = Long.parseLong(sample.getTimeStamp());
            final long sampleEnd = sampleStart + Long.parseLong(sample.getElapsed());

            start.add(sampleStart);
            end.add(sampleEnd);
        }

        Duration calculate() {
            final long start = this.start.calculate();
            final long end = this.end.calculate();

            return Duration.ofMillis(end - start);
        }
    }

    private static class KoCalculator implements Calculator<Sample> {

        private final Accumulator accumulator = new Accumulator();

        @Override
        public void add(Sample sample) {
            final boolean success = Boolean.parseBoolean(sample.getSuccess());

            if (!success) {
                accumulator.add(1L);
            }
        }

        Long calculate() {
            return accumulator.calculate();
        }
    }

    private static class OkCalculator implements Calculator<Sample> {

        private final Accumulator accumulator = new Accumulator();

        @Override
        public void add(Sample sample) {
            final boolean success = Boolean.parseBoolean(sample.getSuccess());

            if (success) {
                accumulator.add(1L);
            }
        }

        Long calculate() {
            return accumulator.calculate();
        }
    }

    private static class ResponseTimeCalculator implements Calculator<Sample> {

        private final Histogram responseTimes = Histogram.standard();

        @Override
        public void add(Sample sample) {
            final long elapsed = Long.parseLong(sample.getElapsed());
            responseTimes.add(elapsed);
        }

        Histogram calculate() {
            return responseTimes;
        }
    }
}

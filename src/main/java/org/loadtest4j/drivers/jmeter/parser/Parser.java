package org.loadtest4j.drivers.jmeter.parser;

import jdk.nashorn.api.scripting.URLReader;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResult;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;

public class Parser {
    public JMeterResult parse(URL results) {
        final String reportUrl = results.toString();

        final ActualDurationCalculator actualDurationCalculator = new ActualDurationCalculator();
        final OkCalculator okCalculator = new OkCalculator();
        final KoCalculator koCalculator = new KoCalculator();
        final ResponseTimeCalculator responseTimeCalculator = new ResponseTimeCalculator();

        try (ICsvBeanReader csvReader = new CsvBeanReader(new URLReader(results), CsvPreference.STANDARD_PREFERENCE)) {
            final String[] header = csvReader.getHeader(true);

            Sample sample;
            while ((sample = csvReader.read(Sample.class, header)) != null) {
                actualDurationCalculator.add(sample);
                okCalculator.add(sample);
                koCalculator.add(sample);
                responseTimeCalculator.add(sample);
            }
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }

        final long ok = okCalculator.calculate();

        final long ko = koCalculator.calculate();

        final Duration actualDuration = actualDurationCalculator.calculate();

        final Histogram histogram = responseTimeCalculator.calculate();
        final DriverResponseTime responseTime = new JMeterResponseTime(histogram);

        return new JMeterResult(actualDuration, ok, ko, reportUrl, responseTime);
    }
}

package org.loadtest4j.drivers.jmeter.parser;

import org.loadtest4j.driver.DriverResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResponseTime;
import org.loadtest4j.drivers.jmeter.JMeterResult;
import org.loadtest4j.drivers.jmeter.util.*;

import java.net.URL;
import java.util.Map;
import java.util.function.Consumer;

public class Parser {
    public JMeterResult parse(URL results) {
        final ActualDuration actualDuration = new ActualDuration();
        final Ok ok = new Ok();
        final Ko ko = new Ko();
        final ResponseTimeHistogram responseTimeHistogram = new ResponseTimeHistogram();

        final Consumer<Map<String, String>> sampleConsumer = Consumers.compose(actualDuration, ok, ko, responseTimeHistogram);

        new CsvReader(results).forEach(sampleConsumer);

        final DriverResponseTime responseTime = new JMeterResponseTime(responseTimeHistogram.get());

        return new JMeterResult(actualDuration.get(), ok.get(), ko.get(), results.toString(), responseTime);
    }
}

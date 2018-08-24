package org.loadtest4j.drivers.jmeter.parser;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.loadtest4j.drivers.jmeter.JMeterResult;

import java.net.URL;
import java.time.Duration;

import static org.junit.Assert.fail;

public class ParserTest {

    private URL fixture(String name) {
        return this.getClass().getClassLoader().getResource(name);
    }

    private Parser sut() {
        return new Parser();
    }

    @Test
    public void testParse() {
        final Parser parser = sut();

        final JMeterResult result = parser.parse(fixture("valid.jtl"));

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(result.getActualDuration()).as("Actual Duration").isEqualTo(Duration.ofMillis(8997));
            s.assertThat(result.getKo()).as("KO").isEqualTo(0);
            s.assertThat(result.getOk()).as("OK").isEqualTo(10);
            s.assertThat(result.getReportUrl()).as("Report URL").isPresent();
            s.assertThat(result.getResponseTime().getPercentile(100)).as("Max Response Time").isEqualTo(Duration.ofMillis(728));
        });
    }

    @Test(expected = CsvException.class)
    public void testParseInvalidResult() {
        final Parser parser = sut();

        parser.parse(fixture("invalid.jtl"));
    }

    @Test
    public void testParseResultWithNoSamples() {
        final Parser parser = sut();

        final JMeterResult result = parser.parse(fixture("empty.jtl"));

        SoftAssertions.assertSoftly(s -> {
            s.assertThat(result.getActualDuration()).as("Actual Duration").isEqualTo(Duration.ZERO);
            s.assertThat(result.getKo()).as("KO").isEqualTo(0);
            s.assertThat(result.getOk()).as("OK").isEqualTo(0);
            s.assertThat(result.getReportUrl()).as("Report URL").isPresent();
            s.assertThat(result.getResponseTime().getPercentile(100)).as("Max Response Time").isEqualTo(Duration.ZERO);
        });
    }
}

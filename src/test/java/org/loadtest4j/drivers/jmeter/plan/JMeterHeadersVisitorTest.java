package org.loadtest4j.drivers.jmeter.plan;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterHeadersVisitorTest extends BodyVisitorTest {

    @Override
    public void testString() {
        final JMeterHeadersVisitor visitor = new JMeterHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.string("foo").accept(visitor);

        assertThat(headers).containsEntry("Content-Type", "text/plain");
    }

    @Override
    public void testStringPart() {
        final JMeterHeadersVisitor visitor = new JMeterHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.parts(BodyPart.string("foo", "bar")).accept(visitor);

        assertThat(headers).isEmpty();
    }

    @Override
    public void testFilePart() {
        final JMeterHeadersVisitor visitor = new JMeterHeadersVisitor(Collections.emptyMap());

        final Map<String, String> headers = Body.parts(BodyPart.file(Paths.get("src/test/resources/example/valid.txt"))).accept(visitor);

        assertThat(headers).isEmpty();
    }

    @Test
    public void testFixHeaders() {
        final Map<String, String> input = Collections.singletonMap("foo", "bar");

        assertThat(JMeterHeadersVisitor.fixHeaders(input))
                .containsEntry("foo", "bar")
                .containsEntry("Content-Type", "text/plain");
    }

    @Test
    public void testFixHeadersPassthrough() {
        final Map<String, String> input = Collections.singletonMap("Content-Type", "application/json");

        assertThat(JMeterHeadersVisitor.fixHeaders(input))
                .isEqualTo(input);
    }

    @Test
    public void testFixHeadersPassthroughCaseInsensitive() {
        final Map<String, String> input = Collections.singletonMap("CoNtENt-TYPe", "application/json");

        assertThat(JMeterHeadersVisitor.fixHeaders(input))
                .isEqualTo(input);
    }
}

package org.loadtest4j.drivers.jmeter.plan;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Category(UnitTest.class)
public class JMeterBodyVisitorTest {

    @Test
    public void testString() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor("", Collections.emptyMap(), "", "", "", 0, "");

        final TestPlan.HttpSampler httpSampler = visitor.string("foo");

        final TestPlan.Header header = httpSampler.headers.get(0);

        assertSoftly(s -> {
            s.assertThat(httpSampler.body).isEqualTo("foo");
            s.assertThat(httpSampler.files).isEmpty();

            s.assertThat(header.name).isEqualTo("Content-Type");
            s.assertThat(header.value).isEqualTo("text/plain");
        });
    }

    @Test
    public void testStringPartFails() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor("", Collections.emptyMap(), "", "", "", 0, "");

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> visitor.parts(Collections.singletonList(BodyPart.string("foo", "bar"))))
                .withMessage("This driver does not support string parts in multipart requests.");
    }

    @Test
    public void testFilePart() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor("", Collections.emptyMap(), "", "", "", 0, "");

        final TestPlan.HttpSampler httpSampler = visitor.parts(Collections.singletonList(BodyPart.file(Paths.get("src/test/resources/example/valid.txt"))));

        final TestPlan.File file = httpSampler.files.get(0);

        assertSoftly(s -> {
            s.assertThat(httpSampler.body).isNull();
            s.assertThat(httpSampler.headers).isEmpty();

            s.assertThat(file.name).isEqualTo("valid.txt");
            s.assertThat(file.path).contains("valid.txt");
            s.assertThat(file.mimetype).isEqualTo("text/plain");
        });
    }

    @Test
    public void testFixHeaders() {
        final Map<String, String> input = Collections.singletonMap("foo", "bar");

        assertThat(JMeterBodyVisitor.fixHeaders(input))
                .containsEntry("foo", "bar")
                .containsEntry("Content-Type", "text/plain");
    }

    @Test
    public void testFixHeadersPassthrough() {
        final Map<String, String> input = Collections.singletonMap("Content-Type", "application/json");

        assertThat(JMeterBodyVisitor.fixHeaders(input))
                .isEqualTo(input);
    }

    @Test
    public void testFixHeadersPassthroughCaseInsensitive() {
        final Map<String, String> input = Collections.singletonMap("CoNtENt-TYPe", "application/json");

        assertThat(JMeterBodyVisitor.fixHeaders(input))
                .isEqualTo(input);
    }
}

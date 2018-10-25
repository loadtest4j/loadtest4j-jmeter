package org.loadtest4j.drivers.jmeter.plan;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Category(UnitTest.class)
public class JMeterFilesVisitorTest extends BodyVisitorTest {

    @Override
    public void testString() {
        final JMeterFilesVisitor visitor = new JMeterFilesVisitor();

        final List<TestPlan.File> files = Body.string("foo").accept(visitor);

        assertThat(files).isEmpty();
    }

    @Override
    public void testStringPart() {
        final JMeterFilesVisitor visitor = new JMeterFilesVisitor();

        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> Body.parts(BodyPart.string("foo", "bar")).accept(visitor))
                .withMessage("This driver does not support string parts in multipart requests.");
    }

    @Override
    public void testFilePart() {
        final JMeterFilesVisitor visitor = new JMeterFilesVisitor();

        final List<TestPlan.File> files = Body.parts(BodyPart.file(Paths.get("src/test/resources/example/valid.txt"))).accept(visitor);

        final TestPlan.File file = files.get(0);

        assertSoftly(s -> {
            s.assertThat(file.name).isEqualTo("valid.txt");
            s.assertThat(file.path).contains("valid.txt");
            s.assertThat(file.mimetype).isEqualTo("text/plain");
        });
    }
}

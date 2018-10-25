package org.loadtest4j.drivers.jmeter.plan;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterBodyVisitorTest extends BodyVisitorTest {

    @Override
    public void testString() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor();

        final String body = Body.string("foo").accept(visitor);

        assertThat(body).isEqualTo("foo");
    }

    @Override
    public void testStringPart() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor();

        final String body = Body.parts(BodyPart.string("foo", "bar")).accept(visitor);

        assertThat(body).isNull();
    }

    @Override
    public void testFilePart() {
        final JMeterBodyVisitor visitor = new JMeterBodyVisitor();

        final String body = Body.parts(BodyPart.file(Paths.get("src/test/resources/example/valid.txt"))).accept(visitor);

        assertThat(body).isNull();
    }


}

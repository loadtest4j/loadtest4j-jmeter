package org.loadtest4j.drivers.jmeter.plan;

import org.junit.experimental.categories.Category;
import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.junit.UnitTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@Category(UnitTest.class)
public class JMeterBodyMatcherTest extends BodyMatcherTest {

    @Override
    public void testString() {
        final JMeterBodyMatcher matcher = new JMeterBodyMatcher();

        final String body = Body.string("foo").match(matcher);

        assertThat(body).isEqualTo("foo");
    }

    @Override
    public void testStringPart() {
        final JMeterBodyMatcher matcher = new JMeterBodyMatcher();

        final String body = Body.multipart(BodyPart.string("foo", "bar")).match(matcher);

        assertThat(body).isNull();
    }

    @Override
    public void testFilePart() {
        final JMeterBodyMatcher matcher = new JMeterBodyMatcher();

        final String body = Body.multipart(BodyPart.file(Paths.get("src/test/resources/example/valid.txt"))).match(matcher);

        assertThat(body).isNull();
    }


}

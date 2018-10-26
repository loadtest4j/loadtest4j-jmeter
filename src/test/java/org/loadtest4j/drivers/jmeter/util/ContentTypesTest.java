package org.loadtest4j.drivers.jmeter.util;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.jmeter.junit.IntegrationTest;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Category(IntegrationTest.class)
public class ContentTypesTest {
    @Test
    public void testDetectTypeOfPlainTextFile() {
        final String type = ContentTypes.detect(Paths.get("src/test/resources/example/valid.txt"));

        assertThat(type).isEqualTo("text/plain");
    }

    @Test
    public void testDetectTypeOfNonFileFails() {
        assertThatExceptionOfType(LoadTesterException.class)
                .isThrownBy(() -> ContentTypes.detect(Paths.get("src/test/resources/example/")));
    }
}

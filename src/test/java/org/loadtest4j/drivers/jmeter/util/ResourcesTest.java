package org.loadtest4j.drivers.jmeter.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.loadtest4j.drivers.jmeter.junit.IntegrationTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@Category(IntegrationTest.class)
public class ResourcesTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testCopyFolder() throws IOException {
        final Path destination = temporaryFolder.getRoot().toPath().resolve("bin");

        Resources.copy("bin", destination.toFile());

        assertThat(destination)
                .exists()
                .isDirectory();

        final Path someFile = destination.resolve("jmeter.properties");

        assertThat(someFile)
                .exists()
                .isRegularFile();
    }

    @Test
    public void testCopyInvalidSource() throws IOException {
        final Path destination = temporaryFolder.getRoot().toPath().resolve("invalid");

        thrown.expect(IOException.class);
        thrown.expectMessage("Could not open a connection to the URL: /fake");

        Resources.copy("/fake", destination.toFile());
    }

    @Test
    public void testCopyDestinationAlreadyExists() throws IOException {
        final File destination = temporaryFolder.newFolder();

        thrown.expect(IOException.class);
        thrown.expectMessage("Mkdir failed for: " + destination.getAbsolutePath());

        Resources.copy("bin", destination);


    }
}

package org.loadtest4j.drivers.jmeter;

import com.xebialabs.restito.builder.verify.VerifyHttp;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.loadtest4j.Body;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.driver.Driver;
import org.loadtest4j.driver.DriverRequest;
import org.loadtest4j.driver.DriverResult;
import org.loadtest4j.drivers.jmeter.junit.IntegrationTest;
import org.loadtest4j.drivers.jmeter.junit.MultiPartConditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.loadtest4j.drivers.jmeter.junit.DriverResultAssert.assertThat;

@Category(IntegrationTest.class)
public class JMeterTest {

    private static final Duration EXPECTED_DURATION = Duration.ofMillis(500);

    private StubServer httpServer;

    static {
        // Silence Restito logging.
        Logger.getLogger("org.glassfish.grizzly").setLevel(Level.OFF);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private Path createTempFile(String name, String content) {
        final Path file;
        try {
            file = temporaryFolder.newFile(name).toPath();
            Files.write(file, Collections.singleton(content));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    @Before
    public void startServer() {
        httpServer = new StubServer().run();
    }

    @After
    public void stopServer() {
        httpServer.stop();
    }

    private Driver sut() {
        return new JMeter("localhost", 10, httpServer.getPort(), "http", 3);
    }

    @Test
    public void testRun()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.get("/"));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0)
                .hasActualDurationGreaterThan(EXPECTED_DURATION)
                .hasMaxResponseTimeGreaterThan(Duration.ZERO);
    }

    @Test
    public void testRunWithMultipleRequests() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.OK_200));
        // And
        whenHttp(httpServer).match(get("/pets")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Arrays.asList(DriverRequests.get("/"), DriverRequests.get("/pets"));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithJsonPost() {
        // Given
        final Driver driver = sut();
        // And
        final String body = "{" + "\n"
                + "\"three\": \"bott\\les\"" + "\n"
                + "}";
        // And
        whenHttp(httpServer).match(post("/pets"), withPostBodyContaining(body)).then(status(HttpStatus.OK_200));

        // When
        final DriverRequest edgeCaseReq = new DriverRequest(Body.string(body), Collections.emptyMap(),"POST","/pets", Collections.emptyMap());
        final List<DriverRequest> requests = Collections.singletonList(edgeCaseReq);
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithEdgeCaseRequest() {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer)
                .match(post("/pets"), withHeader("fo'o", "ba'r"), withPostBodyContaining("three\ngreen\nbottles"))
                .then(status(HttpStatus.OK_200));

        // When
        final DriverRequest edgeCaseReq = new DriverRequest(Body.string("three\ngreen\nbottles"),
                Collections.singletonMap("fo'o", "ba'r"),
                "POST",
                "/pets",
                Collections.emptyMap());
        final List<DriverRequest> requests = Collections.singletonList(edgeCaseReq);
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithErrors()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/")).then(status(HttpStatus.NOT_FOUND_404));

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.get("/"));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOk(0)
                .hasKoGreaterThan(0);
    }

    @Test
    public void testRunWithQueryString()  {
        // Given
        final Driver driver = sut();
        // And
        whenHttp(httpServer).match(get("/"), parameter("foo", "bar")).then(status(HttpStatus.OK_200));

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.getWithQueryParams("/", Collections.singletonMap("foo", "bar")));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOkGreaterThan(0)
                .hasKo(0);
    }

    @Test
    public void testRunWithBrokenDriver() {
        // Given
        final Driver driver = new JMeter("localhost", 1, 1, "http", 2);

        // When
        final List<DriverRequest> requests = Collections.singletonList(DriverRequests.getWithQueryParams("/", Collections.singletonMap("foo", "bar")));
        final DriverResult result = driver.run(requests);

        // Then
        assertThat(result)
                .hasOk(0)
                .hasKoGreaterThan(0);
    }

    @Test
    public void testRunWithMultiPartFileUpload() {
        // Given
        final Driver driver = sut();
        // And
        final Path foo = createTempFile("foo.txt", "foo");
        final Path bar = createTempFile("bar.txt", "bar");
        // And
        whenHttp(httpServer)
                .match(post("/"),
                        withHeader("Authorization", "Bearer abc123"),
                        MultiPartConditions.withMultipartFormHeader(),
                        MultiPartConditions.withPostBodyContainingFilePart("foo.txt", "text/plain", "foo"),
                        MultiPartConditions.withPostBodyContainingFilePart("bar.txt", "text/plain", "bar"))
                .then(status(HttpStatus.OK_200));

        // When
        final Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer abc123");
        final DriverRequest request = DriverRequests.uploadMultiPart("/", foo, bar, headers);
        final DriverResult result = driver.run(Collections.singletonList(request));

        // Then
        assertThat(result)
                .hasOkGreaterThan(1)
                .hasKo(0);
        // And
        VerifyHttp.verifyHttp(httpServer).atLeast(1,
                method(Method.POST),
                uri("/"),
                withHeader("Authorization", "Bearer abc123"),
                MultiPartConditions.withMultipartFormHeader(),
                MultiPartConditions.withPostBodyContainingFilePart("foo.txt", "text/plain", "foo"),
                MultiPartConditions.withPostBodyContainingFilePart("bar.txt", "text/plain", "bar"));
    }

    @Test
    public void testRunWithMultiPartStringUpload() {
        // Given
        final Driver driver = sut();

        // Expect
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("This driver does not support string parts in multipart requests.");

        // When
        final Map<String, String> headers = Collections.singletonMap("Authorization", "Bearer abc123");
        final DriverRequest request = DriverRequests.uploadMultiPart("/", "a", "foo", "b", "bar", headers);
        driver.run(Collections.singletonList(request));
    }

    @Test
    public void testRunWithNoRequests() {
        // Given
        final Driver driver = sut();

        // Expect
        thrown.expect(LoadTesterException.class);
        thrown.expectMessage("No requests were specified for the load test.");

        // When
        driver.run(Collections.emptyList());
    }
}

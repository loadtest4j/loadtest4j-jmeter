package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.driver.DriverRequest;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class DriverRequests {
    static DriverRequest get(String path) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    public static DriverRequest getWithBodyAndHeaders(String path, String body, Map<String, String> headers) {
        return new DriverRequest(Body.string(body), headers, "GET", path, Collections.emptyMap());
    }

    static DriverRequest getWithQueryParams(String path, Map<String, String> queryParams) {
        return new DriverRequest(Body.string(""), Collections.emptyMap(), "GET", path, queryParams);
    }

    static DriverRequest post(String path, String body, Map<String, String> headers) {
        return new DriverRequest(Body.string(body), headers, "POST", path, Collections.emptyMap());
    }

    static DriverRequest uploadMultiPart(String path, Path a, Path b, Map<String, String> headers) {
        final Body body = Body.parts(BodyPart.file(a), BodyPart.file(b));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }

    static DriverRequest uploadMultiPart(String path, String a, String aContent, String b, String bContent, Map<String, String> headers) {
        final Body body = Body.parts(BodyPart.string(a, aContent), BodyPart.string(b, bContent));
        return new DriverRequest(body, headers, "POST", path, Collections.emptyMap());
    }
}
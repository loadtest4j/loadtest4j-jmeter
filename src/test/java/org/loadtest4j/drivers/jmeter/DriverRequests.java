package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.driver.DriverRequest;

import java.util.Collections;
import java.util.Map;

public class DriverRequests {
    public static DriverRequest method(String method, String path) {
        return new DriverRequest("", Collections.emptyMap(), method, path, Collections.emptyMap());
    }

    public static DriverRequest get(String path) {
        return new DriverRequest("", Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    public static DriverRequest getWithBody(String path, String body) {
        return new DriverRequest(body, Collections.emptyMap(), "GET", path, Collections.emptyMap());
    }

    public static DriverRequest getWithQueryParams(String path, Map<String, String> queryParams) {
        return new DriverRequest("", Collections.emptyMap(), "GET", path, queryParams);
    }

    public static DriverRequest getWithHeaders(String path, Map<String, String> headers) {
        return new DriverRequest("", headers, "GET", path, Collections.emptyMap());
    }

    public static DriverRequest getWithBodyAndHeaders(String path, String body, Map<String, String> headers) {
        return new DriverRequest(body, headers, "GET", path, Collections.emptyMap());
    }
}
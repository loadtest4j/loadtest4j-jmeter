package org.loadtest4j.drivers.jmeter.plan;

import java.util.List;

class TestPlan {

    final ThreadGroup threadGroup;

    TestPlan(ThreadGroup threadGroup) {
        this.threadGroup = threadGroup;
    }

    static class ThreadGroup {
        final List<HttpSampler> httpSamplers;
        final int numThreads;
        final int rampTime;

        ThreadGroup(List<HttpSampler> httpSamplers, int numThreads, int rampTime) {
            this.httpSamplers = httpSamplers;
            this.numThreads = numThreads;
            this.rampTime = rampTime;
        }
    }

    static class HttpSampler {
        final String body;
        final String domain;
        final List<Header> headers;
        final String method;
        final String name;
        final String path;
        final int port;
        final String protocol;

        HttpSampler(String body, String domain, List<Header> headers, String method, String name, String path, int port, String protocol) {
            this.body = body;
            this.domain = domain;
            this.headers = headers;
            this.method = method;
            this.name = name;
            this.path = path;
            this.port = port;
            this.protocol = protocol;
        }
    }

    static class Header {
        final String name;
        final String value;

        Header(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}

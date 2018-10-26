package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;

import java.util.List;

class JMeterBodyMatcher implements Body.Matcher<String> {

    @Override
    public String string(String content) {
        return content;
    }

    @Override
    public String multipart(List<BodyPart> body) {
        return null;
    }
}

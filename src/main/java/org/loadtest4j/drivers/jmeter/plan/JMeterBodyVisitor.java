package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;

import java.util.List;

class JMeterBodyVisitor implements Body.Visitor<String> {

    @Override
    public String string(String content) {
        return content;
    }

    @Override
    public String parts(List<BodyPart> body) {
        return null;
    }
}

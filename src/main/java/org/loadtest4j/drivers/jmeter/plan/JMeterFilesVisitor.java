package org.loadtest4j.drivers.jmeter.plan;

import org.loadtest4j.Body;
import org.loadtest4j.BodyPart;
import org.loadtest4j.drivers.jmeter.util.ContentTypes;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class JMeterFilesVisitor implements Body.Visitor<List<TestPlan.File>> {
    @Override
    public List<TestPlan.File> string(String body) {
        return Collections.emptyList();
    }

    @Override
    public List<TestPlan.File> parts(List<BodyPart> body) {
        return body.stream()
                .map(part -> part.accept(new JMeterBodyPartVisitor()))
                .collect(Collectors.toList());
    }

    private static class JMeterBodyPartVisitor implements BodyPart.Visitor<TestPlan.File> {
        @Override
        public TestPlan.File stringPart(String name, String content) {
            throw new UnsupportedOperationException("This driver does not support string parts in multipart requests.");
        }

        @Override
        public TestPlan.File filePart(Path content) {
            final String name = Optional.ofNullable(content.getFileName()).orElseThrow(() -> new NullPointerException("Path did not have a filename.")).toString();
            final String path = content.toAbsolutePath().toString();
            final String contentType = ContentTypes.detect(content);

            return new TestPlan.File(contentType, name, path);
        }
    }
}

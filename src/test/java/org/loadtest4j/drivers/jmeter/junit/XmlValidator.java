package org.loadtest4j.drivers.jmeter.junit;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.Reader;
import java.io.StringReader;

public class XmlValidator {
    public static void validateXml(String str) throws Exception {
        // From https://stackoverflow.com/a/6362975
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);

        final DocumentBuilder builder = factory.newDocumentBuilder();

        builder.setErrorHandler(new SimpleErrorHandler());

        try (Reader reader = new StringReader(str)) {
            // the "parse" method also validates XML, will throw an exception if misformatted
            builder.parse(new InputSource(reader));
        }
    }

    private static class SimpleErrorHandler implements ErrorHandler {
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }

        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}

package org.loadtest4j.drivers.jmeter.util;

import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.loadtest4j.LoadTesterException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;

public final class ContentTypes {

    private ContentTypes() {

    }

    public static String detect(Path path) {
        try {
            final TikaConfig config = new TikaConfig(ContentTypes.class.getResource("/tika-config.xml"));
            final Tika tika = new Tika(config);
            return tika.detect(path);
        } catch (TikaException | IOException | SAXException e) {
            throw new LoadTesterException(e);
        }
    }
}

package org.loadtest4j.drivers.jmeter.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class Resources {
    private Resources() {}

    /**
     * Adapted from https://github.com/nguyenq/tess4j
     *
     * @param source the jar resource (file or 'folder')
     * @param destination the destination to copy the resource to
     * @throws IOException if copying didn't work
     */
    public static void copy(String source, File destination) throws IOException {

        final URL resourceUrl = Resources.class.getClassLoader().getResource(source);

        final JarURLConnection jarConnection;
        try {
            jarConnection = (JarURLConnection) resourceUrl.openConnection();
        } catch (NullPointerException e) {
            throw new IOException("Could not open a connection to the URL: " + source);
        }

        try (JarFile jarFile = jarConnection.getJarFile()) {
            String jarConnectionEntryName = jarConnection.getEntryName();
            if (!jarConnectionEntryName.endsWith("/")) {
                jarConnectionEntryName += "/";
            }

            // Iterate all entries in the jar file.
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
                final JarEntry jarEntry = e.nextElement();
                final String jarEntryName = jarEntry.getName();

                // Extract files only if they match the path.
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    final String filename = jarEntryName.substring(jarConnectionEntryName.length());
                    final File targetFile = new File(destination, filename);

                    if (jarEntry.isDirectory()) {
                        final boolean success = targetFile.mkdirs();
                        if (!success) {
                            throw new IOException("Mkdir failed for: " + targetFile.getAbsolutePath());
                        }
                    } else {
                        if (!targetFile.exists() || targetFile.length() != jarEntry.getSize()) {
                            try (InputStream is = jarFile.getInputStream(jarEntry);
                                 OutputStream out = FileUtils.openOutputStream(targetFile)) {
                                IOUtils.copy(is, out);
                            }
                        }
                    }
                }
            }
        }
    }
}

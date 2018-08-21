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
     * @param targetPath the destination to copy the resource to
     * @throws IOException if copying didn't work
     */
    public static void copy(String source, File targetPath) throws IOException {

        URL resourceUrl = Resources.class.getClassLoader().getResource(source);

        JarURLConnection jarConnection = (JarURLConnection) resourceUrl.openConnection();

        try (JarFile jarFile = jarConnection.getJarFile()) {
            String jarConnectionEntryName = jarConnection.getEntryName();
            if (!jarConnectionEntryName.endsWith("/")) {
                jarConnectionEntryName += "/";
            }

            // Iterate all entries in the jar file.
            for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements(); ) {
                JarEntry jarEntry = e.nextElement();
                String jarEntryName = jarEntry.getName();

                // Extract files only if they match the path.
                if (jarEntryName.startsWith(jarConnectionEntryName)) {
                    String filename = jarEntryName.substring(jarConnectionEntryName.length());
                    File targetFile = new File(targetPath, filename);

                    if (jarEntry.isDirectory()) {
                        targetFile.mkdirs();
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

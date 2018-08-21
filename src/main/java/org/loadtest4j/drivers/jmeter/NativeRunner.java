package org.loadtest4j.drivers.jmeter;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;

class NativeRunner implements Runner {
    @Override
    public File runJmeter(File jmxFile) {
        final HashTree hashTree = loadConfig(jmxFile);

        final File resultFile = createTempFile("loadtest4j", ".jtl");

        addResultCollector(hashTree, resultFile);

        final StandardJMeterEngine jm = new StandardJMeterEngine();

        jm.configure(hashTree);

        jm.run();

        return resultFile;

    }

    private static HashTree loadConfig(File jmxFile) {
        try {
            return  SaveService.loadTree(jmxFile);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static void addResultCollector(HashTree hashTree, File resultFile) {
        final ResultCollector resultCollector = createResultCollector(resultFile);

        hashTree.add(hashTree.getArray()[0], resultCollector);
    }

    private static ResultCollector createResultCollector(File resultFile) {
        final ResultCollector resultCollector = new ResultCollector();
        resultCollector.setFilename(resultFile.getAbsolutePath());

        return resultCollector;
    }

    private static File createTempFile(String prefix, String suffix) {
        try {
            return File.createTempFile(prefix, suffix);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}

package org.loadtest4j.drivers.jmeter.engine;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NativeEngine implements Engine {
    @Override
    public File runJmeter(File testPlan) {
        final HashTree hashTree = loadTestPlan(testPlan);

        final Path resultFile = newResultFile();

        addResultCollector(hashTree, resultFile);

        final StandardJMeterEngine jm = new StandardJMeterEngine();

        jm.configure(hashTree);

        jm.run();

        return resultFile.toFile();

    }

    private static HashTree loadTestPlan(File testPlan) {
        try {
            return  SaveService.loadTree(testPlan);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static void addResultCollector(HashTree hashTree, Path resultFile) {
        final ResultCollector resultCollector = createResultCollector(resultFile);

        hashTree.add(hashTree.getArray()[0], resultCollector);
    }

    private static ResultCollector createResultCollector(Path resultFile) {
        final ResultCollector resultCollector = new ResultCollector();
        resultCollector.setFilename(resultFile.toFile().getAbsolutePath());

        return resultCollector;
    }

    private static Path newResultFile() {
        final Path runDirectory;
        try {
            runDirectory = Files.createTempDirectory("loadtest4j");
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
        return runDirectory.resolve("result.jtl");
    }
}

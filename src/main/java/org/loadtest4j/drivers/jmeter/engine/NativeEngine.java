package org.loadtest4j.drivers.jmeter.engine;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NativeEngine implements Engine {

    private final Path resultsDirectory;

    public NativeEngine(Path resultsDirectory) {
        this.resultsDirectory = resultsDirectory;
    }

    public static NativeEngine standard() {
        final Path resultsDirectory = new File(System.getProperty("user.dir")).toPath().resolve("results");
        return new NativeEngine(resultsDirectory);
    }

    @Override
    public File runJmeter(File testPlan) {
        loadJmeterProperties();

        final HashTree hashTree = loadTestPlan(testPlan);

        final Path resultFile = newResultFile();

        addResultCollector(hashTree, resultFile);

        final StandardJMeterEngine jm = new StandardJMeterEngine();

        jm.configure(hashTree);

        jm.run();

        return resultFile.toFile();

    }

    /**
     * Run this before constructing jmeter API objects because they may read these properties.
     */
    private static void loadJmeterProperties() {
        final File jmeterHome = createTempDirectory("jmeter_home");
        extractConfigSettings(jmeterHome);

        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());

        final Path jmeterProperties = jmeterHome.toPath().resolve("bin").resolve("jmeter.properties");
        JMeterUtils.loadJMeterProperties(jmeterProperties.toString());
    }

    private static File createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix).toFile();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static void extractConfigSettings(File jmeterHome) {
        try {
            Resources.copy("bin", new File(jmeterHome, "bin"));
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static HashTree loadTestPlan(File testPlan) {
        try {
            return  SaveService.loadTree(testPlan);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private Path newResultFile() {
        final String timestamp = String.valueOf(System.currentTimeMillis());

        final Path proposedPath = this.resultsDirectory
                .resolve("loadtest4j-" + timestamp)
                .resolve("result.jtl");

        if (Files.exists(proposedPath)) {
            throw new LoadTesterException("JMeter result file already exists.");
        }

        return proposedPath;
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
}

package org.loadtest4j.drivers.jmeter.engine;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.jmeter.util.Resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NativeEngine implements Engine {

    private final Path resultsDirectory;

    public NativeEngine(Path resultsDirectory) {
        this.resultsDirectory = resultsDirectory;
    }

    @Override
    public File runJmeter(File testPlan) {
        try {
            final Path resultFile = newResultFile(this.resultsDirectory);

            loadJmeterProperties();
            final HashTree hashTree = loadTestPlan(testPlan);
            addResultCollector(hashTree, resultFile);
            final StandardJMeterEngine jm = new StandardJMeterEngine();
            jm.configure(hashTree);
            jm.run();

            return resultFile.toFile();
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }

    private static Path newResultFile(Path resultsDirectory) throws IOException {
        final String timestamp = String.valueOf(System.currentTimeMillis());

        final Path proposedPath = resultsDirectory
                .resolve("loadtest4j-" + timestamp)
                .resolve("result.jtl");

        if (Files.exists(proposedPath)) {
            throw new IOException("JMeter result file already exists.");
        }

        return proposedPath;
    }

    /**
     * Run this before constructing jmeter API objects because they may read these properties.
     */
    private static void loadJmeterProperties() throws IOException {
        final Path jmeterHome = createJMeterHome();
        JMeterUtils.setJMeterHome(jmeterHome.toAbsolutePath().toString());

        final Path jmeterProperties = jmeterHome.resolve("bin").resolve("jmeter.properties");
        JMeterUtils.loadJMeterProperties(jmeterProperties.toString());
    }

    private static Path createJMeterHome() throws IOException {
        final File jmeterHome = Files.createTempDirectory("jmeter_home").toFile();
        Resources.copy("bin", new File(jmeterHome, "bin"));
        return jmeterHome.toPath();
    }

    private static HashTree loadTestPlan(File testPlan) throws IOException {
        return SaveService.loadTree(testPlan);
    }

    private static void addResultCollector(HashTree hashTree, Path resultFile) {
        final ResultCollector resultCollector = new ResultCollector();
        resultCollector.setFilename(resultFile.toFile().getAbsolutePath());

        hashTree.add(hashTree.getArray()[0], resultCollector);
    }
}

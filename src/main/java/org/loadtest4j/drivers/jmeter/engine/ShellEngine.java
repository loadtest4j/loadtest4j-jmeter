package org.loadtest4j.drivers.jmeter.engine;

import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.jmeter.util.*;
import org.loadtest4j.drivers.jmeter.util.Process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ShellEngine implements Engine {

    private final String executable;

    private ShellEngine(String executable) {
        this.executable = executable;
    }

    public static Engine standard() {
        return new ShellEngine("jmeter");
    }

    @Override
    public File runJmeter(File testPlan) {
        final Path resultFile = newResultFile();

        final List<String> arguments = new ArgumentBuilder()
                .addArgument("-n")
                .addNamedArgument("-t", testPlan.getAbsolutePath())
                .addNamedArgument("-l", resultFile.toString())
                .build();

        final Command command = new Command(arguments, executable);

        final Process process = new Shell().start(command);

        // FIXME use it or remove stdout printer
        final int exitStatus = process.waitFor();

        final String output = StreamReader.streamToString(process.getStdout());
        System.out.println(output);

        return resultFile.toFile();
    }

    private static Path newResultFile() {
        final Path runDirectory = createTempDirectory("loadtest4j");
        return runDirectory.resolve("result.jtl");
    }

    private static Path createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}

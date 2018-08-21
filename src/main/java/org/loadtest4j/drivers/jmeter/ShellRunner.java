package org.loadtest4j.drivers.jmeter;

import org.loadtest4j.LoadTesterException;
import org.loadtest4j.drivers.jmeter.util.*;
import org.loadtest4j.drivers.jmeter.util.Process;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class ShellRunner implements Runner {

    private final String executable;

    ShellRunner(String executable) {
        this.executable = executable;
    }

    protected static ShellRunner standard() {
        return new ShellRunner("jmeter");
    }

    @Override
    public File runJmeter(File jmxFile) {
        final Path runDirectory = createTempDirectory("loadtest4j");
        final Path resultFile = runDirectory.resolve("result.jtl");

        final List<String> arguments = new ArgumentBuilder()
                .addArgument("-n")
                .addNamedArgument("-t", jmxFile.getAbsolutePath())
                .addNamedArgument("-l", resultFile.toString())
                .build();

        final Command command = new Command(arguments, executable);

        final Process process = new Shell().start(command);

        final int exitStatus = process.waitFor();

        final String output = StreamReader.streamToString(process.getStdout());
        System.out.println(output);

        return resultFile.toFile();
    }

    private static Path createTempDirectory(String prefix) {
        try {
            return Files.createTempDirectory(prefix);
        } catch (IOException e) {
            throw new LoadTesterException(e);
        }
    }
}

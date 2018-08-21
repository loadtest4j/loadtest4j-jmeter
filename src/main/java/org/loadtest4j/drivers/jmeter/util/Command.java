package org.loadtest4j.drivers.jmeter.util;

import java.util.Collections;
import java.util.List;

public class Command {

    private final List<String> arguments;
    private final String launchPath;

    public Command(List<String> arguments, String launchPath) {
        this.arguments = arguments;
        this.launchPath = launchPath;
    }

    public List<String> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    public String getLaunchPath() {
        return launchPath;
    }
}

package org.loadtest4j.drivers.jmeter.util;

import org.loadtest4j.LoadTesterException;

import java.io.InputStream;

public class Process {

    private final java.lang.Process process;

    Process(java.lang.Process process) {
        this.process = process;
    }

    public InputStream getStderr() {
        return process.getErrorStream();
    }

    public InputStream getStdout() {
        return process.getInputStream();
    }

    public Integer waitFor() {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            throw new LoadTesterException(e);
        }
    }
}

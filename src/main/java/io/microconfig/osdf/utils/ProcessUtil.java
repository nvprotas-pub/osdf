package io.microconfig.osdf.utils;

import java.io.IOException;

import static java.lang.Thread.currentThread;

public class ProcessUtil {
    public static int startAndWait(ProcessBuilder processBuilder) {
        try {
            return startProcess(processBuilder).waitFor();
        } catch (InterruptedException e) {
            currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public static Process startProcess(ProcessBuilder processBuilder) {
        try {
            return processBuilder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

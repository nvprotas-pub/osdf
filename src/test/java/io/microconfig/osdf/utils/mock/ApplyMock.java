package io.microconfig.osdf.utils.mock;

import io.microconfig.osdf.commandline.CommandLineOutput;

import static io.microconfig.osdf.commandline.CommandLineOutput.output;


public class ApplyMock implements OCMock {
    public static ApplyMock applyMock() {
        return new ApplyMock();
    }

    @Override
    public CommandLineOutput execute(String command) {
        return output("ok");
    }

    @Override
    public String pattern() {
        return "oc apply -f (.*)";
    }
}

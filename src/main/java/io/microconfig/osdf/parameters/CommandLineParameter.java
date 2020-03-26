package io.microconfig.osdf.parameters;

import org.apache.commons.cli.Option;

public interface CommandLineParameter<T> {
    Option toOption();

    String name();

    T get();

    void setString(String param);

    String missingHint();
}

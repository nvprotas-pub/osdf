package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;

import java.util.List;

import static io.microconfig.osdf.parameters.ParameterUtils.toList;

public class ComponentsParameter extends ArgParameter<List<String>> {
    public ComponentsParameter() {
        super("components", "c", "Comma separated list of components");
    }

    @Override
    public List<String> get() {
        return toList(getValue());
    }
}

package io.microconfig.osdf.api.parameter;

import io.microconfig.osdf.parameters.ArgParameter;
import io.microconfig.osdf.state.Credentials;

import static io.microconfig.osdf.state.Credentials.of;

public class OpenShiftCredentialsParameter extends ArgParameter<Credentials> {
    public OpenShiftCredentialsParameter() {
        super("credentials", "c", "Credentials for OpenShift: login:password or token");
    }

    @Override
    public Credentials get() {
        if (getValue() == null) return null;
        return of(getValue());
    }
}

package io.microconfig.osdf.state;

import io.microconfig.osdf.microconfig.properties.PropertyGetter;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import static io.microconfig.osdf.microconfig.properties.OSDFDownloadProperties.properties;
import static io.microconfig.osdf.utils.JarUtils.jarPath;
import static io.microconfig.osdf.utils.StringUtils.castToInteger;

@RequiredArgsConstructor
@EqualsAndHashCode
public class OSDFVersion {
    private final int major;
    private final int minor;
    private final int patch;

    public static OSDFVersion fromJar() {
        String filename = jarPath().getFileName().toString();
        filename = filename.substring(0, filename.length() - 4);
        String[] dashSplit = filename.split("-");
        if (dashSplit.length < 2) throw new RuntimeException("Bad jar file name. Should be <name>-<version>.jar");
        return fromString(dashSplit[dashSplit.length - 1]);
    }

    public static OSDFVersion fromState(OSDFState state) {
        return fromString(state.getOsdfVersion());
    }

    public static OSDFVersion fromConfigs(PropertyGetter propertyGetter) {
        return fromString(properties(propertyGetter).version());
    }

    public static OSDFVersion fromString(String s) {
        if (s == null) return null;

        String[] split = s.split("\\.");
        if (split.length != 3) return null;

        Integer major = castToInteger(split[0]);
        Integer minor = castToInteger(split[1]);
        Integer patch = castToInteger(split[2]);
        if (major == null || minor == null || patch == null) return null;
        return new OSDFVersion(major, minor, patch);
    }

    public boolean olderThan(OSDFVersion other) {
        if (major != other.major) return major < other.major;
        if (minor != other.minor) return minor < other.minor;
        if (patch != other.patch) return patch < other.patch;
        return false;
    }

    public boolean hasOlderMinorThan(OSDFVersion other) {
        if (major != other.major) return major < other.major;
        if (minor != other.minor) return minor < other.minor;
        return false;
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }
}

package io.microconfig.osdf.install.jarinstaller;

import io.microconfig.osdf.paths.OSDFPaths;
import io.microconfig.osdf.state.OSDFVersion;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;

import static io.microconfig.osdf.state.OSDFVersion.fromJarPath;
import static io.microconfig.osdf.utils.CommandLineExecutor.execute;
import static io.microconfig.osdf.utils.JarUtils.jarPath;

@RequiredArgsConstructor
public class LocalJarInstaller implements JarInstaller {
    private final Path jarPath;
    private final OSDFPaths paths;

    public static LocalJarInstaller jarInstaller(OSDFPaths paths) {
        return new LocalJarInstaller(jarPath(), paths);
    }

    @Override
    public OSDFVersion version() {
        return fromJarPath(jarPath);
    }

    @Override
    public void prepare() {
        execute("cp " + jarPath + " " + paths.tmp() + "/osdf.jar");
    }

    @Override
    public void replace() {
        execute("cp " + paths.tmp() + "/osdf.jar " + paths.root() + "/osdf.jar");
    }
}

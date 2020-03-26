package io.microconfig.osdf.api;

import io.microconfig.osdf.commands.*;
import io.microconfig.osdf.components.checker.LogHealthChecker;
import io.microconfig.osdf.config.OSDFPaths;
import io.microconfig.osdf.nexus.NexusArtifact;
import io.microconfig.osdf.openshift.OCExecutor;
import io.microconfig.osdf.state.ConfigSource;
import io.microconfig.osdf.state.Credentials;
import io.microconfig.osdf.state.OSDFState;
import lombok.RequiredArgsConstructor;

import java.nio.file.Path;
import java.util.List;

import static io.microconfig.osdf.api.OSDFApiInfo.printHelpForMethod;
import static io.microconfig.osdf.commands.UpdateCommand.updateCommand;
import static io.microconfig.osdf.components.checker.LogHealthChecker.logHealthChecker;
import static io.microconfig.osdf.microconfig.properties.HealthCheckProperties.properties;
import static io.microconfig.osdf.microconfig.properties.PropertyGetter.propertyGetter;
import static io.microconfig.osdf.printer.ColumnPrinter.printer;
import static io.microconfig.osdf.state.OSDFVersion.fromJar;

@RequiredArgsConstructor
public class OSDFApiImpl implements OSDFApi {
    private final OSDFPaths paths;
    private final OCExecutor oc;

    public static OSDFApi osdfApi(OSDFPaths paths, OCExecutor oc) {
        return new OSDFApiImpl(paths, oc);
    }

    @Override
    public void install() {
        new InstallCommand(paths, fromJar()).install();
    }

    @Override
    public void init(String gitUrl, String nexusUrl, NexusArtifact configsNexusArtifact, Path localConfigs, ConfigSource configSource, Credentials openShiftCredentials, Credentials nexusCredentials,
                     String env, String configVersion, String group, String projVersion, List<String> components) {
        new InitCommand(paths).run(gitUrl, nexusUrl, configsNexusArtifact, localConfigs, configSource, openShiftCredentials, nexusCredentials, env, configVersion, group, projVersion, components);
    }

    @Override
    public void deploy(List<String> components) {
        new DeployCommand(paths, oc).run(components);
    }

    @Override
    public void status(List<String> components) {
        new StatusCommand(paths, oc, getLogHealthChecker(), printer()).run(components);
    }

    @Override
    public void restart(List<String> components) {
        new RestartCommand(paths, oc).run(components);
    }

    @Override
    public void stop(List<String> components) {
        new StopCommand(paths, oc).run(components);
    }

    @Override
    public void delete(List<String> components) {
        new DeleteCommand(paths, oc).delete(components);
    }

    @Override
    public void state() {
        new CurrentStateCommand(paths.stateSavePath()).run();
    }

    @Override
    public void pods(List<String> components) {
        new PodsCommand(paths, oc, getLogHealthChecker(), printer()).show(components);
    }

    @Override
    public void deletePod(String component, List<String> pods) {
        new DeletePodCommand(paths, oc).delete(component, pods);
    }

    @Override
    public void logs(String component, String pod) {
        new LogsCommand(paths, oc).show(component, pod);
    }

    @Override
    public void propertiesDiff(List<String> components) {
        new PropertiesDiffCommand(paths.componentsPath()).show(components);
    }

    @Override
    public void update() {
        updateCommand(paths).update();
    }

    @Override
    public void help(String command) {
        printHelpForMethod(command);
    }

    @Override
    public void howToStart() {
        new HowToStartCommand().show();
    }

    private LogHealthChecker getLogHealthChecker() {
        String env = OSDFState.fromFile(paths.stateSavePath()).getEnv();
        return logHealthChecker(properties(propertyGetter(env, paths.configPath())));
    }
}

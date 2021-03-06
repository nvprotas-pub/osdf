package io.microconfig.osdf.api.implementations;

import io.microconfig.osdf.api.declarations.ComponentsApi;
import io.microconfig.osdf.commands.PropertiesDiffCommand;
import io.microconfig.osdf.configs.ConfigsSettings;
import io.microconfig.osdf.paths.OSDFPaths;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static io.microconfig.osdf.resources.ResourcesHashComputer.resourcesHashComputer;
import static io.microconfig.osdf.microconfig.MicroConfig.microConfig;
import static io.microconfig.osdf.microconfig.properties.PropertySetter.propertySetter;
import static io.microconfig.osdf.settings.SettingsFile.settingsFile;
import static java.nio.file.Path.of;

@RequiredArgsConstructor
public class ComponentsApiImpl implements ComponentsApi {
    private final OSDFPaths paths;

    public static ComponentsApi componentsApi(OSDFPaths paths) {
        return new ComponentsApiImpl(paths);
    }

    @Override
    public void propertiesDiff(List<String> components) {
        new PropertiesDiffCommand(paths).show(components);
    }

    @Override
    public void changeVersion(String component, String version) {
        propertySetter().setIfNecessary(paths.projectVersionPath(), "project.version", version);

        String env = settingsFile(ConfigsSettings.class, paths.settings().configs()).getSettings().getEnv();
        microConfig(env, paths).generateSingleComponent(component);
        resourcesHashComputer(of(paths.componentsPath() + "/" + component)).computeAll();
    }
}

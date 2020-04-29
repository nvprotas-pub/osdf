package io.microconfig.osdf.state;

import io.microconfig.osdf.openshift.OpenShiftCredentials;
import org.junit.jupiter.api.Test;

import static io.microconfig.osdf.nexus.NexusArtifact.configsNexusArtifact;
import static io.microconfig.osdf.configs.ConfigsSource.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OSDFStateTest {
    @Test
    void checkEmptyState() {
        OSDFState state = new OSDFState();
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutConfigSource() {
        OSDFState state = new OSDFState();
        state.setEnv("dev");
        state.setOpenShiftCredentials(OpenShiftCredentials.of("test:test"));
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutCredentials() {
        OSDFState state = new OSDFState();
        state.setEnv("dev");
        state.setConfigsSource(LOCAL);
        state.setLocalConfigs("/some/path");
        assertFalse(state.check());
    }

    @Test
    void checkBadStateWithoutEnv() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(OpenShiftCredentials.of("test:test"));
        state.setConfigsSource(LOCAL);
        state.setLocalConfigs("/some/path");
        assertFalse(state.check());
    }

    @Test
    void checkStateWithLocalConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(OpenShiftCredentials.of("test:test"));
        state.setConfigsSource(LOCAL);
        state.setLocalConfigs("/some/path");
        state.setEnv("dev");
        assertTrue(state.check());
    }

    @Test
    void checkStateWithGitConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(OpenShiftCredentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigsSource(GIT);
        state.setGitUrl("git.url");
        state.setConfigVersion("master");
        assertTrue(state.check());
    }

    @Test
    void checkStateWithNexusConfigs() {
        OSDFState state = new OSDFState();
        state.setOpenShiftCredentials(OpenShiftCredentials.of("test:test"));
        state.setEnv("dev");
        state.setConfigsSource(NEXUS);
        state.setNexusUrl("nexus.url");
        state.setConfigVersion("1.1.1");
        state.setConfigsNexusArtifact(configsNexusArtifact("group:artifact"));
        assertTrue(state.check());
    }
}
package io.microconfig.osdf.components.checker;

import io.microconfig.osdf.components.DeploymentComponent;
import io.microconfig.osdf.exceptions.OSDFException;
import io.microconfig.osdf.openshift.Pod;
import io.microconfig.osdf.utils.PropertiesUtils;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

import static io.microconfig.osdf.utils.ThreadUtils.sleepSec;
import static io.microconfig.osdf.utils.YamlUtils.getInt;
import static io.microconfig.osdf.utils.YamlUtils.loadFromPath;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.nio.file.Path.of;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
public class LogHealthChecker implements HealthChecker {
    private final String marker;
    private final int timeoutInSec;

    public static LogHealthChecker logHealthChecker(DeploymentComponent component) {
        Map<String, Object> deployProperties = loadFromPath(of(component.getConfigDir() + "/deploy.yaml"));
        Integer timeoutInSec = getInt(deployProperties, "osdf.start.waitSec");

        Properties processProperties = PropertiesUtils.loadFromPath(of(component.getConfigDir() + "/process.properties"));
        String marker = processProperties.getProperty("healthcheck.marker.success");
        if (marker == null) throw new OSDFException("Marker not found for log healthchecker");
        return new LogHealthChecker(marker, ofNullable(timeoutInSec).orElse(30));
    }

    public boolean check(Pod pod) {
        try {
            Process process = getRuntime().exec("oc logs -f " + pod.getName() + " -c " + pod.getComponentName());
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                long startTime = currentTimeMillis();
                StringBuilder logContent = new StringBuilder();
                boolean gotLogs = false;
                while (true) {
                    if (reader.ready()) {
                        gotLogs = true;
                        String str = reader.readLine();
                        logContent.append(str);
                        if (logContent.indexOf(marker) >= 0) return true;
                        if (logContent.length() > marker.length())
                            logContent.delete(0, logContent.length() - marker.length());
                        continue;
                    }
                    if (!gotLogs && calcSecFrom(startTime) > 10) return false;
                    if (calcSecFrom(startTime) > timeoutInSec) return false;
                    sleepSec(1);
                }
            }
        } catch (Exception e) {
            return false;
        }
    }

    private long calcSecFrom(long startTime) {
        return (currentTimeMillis() - startTime) / 1000;
    }
}

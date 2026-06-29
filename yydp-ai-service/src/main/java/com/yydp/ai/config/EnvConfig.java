package com.yydp.ai.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.LinkedHashMap;
import java.util.Map;

public class EnvConfig implements EnvironmentPostProcessor, Ordered {

    private static final String PROPERTY_SOURCE_NAME = "yydpAiDotenv";
    private static final String ENV_FILE_NAME = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Path envDir = findEnvDirectory();
        if (envDir == null) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(envDir.toString())
                .filename(ENV_FILE_NAME)
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        Map<String, Object> values = new LinkedHashMap<>();
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null && System.getProperty(entry.getKey()) == null) {
                values.put(entry.getKey(), entry.getValue());
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });

        if (!values.isEmpty()) {
            environment.getPropertySources().addLast(new MapPropertySource(PROPERTY_SOURCE_NAME, values));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Path findEnvDirectory() {
        String configuredDir = firstNotBlank(System.getProperty("yydp.ai.env-dir"), System.getenv("YYDP_AI_ENV_DIR"));
        Path fromConfiguredDir = resolveIfContainsEnv(configuredDir);
        if (fromConfiguredDir != null) {
            return fromConfiguredDir;
        }

        String userDir = System.getProperty("user.dir");
        Path fromUserDir = resolveIfContainsEnv(userDir);
        if (fromUserDir != null) {
            return fromUserDir;
        }

        Path fromRootUserDir = resolveIfContainsEnv(pathOf(userDir, "yydp-ai-service"));
        if (fromRootUserDir != null) {
            return fromRootUserDir;
        }

        Path fromClasspath = resolveFromClasspath();
        if (fromClasspath != null) {
            return fromClasspath;
        }

        return null;
    }

    private Path resolveFromClasspath() {
        try {
            CodeSource codeSource = EnvConfig.class.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return null;
            }
            URL location = codeSource.getLocation();
            if (location == null) {
                return null;
            }
            Path path = Paths.get(location.toURI()).toAbsolutePath().normalize();
            if (Files.isRegularFile(path)) {
                path = path.getParent();
            }
            Path current = path;
            for (int i = 0; current != null && i < 5; i++) {
                Path found = resolveIfContainsEnv(current.toString());
                if (found != null) {
                    return found;
                }
                current = current.getParent();
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private Path resolveIfContainsEnv(String dir) {
        if (isBlank(dir)) {
            return null;
        }
        try {
            Path path = Paths.get(dir).toAbsolutePath().normalize();
            if (Files.isRegularFile(path)) {
                path = path.getParent();
            }
            if (path != null && Files.isRegularFile(path.resolve(ENV_FILE_NAME))) {
                return path;
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private String pathOf(String parent, String child) {
        if (isBlank(parent)) {
            return null;
        }
        return Paths.get(parent, child).toString();
    }

    private String firstNotBlank(String first, String second) {
        return isBlank(first) ? second : first;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

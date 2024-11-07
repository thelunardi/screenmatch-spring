package br.com.alura.screenmatch.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public final class EnvUtil {
    private static Properties props = new Properties();

    public static String getApiKey() {
        var envFile = Paths.get(".env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
            return props.get("API_KEY").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getOpenAiKey() {
        var envFile = Paths.get(".env");
        try (var inputStream = Files.newInputStream(envFile)) {
            props.load(inputStream);
            return props.get("OPEN_AI_KEY").toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

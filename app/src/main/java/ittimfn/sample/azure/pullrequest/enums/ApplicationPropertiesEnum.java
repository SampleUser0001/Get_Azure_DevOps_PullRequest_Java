package ittimfn.sample.azure.pullrequest.enums;

import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public enum ApplicationPropertiesEnum {
    ORGANIZATION("azure.devops.organization"),
    PROJECT("azure.devops.project.name"),
    USER("azure.devops.user"),
    TOKEN("azure.devops.token");

    private static Properties properties;
    private final String key;
            
    private ApplicationPropertiesEnum(String key) {
    this.key = key;

    }

    public static void load(Path propertiesPath) throws IOException {
        properties = new Properties();
        properties.load(
            Files.newBufferedReader(propertiesPath, StandardCharsets.UTF_8)
        );
    }

    public String getValue() {
        return properties.getProperty(this.key);
    }

}

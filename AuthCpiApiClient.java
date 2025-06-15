// File: src/main/java/com/example/transport/AuthCpiApiClient.java

package com.example.transport;

import java.util.Properties;
import java.io.InputStream;

public class AuthCpiApiClient {

    private Properties props;

    public AuthCpiApiClient() {
        try {
            props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public boolean transport(String pkg, String flow, String env) {
        // Dummy transport logic, should call CPI API with credentials per env
        System.out.println("Transporting iFlow: " + flow + " from package: " + pkg + " to " + env);
        String user = props.getProperty(env + ".username");
        String pass = props.getProperty(env + ".password");
        String url = props.getProperty(env + ".url");

        // Simulate success
        System.out.println("Using credentials: " + user + "@" + url);
        return true;
    }
}

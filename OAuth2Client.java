// File: src/main/java/com/example/transport/OAuth2Client.java

package com.example.transport;

import java.util.Properties;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class OAuth2Client {

    private Properties props;

    public OAuth2Client() {
        try {
            props = new Properties();
            InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties");
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public String getAccessToken(String env) throws Exception {
        String tokenUrl = props.getProperty(env + ".tokenUrl");
        String clientId = props.getProperty(env + ".clientId");
        String clientSecret = props.getProperty(env + ".clientSecret");

        URL url = new URL(tokenUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String payload = "grant_type=client_credentials";
        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes());
        os.flush();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            result.append(line);
        }

        // Parse token from response (simplified)
        String token = result.toString().split("\"access_token\":\"")[1].split("\"")[0];
        return token;
    }
}

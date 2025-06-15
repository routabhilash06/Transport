package com.example.transport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;

public class CPITransportService {

    // Placeholder for GUI use
    public List<String> fetchPackages() {
        return List.of("PackageA", "PackageB", "PackageC");
    }

    // Placeholder for GUI use
    public List<String> fetchIFlows(String packageName) {
        return List.of("iFlow1", "iFlow2", "iFlow3");
    }

    // GUI-compatible method — minimal param version
    public void transportIFlow(String packageName, String iFlowId, String targetEnv) {
        // You'll need to read actual URLs and auth info from config
        String sourceUrl = Config.get("dev.url");
        String targetUrl = Config.get(targetEnv.toLowerCase() + ".url");
        String authType = Config.get("auth.type");
        String tokenOrCreds = Config.get(targetEnv.toLowerCase() + ".credentials");

        boolean success = transportIFlow(sourceUrl, targetUrl, iFlowId, authType, tokenOrCreds);

        if (success) {
            System.out.println("Transport succeeded.");
        } else {
            System.out.println("Transport failed.");
        }
    }

    // Original logic
    public static boolean transportIFlow(String sourceUrl, String targetUrl, String iFlowId, String authType, String tokenOrCredentials) {
        try {
            byte[] iflowContent = exportIFlow(sourceUrl, iFlowId, authType, tokenOrCredentials);
            if (iflowContent == null) {
                System.err.println("Failed to export iFlow.");
                return false;
            }

            return importIFlow(targetUrl, iFlowId, authType, tokenOrCredentials, iflowContent);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] exportIFlow(String sourceUrl, String iFlowId, String authType, String tokenOrCreds) throws IOException {
        URL url = new URL(sourceUrl + "/api/v1/iflows/" + iFlowId + "/$export");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setAuthHeader(conn, authType, tokenOrCreds);

        if (conn.getResponseCode() == 200) {
            return conn.getInputStream().readAllBytes();
        } else {
            System.err.println("Export failed: HTTP " + conn.getResponseCode());
            return null;
        }
    }

    private static boolean importIFlow(String targetUrl, String iFlowId, String authType, String tokenOrCreds, byte[] content) throws IOException {
        URL url = new URL(targetUrl + "/api/v1/iflows/" + iFlowId + "/$import");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/zip");
        setAuthHeader(conn, authType, tokenOrCreds);

        conn.getOutputStream().write(content);

        if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
            System.out.println("iFlow transported successfully.");
            return true;
        } else {
            System.err.println("Import failed: HTTP " + conn.getResponseCode());
            return false;
        }
    }

    private static void setAuthHeader(HttpURLConnection conn, String authType, String tokenOrCreds) {
        if ("OAuth2".equalsIgnoreCase(authType)) {
            conn.setRequestProperty("Authorization", "Bearer " + tokenOrCreds);
        } else if ("Basic".equalsIgnoreCase(authType)) {
            String encoded = Base64.getEncoder().encodeToString(tokenOrCreds.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);
        }
    }
}

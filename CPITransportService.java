package com.example.transport;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class CPITransportService {

    private final String sourceUrl;
    private final String targetUrl;
    private final String authType;
    private final String tokenOrCreds;

    public CPITransportService(String sourceUrl, String targetUrl, String authType, String tokenOrCreds) {
        this.sourceUrl = sourceUrl;
        this.targetUrl = targetUrl;
        this.authType = authType;
        this.tokenOrCreds = tokenOrCreds;
    }

    public List<String> fetchPackages() throws IOException {
        URL url = new URL(sourceUrl + "/api/v1/IntegrationPackages");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setAuthHeader(conn, authType, tokenOrCreds);

        List<String> packages = new ArrayList<>();
        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("d");
            for (int i = 0; i < results.length(); i++) {
                JSONObject pkg = results.getJSONObject(i);
                packages.add(pkg.getString("Name"));
            }
        } else {
            System.err.println("Failed to fetch packages: " + conn.getResponseCode());
        }
        return packages;
    }

    public List<String> fetchIFlows(String packageId) throws IOException {
        URL url = new URL(sourceUrl + "/api/v1/IntegrationPackages('" + packageId + "')/IntegrationDesigntimeArtifacts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        setAuthHeader(conn, authType, tokenOrCreds);

        List<String> iFlows = new ArrayList<>();
        if (conn.getResponseCode() == 200) {
            String response = new String(conn.getInputStream().readAllBytes());
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("d");
            for (int i = 0; i < results.length(); i++) {
                JSONObject flow = results.getJSONObject(i);
                iFlows.add(flow.getString("Name"));
            }
        } else {
            System.err.println("Failed to fetch iFlows: " + conn.getResponseCode());
        }
        return iFlows;
    }

    public boolean transportIFlow(String packageId, String iFlowId, String iFlowName, String sourceUrl, String targetUrl) {
        try {
            byte[] content = exportIFlow(packageId, iFlowId);
            if (content == null) {
                System.err.println("Export failed.");
                return false;
            }

            boolean result = importIFlow(iFlowId, targetUrl, content);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private byte[] exportIFlow(String packageId, String iFlowId) throws IOException {
        URL url = new URL(sourceUrl + "/api/v1/IntegrationDesigntimeArtifacts(Id='" + iFlowId + "',Version='1.0.0')/$value");
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

    private boolean importIFlow(String iFlowId, String targetUrl, byte[] content) throws IOException {
        URL url = new URL(targetUrl + "/api/v1/IntegrationDesigntimeArtifacts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/zip");
        setAuthHeader(conn, authType, tokenOrCreds);

        conn.getOutputStream().write(content);

        int code = conn.getResponseCode();
        if (code == 200 || code == 201) {
            System.out.println("Import success.");
            return true;
        } else {
            System.err.println("Import failed: HTTP " + code);
            return false;
        }
    }

    private void setAuthHeader(HttpURLConnection conn, String authType, String tokenOrCreds) {
        if ("OAuth2".equalsIgnoreCase(authType)) {
            conn.setRequestProperty("Authorization", "Bearer " + tokenOrCreds);
        } else if ("Basic".equalsIgnoreCase(authType)) {
            String encoded = Base64.getEncoder().encodeToString(tokenOrCreds.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encoded);
        }
    }
}

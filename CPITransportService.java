package com.example.transport;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.List;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

public class CPITransportService {

    public List<String> fetchPackages(String sourceUrl, String authType, String tokenOrCredentials) {
        List<String> packages = new ArrayList<>();
        try {
            URL url = new URL(sourceUrl + "/api/v1/IntegrationPackages");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            setAuthHeader(conn, authType, tokenOrCredentials);

            InputStream responseStream = conn.getInputStream();
            String response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("d");

            for (int i = 0; i < results.length(); i++) {
                packages.add(results.getJSONObject(i).getString("Name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packages;
    }

    public List<String> fetchIFlows(String sourceUrl, String packageId, String authType, String tokenOrCredentials) {
        List<String> iflows = new ArrayList<>();
        try {
            URL url = new URL(sourceUrl + "/api/v1/IntegrationPackages('" + packageId + "')/IntegrationDesigntimeArtifacts");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            setAuthHeader(conn, authType, tokenOrCredentials);

            InputStream responseStream = conn.getInputStream();
            String response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
            JSONObject json = new JSONObject(response);
            JSONArray results = json.getJSONArray("d");

            for (int i = 0; i < results.length(); i++) {
                iflows.add(results.getJSONObject(i).getString("Id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return iflows;
    }

    public static boolean transportIFlow(String sourceUrl, String targetUrl, String iFlowId, String authType, String tokenOrCredentials) {
        try {
            byte[] iflowContent = exportIFlow(sourceUrl, iFlowId, authType, tokenOrCredentials);
            if (iflowContent == null) {
                System.err.println("Failed to export iFlow.");
                return false;
            }

            boolean result = importIFlow(targetUrl, iFlowId, authType, tokenOrCredentials, iflowContent);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static byte[] exportIFlow(String sourceUrl, String iFlowId, String authType, String tokenOrCreds) throws IOException {
        URL

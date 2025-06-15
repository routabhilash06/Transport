// File: src/main/java/com/example/transport/CPITransportService.java

package com.example.transport;

import java.util.Arrays;
import java.util.List;

public class CPITransportService {

    private AuthCpiApiClient apiClient;

    public CPITransportService() {
        apiClient = new AuthCpiApiClient();
    }

    public List<String> fetchPackages() {
        // Dummy list for GUI dropdown. Replace with actual API call.
        return Arrays.asList("com.example.sales", "com.example.procurement");
    }

    public List<String> fetchIFlows(String packageName) {
        // Dummy list based

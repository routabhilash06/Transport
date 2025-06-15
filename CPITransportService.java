package com.example.transport;

import java.util.Arrays;
import java.util.List;

public class CPITransportService {

    // Dummy method: Replace with actual logic if needed
    public List<String> fetchPackages() {
        return Arrays.asList("PackageA", "PackageB", "PackageC");
    }

    // Dummy method: Replace with actual logic if needed
    public List<String> fetchIFlows(String packageId) {
        return Arrays.asList("IFlow1", "IFlow2", "IFlow3");
    }

    // This version matches what TransportToolUI is calling
    public boolean transportIFlow(String sourceEnv, String targetEnv, String iFlowName) {
        System.out.println("Transporting " + iFlowName + " from " + sourceEnv + " to " + targetEnv);
        return true;
    }
}

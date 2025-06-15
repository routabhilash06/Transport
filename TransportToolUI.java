// File: src/main/java/com/example/transport/TransportToolUI.java

package com.example.transport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class TransportToolUI extends JFrame {

    private JComboBox<String> packageDropdown;
    private JComboBox<String> iflowDropdown;
    private JComboBox<String> envDropdown;
    private JButton transportButton;
    private CPITransportService transportService;

    public TransportToolUI() {
        super("CPI Transport Tool");
        transportService = new CPITransportService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new GridLayout(5, 1, 10, 10));
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        packageDropdown = new JComboBox<>();
        iflowDropdown = new JComboBox<>();
        envDropdown = new JComboBox<>(new String[]{"QA", "PROD"});
        transportButton = new JButton("Transport");

        add(new JLabel("Select Package:"));
        add(packageDropdown);
        add(new JLabel("Select iFlow:"));
        add(iflowDropdown);
        add(new JLabel("Select Environment:"));
        add(envDropdown);
        add(transportButton);

        // Load packages on startup
        List<String> packages = transportService.fetchPackages();
        for (String pkg : packages) {
            packageDropdown.addItem(pkg);
        }

        // Load iFlows when a package is selected
        packageDropdown.addActionListener((ActionEvent e) -> {
            String selectedPackage = (String) packageDropdown.getSelectedItem();
            List<String> iflows = transportService.fetchIFlows(selectedPackage);
            iflowDropdown.removeAllItems();
            for (String flow : iflows) {
                iflowDropdown.addItem(flow);
            }
        });

        // Transport button action
        transportButton.addActionListener((ActionEvent e) -> {
            String pkg = (String) packageDropdown.getSelectedItem();
            String iflow = (String) iflowDropdown.getSelectedItem();
            String env = (String) envDropdown.getSelectedItem();

            boolean result = transportService.transportIFlow(pkg, iflow, env);
            JOptionPane.showMessageDialog(this,
                    result ? "Transport successful!" : "Transport failed.",
                    "Status", result ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TransportToolUI::new);
    }
}

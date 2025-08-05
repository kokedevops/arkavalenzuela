package com.arka.hello.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;

@RestController
@RequestMapping("/")
public class HelloController {

    @Value("${server.port}")
    private String port;

    @GetMapping
    public String hello() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        return String.format("Hello World from %s on port %s!", hostName, port);
    }

    @GetMapping("/info")
    public HelloInfo getInfo() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        return new HelloInfo(
            "Hello World Service",
            "1.0.0",
            hostName,
            port,
            System.currentTimeMillis()
        );
    }

    @GetMapping("/health")
    public String health() throws UnknownHostException {
        String hostName = InetAddress.getLocalHost().getHostName();
        return String.format("Hello World Service is UP! Running on %s:%s", hostName, port);
    }

    public static class HelloInfo {
        private String serviceName;
        private String version;
        private String hostname;
        private String port;
        private long timestamp;

        public HelloInfo(String serviceName, String version, String hostname, String port, long timestamp) {
            this.serviceName = serviceName;
            this.version = version;
            this.hostname = hostname;
            this.port = port;
            this.timestamp = timestamp;
        }

        // Getters
        public String getServiceName() { return serviceName; }
        public String getVersion() { return version; }
        public String getHostname() { return hostname; }
        public String getPort() { return port; }
        public long getTimestamp() { return timestamp; }

        // Setters
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        public void setVersion(String version) { this.version = version; }
        public void setHostname(String hostname) { this.hostname = hostname; }
        public void setPort(String port) { this.port = port; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}

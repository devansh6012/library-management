package com.example.librarymanagement;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "library")
public class LibraryConfigProperties {
    private String name;
    private int maxBooksPerUser;
    private double lateFeePerDay;
    private boolean enableNotifications = true;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public int getMaxBooksPerUser() { return maxBooksPerUser; }

    public void setMaxBooksPerUser(int maxBooksPerUser) {
        this.maxBooksPerUser = maxBooksPerUser;
    }

    public double getLateFeePerDay() {
        return lateFeePerDay;
    }

    public void setLateFeePerDay(double lateFeePerDay) {
        this.lateFeePerDay = lateFeePerDay;
    }

    public boolean isEnableNotifications() {
        return enableNotifications;
    }

    public void setEnableNotifications(boolean enableNotifications) {
        this.enableNotifications = enableNotifications;
    }
}

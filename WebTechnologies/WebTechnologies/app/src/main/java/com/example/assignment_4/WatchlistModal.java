package com.example.assignment_4;

import java.text.DecimalFormat;

public class WatchlistModal {
    private watchProfile profile;
    private watchQuote quote;

    public WatchlistModal(watchProfile profile, watchQuote quote) {
        this.profile = profile;
        this.quote = quote;
    }

    public watchProfile getProfile() {
        return profile;
    }

    public watchQuote getQuote() {
        return quote;
    }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }
// Constructor, getters, and setters
}

class watchProfile{
    private String ticker;
    private String name;

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public watchProfile(String ticker, String name) {
        this.ticker = ticker;
        this.name = name;
    }
}

class watchQuote {
    private double c; // current price
    private double d; // change
    private double dp; // change percentage

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
    }

    public double getDp() {
        return dp;
    }

    // Constructors, getters, and setters
    public watchQuote(double c, double d, double dp) {
        this.c = c;
        this.d = d;
        this.dp = dp;
    }
}

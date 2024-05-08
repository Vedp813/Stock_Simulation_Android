package com.example.assignment_4;

import java.text.DecimalFormat;

public class PortfolioModal {
        private String ticker;
        private String name;
        private int quantity;
        private double totalCost;

    public PortfolioModal(String ticker, String name, int quantity, double totalCost, portQuote quote) {
        this.ticker = ticker;
        this.name = name;
        this.quantity = quantity;
        this.totalCost = totalCost;
        this.quote = quote;
    }

    private portQuote quote;

    public String getTicker() {
        return ticker;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public portQuote getQuote() {
        return quote;
    }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }
// Constructor, getters, and setters
}

class portQuote {
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
    public portQuote(double c, double d, double dp) {
        this.c = c;
        this.d = d;
        this.dp = dp;
    }
}



package com.example.assignment_4;

import android.webkit.JavascriptInterface;

public class CDataHelper {
     String ticker;

    public CDataHelper(String ticker) {
        this.ticker =  ticker;
    }

    @JavascriptInterface
    public String getTicker() {
        return ticker;
    }
}

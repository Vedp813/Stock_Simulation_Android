package com.example.assignment_4;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class StockInfo extends AppCompatActivity {
    private ApiStockHelper apiStockHelper;
    private JSONObject stockData;
    private ProgressBar progressBar;
    private NestedScrollView stockDataView;
    private NewsAdapter adapterNews;
    public List<NewsModal> newsList = new ArrayList<>();
    public List<String> peerList = new ArrayList<>();
    public RecyclerView recyclerViewNews;
    private WebView chartarea;
    private WebView sbschart;
    private WebView epschart;
    private TabLayout chartTab;
    private ApiHelper apiHelper;
    private RecyclerView peerRV;
    private PeerStocksAdapter peerStocksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_info);
        Toolbar toolbar = findViewById(R.id.toolbarstock);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("ticker"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Content to be visible for this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.stockMain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //        Navigation Bar getting black and icons white
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        apiStockHelper = new ApiStockHelper(this);
        String ticker = getIntent().getStringExtra("ticker");
        progressBar = findViewById(R.id.progressBar);
        stockDataView = findViewById(R.id.stockDataView);
        chartarea = findViewById(R.id.HourlyChart);
        sbschart = findViewById(R.id.SBSChart);
        epschart = findViewById(R.id.EPSChart);
        chartTab = findViewById(R.id.ChartTab);
        peerRV = findViewById(R.id.peerRecyclerView);
        peerRV.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        peerStocksAdapter = new PeerStocksAdapter(peerList, this);

        chartTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    showHourChart();
                } else {
                    showHistChart();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fetchData(ticker);

        recyclerViewNews = findViewById(R.id.NewsView);
        adapterNews = new NewsAdapter(newsList, this);

    }

    private void fetchData(String ticker) {
        String url = "/search/" + ticker;
        apiStockHelper.fetchStockData(url, new ApiStockHelper.ApiResponseListenerStock() {
            @Override
            public void onSuccess(JSONObject response) {
                stockData = response;
                Log.d("StockData", stockData.toString());
                progressBar.setVisibility(View.GONE);
                stockDataView.setVisibility(View.VISIBLE);
                firstSection(stockData);
                try {
                    aboutSection(stockData.getJSONObject("0"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
//                fetchPortfolio();
//                portfolioSection(stockData);
                try {
                    JSONObject quoteData = stockData.getJSONObject("1");
                    quoteSection(quoteData);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                try {
                    sentiments(stockData.getJSONObject("5"));
                } catch (JSONException e){
                    throw new RuntimeException(e);
                }

                try {
                    JSONArray peerArray = stockData.getJSONArray("2");

                    // Convert the JSON array to a List<String>
                    for (int i = 0; i < peerArray.length(); i++) {
                        peerList.add(peerArray.getString(i));
                    }
                    peerRV.setAdapter(peerStocksAdapter );
                    Log.d("PEER", ""+peerList);
                } catch (JSONException e){
                    throw new RuntimeException(e);
                }
                showHourChart();
                showSBSChart();
                showEPSChart();
                try {
                    JSONArray newsData = stockData.getJSONArray("3");
                    Log.d("NewsData",newsData.toString());
                    for (int i = 0; i < newsData.length(); i++) {
                        JSONObject data = newsData.getJSONObject(i);
                        String image = data.getString("image");
                        String newsSource = data.getString("source");
                        long newsTime = data.getLong("datetime");
                        String newsText = data.getString("headline");
                        String newsSummary = data.getString("summary");
                        String newsUrl = data.getString("url");
                        newsList.add(new NewsModal(image,newsSource,newsTime,newsText,newsSummary,newsUrl));
                    }
                    Log.d("NEWS LIST", ""+newsList);
                    recyclerViewNews.setAdapter(adapterNews);
                    recyclerViewNews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                    adapterNews.notifyDataSetChanged();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Error ", error);
            }
        });
    }
    public List<PortfolioModal> portfolioData = new ArrayList<>();

    private void fetchPortfolio() {
        String url = "/portfolio/get";
        apiHelper.fetchData(url, new ApiHelper.ApiResponseListener() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        String ticker = data.getString("ticker");
                        String name = data.getString("name");
                        int quantity = data.getInt("quantity");
                        double totcost = data.getDouble("totalCost");
                        JSONObject quoteData = data.getJSONObject("quote");
                        double c = quoteData.getDouble("c");
                        double d = quoteData.getDouble("d");
                        double dp = quoteData.getDouble("dp");
                        portQuote quote = new portQuote(c, d, dp);

                        portfolioData.add(new PortfolioModal(ticker, name, quantity, totcost, quote));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Error ",error);
            }
        });
    }

    private TextView tickerName;
    private TextView tickerFullName;
    private TextView tickerPrice;
    private TextView tickerChangePrice;

    public void firstSection(JSONObject stockData) {
        tickerName = findViewById(R.id.textTickerStock);
        tickerFullName = findViewById(R.id.textTickerFullName);
        tickerPrice = findViewById(R.id.textTickerPrice);
        tickerChangePrice = findViewById(R.id.textTickerChange);

        try {
            JSONObject nameData = stockData.getJSONObject("0");
            JSONObject quoteData = stockData.getJSONObject("1");
            tickerName.setText(nameData.getString("ticker"));
            tickerFullName.setText(nameData.getString("name"));
            tickerPrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("c"))));
            tickerChangePrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("d"))) + "(" + Double.toString(formatDouble(quoteData.getDouble("dp"))) + "%)");

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private TextView sharesOwned;
    private TextView avgCost;
    private TextView totalCost;
    private TextView change;
    private TextView marketValue;


    public void portfolioSection(JSONObject stockData) {

        sharesOwned = findViewById(R.id.textView7);
        avgCost = findViewById(R.id.textView8);
        totalCost = findViewById(R.id.textView9);
        change = findViewById(R.id.textView50);
        marketValue = findViewById(R.id.textView11);

        try {
            Boolean share = stockData.getBoolean("portfolio");
            JSONObject nameData = stockData.getJSONObject("0");
            JSONObject quoteData = stockData.getJSONObject("1");
            String searchedTicker = nameData.getString("ticker");
            for (PortfolioModal portfolioItem : portfolioData) {
                if (portfolioItem.getTicker().equals(searchedTicker)) {
                    sharesOwned.setText(String.valueOf(portfolioItem.getQuantity()));
                    totalCost.setText(String.valueOf(portfolioItem.getTotalCost()));
                    int quantity = portfolioItem.getQuantity();
                    double totalCost = portfolioItem.getTotalCost();
                    double avgCostValue = (quantity != 0) ? (totalCost / quantity) : 0.0;
                    avgCost.setText(String.valueOf(avgCostValue));
                    marketValue.setText("$" + Double.toString(formatDouble(quoteData.getDouble("c"))));
                    double currentMarketValue = portfolioItem.getQuote().getC();
                    double avgMarketValue = (quantity != 0) ? (totalCost / quantity) : 0.0;
                    double changeValue = currentMarketValue - avgMarketValue;
                    change.setText("$ "+String.valueOf(changeValue));
                    break; // No need to continue loop once the ticker is found
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private TextView openPrice;
    private TextView prevClosePrice;
    private TextView highPrice;
    private TextView lowPrice;

    public void quoteSection(JSONObject quoteData){
        openPrice = findViewById(R.id.textView15);
        lowPrice = findViewById(R.id.textView16);
        highPrice = findViewById(R.id.textView19);
        prevClosePrice = findViewById(R.id.textView20);

        try {
            openPrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("o"))));
            lowPrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("l"))));
            highPrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("h"))));
            prevClosePrice.setText("$" + Double.toString(formatDouble(quoteData.getDouble("pc"))));
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    public TextView ipoDate;
    public TextView industry;
    public TextView webPage;
    public void aboutSection(JSONObject aboutData){
        ipoDate = findViewById(R.id.textView26);
        industry = findViewById(R.id.textView27);
        webPage = findViewById(R.id.textView28);

        try {
            ipoDate.setText(aboutData.getString("ipo"));
            Log.d("ABOUT DATA", aboutData.toString());
            industry.setText(aboutData.getString("finnhubIndustry"));
            Log.d("FINNHUB INDUSTRY",aboutData.getString("finnhubIndustry").toString());
            Log.d("WEBURL",aboutData.getString("weburl").toString());
            webPage.setText(aboutData.getString("weburl"));
        } catch (JSONException e){
            throw new RuntimeException(e);
        }
    }

    private TextView totMSRP;
    private TextView posMSRP;
    private TextView negMSRP;
    private TextView totChange;
    private TextView posChange;
    private TextView negChange;
    public void sentiments(JSONObject sentimentsData){
        totMSRP = findViewById(R.id.textView35);
        posMSRP = findViewById(R.id.textView38);
        negMSRP = findViewById(R.id.textView41);
        totChange = findViewById(R.id.textView36);
        posChange = findViewById(R.id.textView39);
        negChange = findViewById(R.id.textView42);

        try {
            JSONArray dataArray = sentimentsData.getJSONArray("data");

            // Initialize variables to hold total, positive, and negative values for mspr and change
            double totalMspr = 0;
            double totalChange = 0;
            double positiveMspr = 0;
            double positiveChange = 0;
            double negativeMspr = 0;
            double negativeChange = 0;

            // Iterate through the data array
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject item = dataArray.getJSONObject(i);
                double mspr = item.getDouble("mspr");
                double change = item.getDouble("change");

                // Accumulate total, positive, and negative values for mspr
                totalMspr += mspr;
                if (mspr > 0) {
                    positiveMspr += mspr;
                } else {
                    negativeMspr -= mspr;
                }

                // Accumulate total, positive, and negative values for change
                totalChange += change;
                if (change > 0) {
                    positiveChange += change;
                } else {
                    negativeChange -= change;
                }
            }

            // Print the results
            totMSRP.setText(String.valueOf(totalMspr));
            posMSRP.setText(String.valueOf(positiveMspr));
            negMSRP.setText(String.valueOf(negativeMspr));
            totChange.setText(String.valueOf(totalChange));
            posChange.setText(String.valueOf(positiveChange));
            negChange.setText(String.valueOf(negativeChange));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }

    private void showHourChart(){
        WebSettings websettings = chartarea.getSettings();
        websettings.setJavaScriptEnabled(true);
        chartarea.clearCache(true);
        CDataHelper chart = new CDataHelper((getIntent().getStringExtra("ticker")));
        chartarea.addJavascriptInterface(chart, "CDataHelper");
        chartarea.loadUrl("file:///android_asset/Hourly.HTML");
    }

    private void showHistChart(){
        WebSettings websettings = chartarea.getSettings();
        websettings.setJavaScriptEnabled(true);
        chartarea.clearCache(true);
        CDataHelper chart = new CDataHelper((getIntent().getStringExtra("ticker")));
        chartarea.addJavascriptInterface(chart, "CDataHelper");
        chartarea.loadUrl("file:///android_asset/MainChart.HTML");
    }

    private void showSBSChart(){
        WebSettings websettings = sbschart.getSettings();
        websettings.setJavaScriptEnabled(true);
        sbschart.clearCache(true);
        CDataHelper chart = new CDataHelper((getIntent().getStringExtra("ticker")));
        sbschart.addJavascriptInterface(chart, "CDataHelper");
        sbschart.loadUrl("file:///android_asset/SBSChart.HTML");
    }

    private void showEPSChart(){
        WebSettings websettings = epschart.getSettings();
        websettings.setJavaScriptEnabled(true);
        epschart.clearCache(true);
        CDataHelper chart = new CDataHelper((getIntent().getStringExtra("ticker")));
        epschart.addJavascriptInterface(chart, "CDataHelper");
        epschart.loadUrl("file:///android_asset/EPSChart.HTML");
    }

}
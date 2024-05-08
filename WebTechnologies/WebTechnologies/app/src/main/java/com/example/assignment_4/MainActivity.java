package com.example.assignment_4;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private TextView textDate;
    private TextView finnhubLink;
    private TextView textmoney;
    private ProgressBar progressBar;
    private LinearLayout contentLayout;
    private boolean isDataLoaded = true;
//    Fetching Data from server
    private ApiHelper apiHelper;
    private Menu menu;
    private String searchText;
    private double moneyData;
    private Double money;
    private MyAdapter adapterPort;
    public List<PortfolioModal> portfolioData = new ArrayList<>();
    public RecyclerView recyclerViewPort;
    private WatchAdapter adapterWatch;
    public List<WatchlistModal> watchlistData = new ArrayList<>();
    public RecyclerView recyclerViewWatch;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Creating activity main
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Declaring variables and given linearLayout as disabled as of the start
        progressBar = findViewById(R.id.progressBar);
        contentLayout = findViewById(R.id.linearLayout);
        // Initialize TextView
        textDate = findViewById(R.id.textDate);
        finnhubLink = findViewById(R.id.finnhublink);
        finnhubLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToFinnHub = new Intent(Intent.ACTION_VIEW);
                goToFinnHub.setData(Uri.parse("https://finnhub.io"));
                startActivity(goToFinnHub);
            }
        });

        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        // Set current date to the TextView
        textDate.setText(currentDate);

//        Navigation Bar getting black and icons white
        WindowCompat.setDecorFitsSystemWindows(getWindow(),false);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

//        Content to be visible for this
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiHelper = new ApiHelper(this);
        fetchMoney();
        fetchPortfolio();
        fetchWatchlist();

        recyclerViewPort = findViewById(R.id.recycler_view_portfolio);
        adapterPort = new MyAdapter(portfolioData, getApplicationContext(), (MyAdapter.OnItemClickListener) this::onItemClickPort);

        recyclerViewWatch = findViewById(R.id.recycler_view_favorites);
        adapterWatch = new WatchAdapter(watchlistData, getApplicationContext(), (WatchAdapter.OnItemClickListener) this::onItemClickFav);



        ItemTouchHelper.SimpleCallback simpleCallbackFav = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                Collections.swap(watchlistData, fromPosition, toPosition);
                adapterWatch.notifyItemMoved(fromPosition, toPosition);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getLayoutPosition();

                if (direction == ItemTouchHelper.LEFT) {
//                    volleySingleton.postData("api/watchlist/delete", watchlistData.get(position).getTicker(), new VolleySingleton.VolleyCallback() {
//                        @Override
//                        public void onSuccess(JSONObject response) {
//                            watchlistData.remove(position);
//                            adapter.notifyItemRemoved(position);
//                            Toast.makeText(getApplicationContext(),
//                                    "  favorite removed successfully",
//                                    Toast.LENGTH_LONG).show();
//                            Log.d("RESPONSE in fav delete", ""+response);
//                        }
//
//                        @Override
//                        public void onError(String errorMessage) {
//                            Log.e("Error in fetching stock data", ""+errorMessage);
//                        }
//                    });
                    watchlistData.remove(position);
                    adapterWatch.notifyItemRemoved(position);
                    Toast.makeText(getApplicationContext(),
                            "  favorite removed successfully",
                            Toast.LENGTH_LONG).show();
                } else {
                    throw new IllegalStateException("Unexpected value: " + direction);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.watchList))
                        .addSwipeLeftActionIcon(R.drawable.delete)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

            }
        };

        ItemTouchHelper itemTouchHelperFav = new ItemTouchHelper(simpleCallbackFav);
        itemTouchHelperFav.attachToRecyclerView(recyclerViewWatch);
    }

    public void onItemClickPort(String symbol) {
        Intent intent = new Intent(this, StockInfo.class);
        intent.putExtra("ticker", symbol);
        startActivity(intent);
    }

    public void onItemClickFav(String symbol) {
        Intent intent = new Intent(this, StockInfo.class);
        intent.putExtra("ticker", symbol);
        startActivity(intent);
    }

//    Fetching money
    private void fetchMoney() {
        String url = "/money";
        apiHelper.fetchData(url, new ApiHelper.ApiResponseListener() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    JSONObject data = response.getJSONObject(0);
                    Log.d("MoNEY", data.getString("money"));
                    moneyData = data.getDouble("money");
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    money = Double.parseDouble(decimalFormat.format(moneyData));
//                    updateUIWithData();
                    textmoney = findViewById(R.id.textMoney);
                    textmoney.setText(money.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("Error ",error);
            }
        });
    }

//    As per name fetching the portfolio
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

                        portfolioData.add(new PortfolioModal(ticker, name,quantity,totcost,quote));
                    }
                    recyclerViewPort.setAdapter(adapterPort);
                    recyclerViewPort.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapterPort.notifyDataSetChanged();
                    isDataLoaded = true;
                    updateUIWithData();
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

    private void fetchWatchlist() {
        String url = "/watchlist/get";
        apiHelper.fetchData(url, new ApiHelper.ApiResponseListener() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject data = response.getJSONObject(i);
                        JSONObject profileData = data.getJSONObject("profile");
                        String ticker = profileData.getString("ticker");
                        String name = profileData.getString("name");
                        watchProfile profile = new watchProfile(ticker, name);
                        JSONObject quoteData = data.getJSONObject("quote");
                        double c = quoteData.getDouble("c");
                        double d = quoteData.getDouble("d");
                        double dp = quoteData.getDouble("dp");
                        watchQuote quote = new watchQuote(c, d, dp);
                        watchlistData.add(new WatchlistModal(profile,quote));
                    }
                    recyclerViewWatch.setAdapter(adapterWatch);
                    recyclerViewWatch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    adapterWatch.notifyDataSetChanged();
                    isDataLoaded = true;
                    updateUIWithData();
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

//    This is updating the UI Once the data is there on this page
    private void updateUIWithData() {
                // Hide progress bar and show content layout
                progressBar.setVisibility(View.GONE);
                contentLayout.setVisibility(View.VISIBLE);
                MenuItem searchItem = menu.findItem(R.id.action_search);
                searchItem.setVisible(true);
    }


// Search Button
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    this.menu = menu;
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    @SuppressLint("RestrictedApi") final SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

    // Set background color for dropdown
    searchAutoComplete.setDropDownBackgroundResource(R.color.navColor);

    // Create suggestions adapter
    ArrayList<String> suggestionsArr = new ArrayList<>();
    final ArrayAdapter<String> suggestionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestionsArr);

    // Set item click listener for SearchAutoComplete
    searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String queryString = (String) parent.getItemAtPosition(position);
            String symbol = queryString.split(" | ")[0];
            searchAutoComplete.setText(symbol);
            Intent intent = new Intent(MainActivity.this, StockInfo.class);
            intent.putExtra("ticker", symbol);
            startActivity(intent);
        }
    });

    // Create API helper instance
    final ApiHelper apiHelper = new ApiHelper(this);

    // Create handler and runnable for delayed API call
    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!searchAutoComplete.getText().toString().isEmpty()) {
                Log.d("AUTOCOMPLETE", "" +
                        searchAutoComplete.getText().toString());
                apiHelper.fetchData("/api/" + searchAutoComplete.getText().toString(), new ApiHelper.ApiResponseListener() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        try {
                            Log.d("AUTOCOMPLETE", "" +
                                    String.valueOf(response));
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject itemObj = response.getJSONObject(i);
                                String ticker = itemObj.getString("displaySymbol");
                                String desc = itemObj.getString("description");
                                String temp = ticker + " | " + desc;
                                suggestionsArr.add(temp);
                            }
                            Log.d("AUTOCOMPLETE",String.valueOf(suggestionsArr));
                            searchAutoComplete.setAdapter(suggestionsAdapter);
                            searchAutoComplete.showDropDown();
                        } catch (JSONException error) {
                            Log.e("ERROR in onchange fetch", String.valueOf(error));
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Handle error if needed
                    }
                });
            }
        }
    };

    // Set up text change listener for SearchView
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            Log.d("ON SUBMIT", query);
            Intent intent = new Intent(MainActivity.this, StockInfo.class);
            intent.putExtra("ticker", query);
            startActivity(intent);
            return true;
        }

        @Override
        public boolean onQueryTextChange(final String newText) {
            Log.d("running on Change", newText);
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000);
            return true;
        }
    });

    return super.onCreateOptionsMenu(menu);
}


    // Method to navigate to the next activity and pass the search query
//    private void navigateToNextActivity(String query) {
//        Intent intent = new Intent(MainActivity.this, StockInfo.class);
//        intent.putExtra("ticker", query);
//        startActivity(intent);
//    }



//    private void fetchStockData(String tickerSymbol) {
//        String url = "search/" + tickerSymbol;
//        apiHelper.fetchData(url, new ApiHelper.ApiResponseListener() {
//            @Override
//            public void onSuccess(JSONArray response) {
//                try {
//                    JSONObject stockData = response.getJSONObject(0);
//                    Log.d("StockData", stockData.toString());
//                    navigateToNextActivity(stockData.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                Log.e("Error", error);
//            }
//        });
//    }

}
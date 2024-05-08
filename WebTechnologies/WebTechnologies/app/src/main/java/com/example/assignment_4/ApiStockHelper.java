package com.example.assignment_4;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class ApiStockHelper {
    private static final String baseUrl = "https://assignment3-webtech-csci571.wl.r.appspot.com"; // Log tag
    private Context context;
    private RequestQueue requestQueue;
    public ApiStockHelper(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void fetchStockData(String endpoint, final ApiStockHelper.ApiResponseListenerStock listener) {
        // Construct the complete URL by appending the endpoint to the base URL
        String url = baseUrl + endpoint;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, // Passing null as the request body for GET request
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onError(error.toString());
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }

    public interface ApiResponseListenerStock{
        void onSuccess(JSONObject response);

        void onError(String error);
    }
}

package com.example.assignment_4;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApiHelper {

    private static final String baseUrl = "https://assignment3-webtech-csci571.wl.r.appspot.com/"; // Log tag
    private Context context;
    private RequestQueue requestQueue;


    public ApiHelper(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void fetchData(String endpoint, final ApiResponseListener listener) {
        // Construct the complete URL by appending the endpoint to the base URL
        String url = baseUrl + endpoint;

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, // Passing null as the request body for GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
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

    public interface ApiResponseListener {
        void onSuccess(JSONArray response);
        void onError(String error);
    }
}



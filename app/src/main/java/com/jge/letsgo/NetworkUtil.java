package com.jge.letsgo;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jge.letsgo.models.GoLocation;
import com.jge.letsgo.ui.main.ListFragment;
import com.jge.letsgo.ui.main.MapsFragment;
import com.jge.letsgo.ui.main.PlaceholderFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;

import static com.jge.letsgo.ui.main.PlaceholderFragment.BASE_URL;

public class NetworkUtil {

    public static PlaceholderFragment doNetWorkCall(final Context context, final int index){
        final PlaceholderFragment[] fragment = new PlaceholderFragment[1];
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if(index == 1){
                    fragment[0] = ListFragment.newInstance(index);
                }else if(index == 2){
                    fragment[0] = MapsFragment.newInstance(index);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage();
            }
        });
        queue.add(jsonArrayRequest);
        return fragment[0];
    }

    private static ArrayList<GoLocation> gsonMap(JSONArray response){
        ArrayList listOfLocations = new ArrayList();
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        if (response.length() > 0){
            return new ArrayList<>(Arrays.asList(gson.fromJson(response.toString(), GoLocation[].class)));
        }else{
            return listOfLocations;
        }
    }

    private static void showErrorMessage(){

    }
}

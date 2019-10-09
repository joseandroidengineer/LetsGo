package com.jge.letsgo.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jge.letsgo.AppExecutors;
import com.jge.letsgo.R;
import com.jge.letsgo.adapters.GoListAdapter;
import com.jge.letsgo.database.AppDatabase;
import com.jge.letsgo.database.GoLocationPreference;
import com.jge.letsgo.models.GoLocation;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListFragment extends PlaceholderFragment {
    private List<GoLocation> listOfLocations;
    private RecyclerView recyclerView;
    private GoListAdapter goListAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMessageView;
    private AppDatabase mGoLocationDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.list_fragment, container, false);
        initViews(root);
        mGoLocationDatabase = AppDatabase.getInstance(getContext());
        if(GoLocationPreference.getPreferenceNetworkLoaded(getContext())){
            FragmentViewModel viewModel = ViewModelProviders.of(this).get(FragmentViewModel.class);
            viewModel.getGoLocations().observe(this, new Observer<List<GoLocation>>() {
                @Override
                public void onChanged(List<GoLocation> goLocations) {
                    listOfLocations = new ArrayList<>(goLocations);
                    setUpRecyclerView();
                    showContent();
                    Toast.makeText(getContext(), "Loaded from database", Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            showLoading();
            doNetWorkCall();
            Toast.makeText(getContext(), "Loaded from network", Toast.LENGTH_SHORT).show();
        }
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public static ListFragment newInstance(int index) {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        fragment.setArguments(args);
        args.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    private void setUpRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        goListAdapter = new GoListAdapter(listOfLocations);
        recyclerView.setAdapter(goListAdapter);
    }

    private void doNetWorkCall(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                gsonMap(response);
                setUpRecyclerView();
                showContent();
                GoLocationPreference.savePreferenceNetworkLoaded(getContext(), true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage();
            }
        });
        queue.add(jsonArrayRequest);
    }
    private void gsonMap(JSONArray response){
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        if (response.length() > 0){
            listOfLocations = Arrays.asList(gson.fromJson(response.toString(), GoLocation[].class));
        }

    }

    private void initViews(View root){
        recyclerView = root.findViewById(R.id.recycler_view);
        mProgressBar = root.findViewById(R.id.progress_bar);
        mErrorMessageView = root.findViewById(R.id.error_message);
    }

    private void showLoading(){
        mProgressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        mErrorMessageView.setVisibility(View.GONE);
    }
    private void showContent(){
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        mErrorMessageView.setVisibility(View.GONE);
    }
    private void showErrorMessage(){
        mProgressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        mErrorMessageView.setVisibility(View.VISIBLE);
    }
}

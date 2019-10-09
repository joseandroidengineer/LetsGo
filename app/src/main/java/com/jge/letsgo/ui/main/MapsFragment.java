package com.jge.letsgo.ui.main;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import com.jge.letsgo.database.AppDatabase;
import com.jge.letsgo.database.GoLocationPreference;
import com.jge.letsgo.models.GoLocation;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class MapsFragment extends PlaceholderFragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, PermissionsListener {
    private static final String MAP_KEY = "pk.eyJ1IjoiamVzY2ExNTM0MSIsImEiOiJjazFmYzJvbGIwNDNrM2NwY2d3ZnloNzlrIn0.vHK1KTyMiHzOLd6WPkBQDA";
    private static final String MARKER_SOURCE = "markers-source";
    private static final String MARKER_STYLE_LAYER = "markers-style-layer";
    private static final String MARKER_IMAGE = "custom-marker";
    private AppDatabase mGoLocationDatabase;
    private MapboxMap mapboxMap;
    private List<LatLng> listOfLatLngs;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationListeningCallback locationListeningCallback;
    private LocationComponent locationComponent;
    private MapView mapView;


    private List<GoLocation> listOfLocations;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(),MAP_KEY);
        permissionsManager = new PermissionsManager(this);
        mGoLocationDatabase = AppDatabase.getInstance(getContext());
        View root = inflater.inflate(R.layout.map_fragment, container, false);
        mapView = root.findViewById(R.id.mapView);
        if(PermissionsManager.areLocationPermissionsGranted(getContext())){
            setUpLocationEngineAndCallBack();
            onMapReady(mapboxMap);
        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
        if(GoLocationPreference.getPreferenceNetworkLoaded(getContext())){
            FragmentViewModel viewModel = ViewModelProviders.of(this).get(FragmentViewModel.class);
            viewModel.getGoLocations().observe(this, new Observer<List<GoLocation>>() {
                @Override
                public void onChanged(List<GoLocation> goLocations) {
                    listOfLocations = new ArrayList<>(goLocations);
                }
            });
        }else{
            doNetWorkCall();
        }
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void doNetWorkCall(){
        RequestQueue queue = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, BASE_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                gsonMap(response);
                GoLocationPreference.savePreferenceNetworkLoaded(getContext(), true);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        for(GoLocation goLocation: listOfLocations){
                            mGoLocationDatabase.goLocationDao().insertGoLocation(goLocation);
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    private void addMarkers(@NonNull Style loadedMapStyle) {
        List<Feature> features = new ArrayList<>();
        listOfLatLngs = new ArrayList<>();
        for(GoLocation goLocation: listOfLocations){
            features.add(Feature.fromGeometry(Point.fromLngLat(goLocation.longitude,goLocation.latitude)));
            LatLng latLng = new LatLng();
            latLng.setLatitude(goLocation.latitude);
            latLng.setLongitude(goLocation.longitude);
            listOfLatLngs.add(latLng);
        }

        loadedMapStyle.addSource(new GeoJsonSource(MARKER_SOURCE, FeatureCollection.fromFeatures(features)));

        loadedMapStyle.addLayer(new SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE)
                .withProperties(
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconImage(MARKER_IMAGE),
                        PropertyFactory.textAnchor(Property.TEXT_ANCHOR_BOTTOM),
                        PropertyFactory.textField("Marker Title"),
                        PropertyFactory.iconOffset(new Float[] {0f, -52f})
                ));

        if(locationEngine != null){
            locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                @Override
                public void onSuccess(LocationEngineResult result) {
                    LatLng latLng = new LatLng();
                    latLng.setLongitude(result.getLastLocation().getLongitude());
                    latLng.setLatitude(result.getLastLocation().getLatitude());
                    listOfLatLngs.add(latLng);
                }
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getContext(),"Could not find last known location", Toast.LENGTH_SHORT);

                }
            });
        }
        LatLngBounds latLngBounds = new LatLngBounds.Builder().includes(listOfLatLngs).build();
        mapboxMap.easeCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100), 5000);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(locationListeningCallback);
        }

        mapView.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static MapsFragment newInstance(int index) {

        Bundle args = new Bundle();
        MapsFragment fragment = new MapsFragment();
        fragment.setArguments(args);
        args.putInt(PlaceholderFragment.ARG_SECTION_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return false;
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        setUpStyle();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){
            setUpLocationEngineAndCallBack();
            onMapReady(mapboxMap);
        }else{

        }

    }

    private static class LocationListeningCallback
            implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MapsFragment> fragmentWeakReference;

        LocationListeningCallback(MapsFragment fragment) {
            this.fragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {


        }

        @Override
        public void onFailure(@NonNull Exception exception) {


        }
    }

    private void setUpLocationEngineAndCallBack(){
        locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());
        locationListeningCallback = new LocationListeningCallback(this);

        long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
        long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;

        locationEngine = LocationEngineProvider.getBestLocationEngine(getContext());

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_NO_POWER)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME)
                .build();

        locationEngine.requestLocationUpdates(request, locationListeningCallback, getMainLooper());
        locationEngine.getLastLocation(locationListeningCallback);

    }

    private void enableLocationComponents(Style style){
        if(PermissionsManager.areLocationPermissionsGranted(getContext())) {
            LocationComponentOptions customLocationComponentOptions = LocationComponentOptions.builder(getContext())
                    .elevation(5)
                    .accuracyAlpha(.6f)
                    .accuracyColor(Color.GREEN)
                    .build();

            // Get an instance of the component
            locationComponent = mapboxMap.getLocationComponent();

            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(getContext(), style)
                            .locationComponentOptions(customLocationComponentOptions)
                            .build();

            // Activate with options
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        }

    }

    private void setUpStyle() {
        if (mapboxMap != null) {
            mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    style.addImage(MARKER_IMAGE, BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.custom_marker));
                    enableLocationComponents(style);
                    addMarkers(style);
                }
            });
        }

    }


}

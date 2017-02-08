package com.pacoperezgalan.geolocalitzacio_prova;


import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;


import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import static com.google.android.gms.analytics.internal.zzy.m;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Marker marcador;
    Double lat = 0.0;
    double lng = 0.0;
    LocationManager locationManager;
    Location location;

    GoogleApiClient mGoogleApiClient;

    Button sitios;
    Button vista;
    /*
    private List<Polyline> polylineList;
    private List<Marker> marcasOrigen;
    private List<Marker> marcasDestino;
    */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        sitios = (Button) findViewById(R.id.btn_seleccionarLugar);
        vista = (Button) findViewById(R.id.btn_vista);

// Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sitios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGooglePlaces();
            }
        });

        vista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    vista.setText("Vista normal");
                    comprovaConnexio();
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    vista.setText("Vista satelite");
                }
            }
        });


    }

    int PLACE_PICKER_REQUEST = 1;

    public void getGooglePlaces() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();


            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }else {
            mMap.setMyLocationEnabled(true);
        }

    }

    private void agregarMarcador(Double lat, Double lng) {
        LatLng coordenades = new LatLng(lat, lng);
        CameraUpdate mUbicacio = CameraUpdateFactory.newLatLngZoom(coordenades, 15);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenades)
                .title("La meua ubicacio")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_ubicacio)));
        mMap.animateCamera(mUbicacio);
    }

    private void actualitzarUbicacio(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Toast.makeText(this, "Localizacion chachi: " + lat + "   " + lng, Toast.LENGTH_SHORT).show();
            agregarMarcador(lat, lng);
        } else {

        }
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            actualitzarUbicacio(mLastLocation);
        } else {
            Toast.makeText(this, "onConnected: location null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //parte coger cosas de url api google/////////////////////////////////////////////////////////////////////////////

    protected boolean comprovaConnexio() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new ConectaURL().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=218f29d4bc4537f7215442077c55ee1502fc0a4a&location=725469.221836251905188,%204373239.299544908106327&radius=2000");
            return true;
        } else {
            /*
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.recyclerPost), "No hi ha connexió a la Xarxa", Snackbar.LENGTH_INDEFINITE);

            mySnackbar.setAction("Tornar a provar", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    xarxa = comprovaConnexio();
                }
            });
            mySnackbar.show();
            */
            return false;
        }
    }


    private class ConectaURL extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... url) {

            String documentJSON = conectaURL(url[0]);

            long tempsInicial = System.currentTimeMillis();

            //parsejaJSON(documentJSON);

            long tempsFinal = System.currentTimeMillis();

            return ((tempsFinal-tempsInicial)+" ms.");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //adaptadorRecyclerPost.notifyDataSetChanged();

        }
    }


    private String conectaURL(String llocAConnectar){
        URL url;
        String resposta=null;
        try {
            Log.d("tag","Iniciant la connexió: (");

            url = new URL(llocAConnectar);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();

            int response = conn.getResponseCode();
            Log.d("tag", "Rebent dades des del Servidor en streaming: ");

            InputStream is = new BufferedInputStream(conn.getInputStream());
            Log.d("tag","Convertint l'streaming en un String: ");

            resposta = converteixStreamAString(is);

            Log.d("tag","Resposta/////////////////////////////////////////////////////////////////////////////////////: ("+response+")"+resposta);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            return resposta;
        }
    }

    private String converteixStreamAString(InputStream is) {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    /*
    private void parsejaJSON(String documentJSON) {

        Log.d("tag", "Resposta2: " + documentJSON);

        if (documentJSON != null) {
            try {

                JSONArray jsonPosts = new JSONArray(documentJSON) ;

                for (int i = 0; i < jsonPosts.length(); i++) {
                    JSONObject jsonObj = jsonPosts.getJSONObject(i);

                    int id = jsonObj.getInt("id");
                    String title = jsonObj.getString("title");
                    String body = jsonObj.getString("body");





                    Post unPost = new Post();

                    unPost.setId(id);
                    unPost.setTitle(title);
                    unPost.setBody(body);

                    Log.d("tag",unPost.toString());
                    postList.add(unPost);
                }
            } catch (final JSONException e) {
                Log.e("tag", "Error parsejant Json: " + e.getMessage());
                Snackbar.make(findViewById(R.id.recyclerPost),
                        "Error parsejant Json", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Log.e("tag", "Error intentant rebre el Json.");
            Snackbar.make(findViewById(R.id.recyclerPost),
                    "Error intentant rebre el Json.", Snackbar.LENGTH_LONG).show();


        }

    }
    */

    /*
    private void meuaUbicacio() {

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No va la ubicacio ", Toast.LENGTH_SHORT).show();
            return;
        }

        locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            actualitzarUbicacio(location);
        } else {
            Toast.makeText(this, "meua Ubicacio: location null", Toast.LENGTH_SHORT).show();
        }
    }
*/
}

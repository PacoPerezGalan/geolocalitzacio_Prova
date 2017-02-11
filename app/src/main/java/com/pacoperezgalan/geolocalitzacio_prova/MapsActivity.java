package com.pacoperezgalan.geolocalitzacio_prova;



import android.Manifest;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Criteria;
import android.location.Geocoder;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
import java.net.URLConnection;
import java.util.ArrayList;

import it.sephiroth.android.library.picasso.Picasso;
import it.sephiroth.android.library.picasso.Target;

import static android.R.attr.bitmap;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.gms.analytics.internal.zzy.m;
import static com.google.android.gms.analytics.internal.zzy.n;
import static it.sephiroth.android.library.picasso.Picasso.with;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Marker marcador;
    Double lat = 0.0;
    double lng = 0.0;

    GoogleApiClient mGoogleApiClient;

    Button vista;

    ArrayList<Lugar> lugaresList;
    ArrayList<Place> placesList;
    ArrayList<Marker> markerArrayList;

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        vista = (Button) findViewById(R.id.btn_vista);
        lugaresList=new ArrayList<Lugar>();
        placesList=new ArrayList<Place>();
        markerArrayList=new ArrayList<Marker>();


        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(AppIndex.API).build();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        vista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    vista.setText("Vista normal");

                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    vista.setText("Vista satelite");
                }
            }
        });
    }


    public void pintarLugaresMapa(){
/*
        for(int i=0;i<lugaresList.size();i++){

            PendingResult<PlaceBuffer> place= Places.GeoDataApi.getPlaceById(mGoogleApiClient, lugaresList.get(i).getId())
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                final Place myPlace = places.get(0);
                                Log.i("tag", "Place found: " + myPlace.getName());
                            } else {
                                Log.e("tag", "Place not found");
                            }
                            places.release();
                        }
                    });



            placesList.add(place.await().get(0));
        }

        for(int i=0;i<placesList.size();i++){
            Bitmap bitmap= getBitmapFromURL(lugaresList.get(i).getIcon());
            Double lat=placesList.get(i).getLatLng().latitude;
            Double lng=placesList.get(i).getLatLng().longitude;
            agregarMarcador(lat,lng,bitmap);
        }
        */

        Bitmap bitmap;
        bitmap= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.icon_restaurant),100,100,false);

        for(int i=0;i<lugaresList.size();i++){


            Double lat=lugaresList.get(i).getLat();
            Double lng=lugaresList.get(i).getLng();
            LatLng coordenades = new LatLng(lat, lng);
            markerArrayList.add(mMap.addMarker(new MarkerOptions()
                    .position(coordenades)
                    .title(lugaresList.get(i).getName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap))));

            //new BitmapFromURL(lugaresList.get(i),mMap).execute(lugaresList.get(i).getIcon());
            //BitmapDescriptorFactory.fromResource(R.drawable.image_ubicacio))      BitmapDescriptorFactory.fromBitmap(icon))
        }

    }

    int razon;
    boolean botonUbicacionPulsado;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.setMyLocationEnabled(true);



        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int r) {
                razon =r;

            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(razon !=2 || botonUbicacionPulsado) {
                    mMap.clear();
                    lugaresList.clear();
                    markerArrayList.clear();
                    comprovaConnexio();
                }
                botonUbicacionPulsado=false;
                //Toast.makeText(getApplicationContext(),"menejant camara",Toast.LENGTH_SHORT).show();
            }
        });
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                botonUbicacionPulsado=true;
                return false;
            }

        });


    }



    private void actualitzarUbicacio(Location location) {
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            Toast.makeText(this, "Localizacion chachi: " + lat + "   " + lng, Toast.LENGTH_SHORT).show();
            //agregarMarcador(lat, lng);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 15));
        } else {

        }
    }





    //parte coger cosas de url api google/////////////////////////////////////////////////////////////////////////////

    protected boolean comprovaConnexio() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            new ConectaURL().execute("https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=AIzaSyB38CQ8uYvqAmZfTnXAjX1A3IPEYHun-9s&location="+mMap.getCameraPosition().target.latitude+","+mMap.getCameraPosition().target.longitude+"&rankby=distance&types=food|bar|restaurant");
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

            parsejaJSON(documentJSON);

            long tempsFinal = System.currentTimeMillis();

            return ((tempsFinal-tempsInicial)+" ms.");
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pintarLugaresMapa();
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

            Log.d("tag","Resposta/////////////////////: ("+response+")"+resposta);


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

    private void parsejaJSON(String documentJSON) {

        Log.d("tag", "Resposta2: " + documentJSON);

        if (documentJSON != null) {
            try {
                JSONObject jsonTodo = new JSONObject(documentJSON);
                JSONArray jsonResultados = jsonTodo.getJSONArray("results") ;

                for (int i = 0; i < jsonResultados.length(); i++) {
                    JSONObject jsonObj = jsonResultados.getJSONObject(i);

                    String place_id = jsonObj.getString("place_id");
                    String icon=jsonObj.getString("icon");
                    String name=jsonObj.getString("name");
                    Double lat=Double.parseDouble(jsonObj.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                    Double lng=Double.parseDouble(jsonObj.getJSONObject("geometry").getJSONObject("location").getString("lng"));

                    Lugar unLugar = new Lugar();

                    unLugar.setId(place_id);
                    unLugar.setIcon(icon);
                    unLugar.setName(name);
                    unLugar.setLat(lat);
                    unLugar.setLng(lng);

                    Log.d("tag",unLugar.getId().toString());
                    lugaresList.add(unLugar);
                }
            } catch (final JSONException e) {
                Log.e("tag", "Error parsejant Json: " + e.getMessage());
                //Snackbar.make(findViewById(R.id.recyclerPost), "Error parsejant Json", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Log.e("tag", "Error intentant rebre el Json.");
            //Snackbar.make(findViewById(R.id.recyclerPost), "Error intentant rebre el Json.", Snackbar.LENGTH_LONG).show();

        }

    }

    public static  class  BitmapFromURL extends AsyncTask<String,Void,Object>{
        Lugar unLugar;
        GoogleMap mMap;

        public BitmapFromURL(Lugar lloc,GoogleMap map){
            unLugar=lloc;
            mMap=map;
        }
        @Override
        protected Object doInBackground(String... src) {
            Log.d("tag","intentant agafar bitmap de url........:    "+src);
            Bitmap bitmap;
            try {
                URL url = new URL(src[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("tag","no sha pogut agarrar el bitmap   "+src);
                return null;
            }
        }
        @Override
        protected void onPostExecute(Object bitmap) {
            super.onPostExecute(bitmap);
            Double lat=unLugar.getLat();
            Double lng=unLugar.getLng();
            LatLng coordenades = new LatLng(lat, lng);
            mMap.addMarker(new MarkerOptions()
                    .position(coordenades)
                    .title(unLugar.getName())
                    .icon(BitmapDescriptorFactory.fromBitmap((Bitmap)bitmap)));
        }
    }














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

    /*
    private void agregarMarcador(Double lat, Double lng, Bitmap icon) {
        LatLng coordenades = new LatLng(lat, lng);
        CameraUpdate mUbicacio = CameraUpdateFactory.newLatLngZoom(coordenades, 15);
        if (marcador != null) marcador.remove();

        marcador = mMap.addMarker(new MarkerOptions()
                .position(coordenades)
                .title("La meua ubicacio")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_ubicacio)));
        //BitmapDescriptorFactory.fromResource(R.drawable.image_ubicacio))      BitmapDescriptorFactory.fromBitmap(icon))
        mMap.animateCamera(mUbicacio);
    }


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
**/
}

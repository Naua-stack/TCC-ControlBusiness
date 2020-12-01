package com.example.tcc;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapTracking extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private User user;
    private GoogleMap mMap;
    private Marker currentLocationMarker;
    private LatLng currentLocationLatLong;
    private DatabaseReference mdatabase;

    private ArrayList<Marker> tmpRealTimeMarkerS = new ArrayList<>();
    private ArrayList<Marker> realTimeMarkers = new ArrayList<>();
    private FirebaseDatabase database;
    private static final String USERS = "Usuários";
    private static final String USERS2 = "Empresas";
    private FirebaseAuth mAuth;
    private Address endereco;
    private String titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_tracking);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startGettingLocations();

        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference();
        BuscarUsuarios();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng criciúma = new LatLng(-28.689465, -49.380369);


        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(criciúma).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        String id = mAuth.getCurrentUser().getUid();
    }
        private void BuscarUsuarios() {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Usuários");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mMap.clear();
                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                        DataSnapshot productsSnapshot = requestSnapshot.child("Location");




                                LocationData maps = productsSnapshot.getValue(LocationData.class);
                               if(maps != null) {
                                   Double longitude = maps.getLongitude();
                                    Double latitude = maps.getLatitude();
                                   Log.e("longitude", String.valueOf(longitude));
                                    Log.e("latitude", String.valueOf(latitude));

                                    if(latitude != 0) {
                                        MarkerOptions markerOptions = new MarkerOptions();
                                        User user = requestSnapshot.getValue(User.class);
                                        markerOptions.title(user.getNome());
                                        markerOptions.snippet("Email:" +   user.getEmail  () + "         "  +  "Telefone:"+  user.getTelefone  ());


                                        markerOptions.position(new LatLng(latitude, longitude));
                                        mMap.addMarker(markerOptions);
                                        tmpRealTimeMarkerS.add(mMap.addMarker(markerOptions));

                                    }
                                }
                        realTimeMarkers.addAll(tmpRealTimeMarkerS);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }





    @Override
    public void onLocationChanged(Location location) {

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        //Add marker

        currentLocationLatLong = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(currentLocationLatLong);

        markerOptions.title("Sua Localização");
        markerOptions.snippet("Aqui está você");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);

        //Move to new location
        CameraPosition cameraPosition = new CameraPosition.Builder().zoom(15).target(currentLocationLatLong).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        LocationData locationData = new LocationData(location.getLatitude(), location.getLongitude());

        Map<String, Object> latlang = new HashMap<>();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        try {
            endereco = buscarendereco(latitude, longitude);


        } catch (IOException e) {
            e.printStackTrace();
        }

        latlang.put("latitude", endereco.getLatitude());
        latlang.put("longitude", endereco.getLongitude());
        latlang.put("País", endereco.getCountryName());
        latlang.put("Estado", endereco.getAdminArea());
        latlang.put("Cidade", endereco.getSubAdminArea());
        latlang.put("Bairro", endereco.getSubLocality());
        latlang.put("Rua", endereco.getThoroughfare());
        latlang.put("CEP", endereco.getPostalCode());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String id = mAuth.getCurrentUser().getUid();


        mdatabase.child("Usuários").child(id).child("Location").setValue(latlang);


    }


    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS desativado!");
        alertDialog.setMessage("Ativar GPS?");
        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }


    @SuppressLint("MissingPermission")
    private void startGettingLocations() {

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetwork = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean canGetLocation = true;
        int ALL_PERMISSIONS_RESULT = 101;
        long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;// Distance in meters
        long MIN_TIME_BW_UPDATES = 1000 * 10;// Time in milliseconds

        ArrayList<String> permissions = new ArrayList<>();
        ArrayList<String> permissionsToRequest;

        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        //Check if GPS and Network are on, if not asks the user to turn on
        if (!isGPS && !isNetwork) {
            showSettingsAlert();
        } else {
            // check permissions

            // check permissions for later versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    canGetLocation = false;
                }
            }
        }


        //Checks if FINE LOCATION and COARSE Location were granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
            return;
        }

        //Starts requesting location updates
        if (canGetLocation) {
            if (isGPS) {
                lm.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            } else if (isNetwork) {
                // from Network Provider

                lm.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

            }
        } else {
            Toast.makeText(this, "Não é possível obter a localização", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class UserItem extends Item<ViewHolder> {
        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView titulo = (TextView) findViewById(R.id.titulo);
            titulo.setText(user.getNome());
        }

        @Override
        public int getLayout() {

            return R.layout.custom_info_maps;
        }
    }

    public Address buscarendereco(double latitude, double longitude)
            throws IOException {
        Geocoder geocoder;
        Address address = null;
        List<Address> adddresses;
        geocoder = new Geocoder(getApplicationContext());
        adddresses = geocoder.getFromLocation(latitude, longitude, 1);
        if (adddresses.size() > 0) {
            address = adddresses.get(0);
        }
        return address;
    }


}

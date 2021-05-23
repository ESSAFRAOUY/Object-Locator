package com.example.objectlocator;

import android.location.Location;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MapView mapview;
    List<Location> savedLocations;
    private List<ObjectModel> allObjects;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //pour lier la classe AddFragment avec le layout fragment_add
        View view= inflater.inflate(R.layout.fragment_map,container,false);
        MyApplication myApplication=(MyApplication)getActivity().getApplicationContext();
        savedLocations=myApplication.getMyLocations();
        //code ajouté
        // pour récuperer les objets qu'on a dans la base de données
        MydataBase mydataBase =new MydataBase(getContext());
        allObjects=mydataBase.getAllObjects();
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapview = (MapView)view.findViewById(R.id.mapview);
        if (mapview != null) {
            mapview.onCreate(null);
            mapview.onResume();
            mapview.getMapAsync(this);
        }
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
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        LatLng lastLocationPlaced=sydney;

        //la boucle suivante pour afficher tous les objets qu'on a collecté sur la map.
        for (ObjectModel objectModel:allObjects
        ) {
            float latitude=Float.parseFloat(objectModel.getLatitude());
            float longitude=Float.parseFloat(objectModel.getLongitude());
            LatLng latLng=new LatLng(latitude,longitude);
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Name:"+objectModel.getNom()+" Lat:"+objectModel.getLatitude()+" Long:"+objectModel.getLongitude());
            mMap.addMarker(markerOptions);
            lastLocationPlaced=latLng;

        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLocationPlaced,12.0f));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                // the number of times the pin is clicked
                Integer clicks= (Integer) marker.getTag();
                if (clicks==null){
                    clicks=0;
                }
                clicks++;
                marker.setTag(clicks);
                //Toast.makeText(getContext(),"marker "+marker.getTitle()+" was clicked "+marker.getTag()+" times",Toast.LENGTH_SHORT).show();

                return false;
            }
        });

    }

// création d'un menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.maps_menu, menu);
        setHasOptionsMenu(true);
    }


//pour afficher une map selon le choix d'utilisateur
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        //change the map type based on the user's selection
        switch (item.getItemId()){
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            default:
                return super.onOptionsItemSelected(item); }
    }
}
